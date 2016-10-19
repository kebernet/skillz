/*
 *    Copyright (c) 2016 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.kebernet.skillz.impl;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.google.common.collect.ArrayListMultimap;
import net.kebernet.invoker.runtime.InvokerException;
import net.kebernet.invoker.runtime.ParameterValue;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.invoker.runtime.impl.InvokableMethod;
import net.kebernet.skillz.FormatterMappings;
import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.TypeFactory;
import net.kebernet.skillz.annotation.Launched;
import net.kebernet.skillz.annotation.ExpressionValue;
import net.kebernet.skillz.annotation.ResponseFormatter;
import net.kebernet.skillz.annotation.SessionEnded;
import net.kebernet.skillz.annotation.SessionStarted;
import net.kebernet.skillz.annotation.Skill;
import net.kebernet.skillz.annotation.Slot;
import net.kebernet.skillz.util.Coercion;
import ognl.Ognl;
import ognl.OgnlException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This is a Speechlet subclass that will delegate to a Skill annotated pojo.
 */
public class DynamicSpeechlet implements Speechlet {
    private static final Logger LOGGER = Logger.getLogger(DynamicSpeechlet.class.getCanonicalName());
    private static final Coercion coersion = new Coercion();
    private final ArrayListMultimap<String, InvokableMethod> methods;
    private final IntrospectionData data;
    private final Registry registry;
    private final Object implementation;
    private final FormatterMappings responseMapper;
    private final TypeFactory typeFactory;

    public DynamicSpeechlet(ArrayListMultimap<String, InvokableMethod> methods, IntrospectionData data, FormatterMappings responseMapper, Registry registry, Object implementation, TypeFactory typeFactory) {
        this.methods = methods;
        this.data = data;
        this.registry = registry;
        this.implementation = implementation;
        this.responseMapper = responseMapper;
        this.typeFactory = typeFactory;
    }

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        handleVoidEvent(SessionStarted.class, request, session);
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        return handleResponseEvent(Launched.class.getSimpleName(), request, session);
    }


    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        return handleResponseEvent(request.getIntent().getName(), request, session);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        handleVoidEvent(SessionEnded.class, request, session);
    }

    private <T extends SpeechletRequest> SpeechletResponse handleResponseEvent(String name, T request, Session session) throws SpeechletException {
        List<InvokableMethod> started = methods.get(name);
        if (started == null || started.isEmpty()) {
            LOGGER.log(Level.SEVERE, "No handler available for " + name + " on path " + ((Skill) data.getType().getAnnotation(Skill.class)).path());
            throw new SpeechletException("No hanlder available for " + name);
        }
        if (started.size() == 1) {
            InvokableMethod method = started.iterator().next();
            List<ParameterValue> values = synthesizeValues(data, method, request, session);
            return invokeResponseEvent(method, values);
        } else {
            MethodEvaluation evaluation = findMethodEvaluation(request, session, started);
            return invokeResponseEvent(evaluation.method, evaluation.values);
        }
    }

    @SuppressWarnings("unchecked")
    private SpeechletResponse invokeResponseEvent(InvokableMethod method, List<ParameterValue> values) {
        try {
            ResponseFormatter declaredFormatter = method.getNativeMethod().getAnnotation(ResponseFormatter.class);
            Object result = registry.getInvoker().invoke(implementation, method.getName(), values);
            if (result instanceof SpeechletResponse) {
                return (SpeechletResponse) result;
            } else if(declaredFormatter != null) {
                return (SpeechletResponse) typeFactory.create(declaredFormatter.value()).apply(result);
            } else {
                return (SpeechletResponse) responseMapper.findMappingFunction(result.getClass()).apply(result);
            }
        } catch (InvokerException e) {
            throw new SkillzException("Unable to sevaluate method : " + method, e);
        }
    }


    private <T extends SpeechletRequest> void handleVoidEvent(Class<? extends Annotation> annotation, T request, Session session) {
        List<InvokableMethod> started = methods.get(annotation.getSimpleName());
        if (started == null || started.isEmpty()) {
            return;
        }
        if (started.size() == 1) {
            InvokableMethod method = started.iterator().next();
            List<ParameterValue> values = synthesizeValues(data, method, request, session);
            invokeVoidEvent(method, values);
        } else {
            MethodEvaluation evaluation = findMethodEvaluation(request, session, started);
            invokeVoidEvent(evaluation.method, evaluation.values);
        }
    }

    private void invokeVoidEvent(InvokableMethod method, List<ParameterValue> values) {
        try {
            Object result = registry.getInvoker().invoke(implementation, method.getName(), values);
            if (result != Void.class) {
                LOGGER.info("Non-void return from " + method.getName() + " method : " + method);
            }
        } catch (InvokerException e) {
            throw new SkillzException("Unable to evaluate method : " + method, e);
        }
    }

    private MethodEvaluation findMethodEvaluation(SpeechletRequest request, Session session, List<InvokableMethod> started) {
        return started.stream()
                .map(m -> {
                    List<ParameterValue> possibleArguments = synthesizeValues(data, m, request, session);
                    return new MethodEvaluation(m, possibleArguments, m.matchValue(m.getName(), possibleArguments));
                })
                .sorted()
                .findFirst()
                .orElseThrow(() -> new SkillzException("Unable to deal with methods: " + started));
    }

    static <T extends SpeechletRequest> List<ParameterValue> synthesizeValues(IntrospectionData data, InvokableMethod method,
                                                                              T request, Session session) {
        Map<String, Object> context = new HashMap<>(2);
        context.put("session", session);
        context.put("request", request);
        ArrayList<ParameterValue> values = method.getParameters()
                .stream()
                .map(param -> {
                    Slot slotAnnotation = param.getParameter().getAnnotation(Slot.class);
                    if (slotAnnotation != null) {
                        try {
                            IntentRequest ir = (IntentRequest) request;
                            com.amazon.speech.slu.Slot slot = ir.getIntent().getSlot(slotAnnotation.name());
                            return slot == null ? null : new ParameterValue(param.getName(), coersion.coerce(slot.getValue(), param.getType()));
                        } catch (ClassCastException e) {
                            throw new SkillzException(data.getType().getCanonicalName() + "." + method.getNativeMethod().getName() + " cannot declare a slot unless " +
                                    "it is handling and IntentRequest.");
                        }
                    }
                    ExpressionValue requestValue = param.getParameter().getAnnotation(ExpressionValue.class);
                    if (requestValue != null) {
                        return new ParameterValue(param.getName(), evaluate(context, requestValue.value()));
                    }
                    return null;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return values;
    }

    private static Object evaluate(Map<String, Object> context, String expression) {
        Object expr;
        try {
            expr = Ognl.parseExpression(expression);
        } catch (OgnlException e) {
            throw new SkillzException("Failed to parse '" + expression + "'", e);
        }
        Object value;
        try {
            value = Ognl.getValue(expr, context);
        } catch (OgnlException e) {
            throw new SkillzException("Failed to evaluate " + expr + " with context " + context, e);
        }

        return value;
    }

    private static class MethodEvaluation implements Comparable {
        final InvokableMethod method;
        final List<ParameterValue> values;
        final int score;

        private MethodEvaluation(InvokableMethod method, List<ParameterValue> values, int score) {
            this.method = method;
            this.values = values;
            this.score = score;
        }

        @Override
        public int compareTo(Object o) {
            MethodEvaluation that = (MethodEvaluation) o;
            if (this.score == 0 && that.score == 0) {
                return Integer.compare(that.method.getRequiredParameterCount(), this.method.getRequiredParameterCount());
            } else {
                return Integer.compare(that.score, this.score);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodEvaluation)) return false;

            MethodEvaluation that = (MethodEvaluation) o;

            if (score != that.score) return false;
            if (method != null ? !method.equals(that.method) : that.method != null) return false;
            return values != null ? values.equals(that.values) : that.values == null;
        }

        @Override
        public int hashCode() {
            int result = method.hashCode();
            result = 31 * result + values.hashCode();
            result = 31 * result + score;
            return result;
        }
    }
}
