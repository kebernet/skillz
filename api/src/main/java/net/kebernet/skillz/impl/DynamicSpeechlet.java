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
import net.kebernet.skillz.annotation.ExpressionValue;
import net.kebernet.skillz.annotation.Launched;
import net.kebernet.skillz.annotation.ResponseFormatter;
import net.kebernet.skillz.annotation.SessionEnded;
import net.kebernet.skillz.annotation.SessionStarted;
import net.kebernet.skillz.annotation.Skill;
import net.kebernet.skillz.annotation.Slot;
import net.kebernet.skillz.util.Coercion;
import ognl.Ognl;
import ognl.OgnlException;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * This is a Speechlet subclass that will delegate to a Skill annotated pojo.
 */
public class DynamicSpeechlet implements Speechlet {
    private static final Logger LOGGER = Logger.getLogger(DynamicSpeechlet.class.getCanonicalName());
    private static final Coercion COERCION = new Coercion();
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

    @SuppressWarnings("WeakerAccess")
    public IntrospectionData getData() {
        return data;
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
            LOGGER.log(Level.SEVERE, "No handler available for '" + name + "' on path " + ((Skill) data.getType().getAnnotation(Skill.class)).path());
            throw new SpeechletException("No handler available for '" + name+"'");
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

    /**
     * Invokes a method and creates a speechlet response.
     * @param method The method to invoke
     * @param values The parameter values to use.
     */
    @SuppressWarnings("unchecked")
    private SpeechletResponse invokeResponseEvent(InvokableMethod method, List<ParameterValue> values) {
        try {
            ResponseFormatter declaredFormatter = method.getNativeMethod().getAnnotation(ResponseFormatter.class);
            Object result = registry.getInvoker().invoke(implementation, method, values);
            if (result instanceof SpeechletResponse) {
                return (SpeechletResponse) result;
            } else if(declaredFormatter != null) {
                return (SpeechletResponse) typeFactory.create(declaredFormatter.value()).apply(result);
            } else {
                return (SpeechletResponse) responseMapper.findMappingFunction(result.getClass()).apply(result);
            }
        } catch (InvokerException e) {
            LOGGER.log(Level.WARNING, "Exception invoking method: "+method.getNativeMethod().getName()+" ("+method.getName()+")", e);
            throw new SkillzException("Unable to evaluate method : " + method, e);
        }
    }


    private <T extends SpeechletRequest> void handleVoidEvent(Class<? extends Annotation> annotation, T request, Session session) {
        List<InvokableMethod> handlers = methods.get(annotation.getSimpleName());
        if (handlers == null || handlers.isEmpty()) {
            return;
        }
        if (handlers.size() == 1) {
            InvokableMethod method = handlers.iterator().next();
            List<ParameterValue> values = synthesizeValues(data, method, request, session);
            invokeVoidEvent(method, values);
        } else {
            MethodEvaluation evaluation = findMethodEvaluation(request, session, handlers);
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

    private MethodEvaluation findMethodEvaluation(SpeechletRequest request, Session session, List<InvokableMethod> methods) {
        return methods.stream()
                .map(m -> {
                    List<ParameterValue> possibleArguments = synthesizeValues(data, m, request, session);
                    int score = m.matchValue(m.getName(), possibleArguments);
                    return new MethodEvaluation(m, possibleArguments, score);
                })
                .filter(m->
                        m.score >= 0)
                .sorted()
                .findFirst()
                .orElseThrow(() -> new SkillzException("Unable to deal with methods: " + methods));
    }

    /**
     * This method creates a synthesized list of ParameterValues to use as possible invocation arguments
     * @param data Introspection data to use.
     * @param method The method we might want to invoke
     * @param request The SpeechletRequest to use.
     * @param session The session to use.
     * @param <T> The type of speechlet request.
     * @return A List of parameter values evaluated from the request and session context.
     */
    @SuppressWarnings("WeakerAccess")
    static <T extends SpeechletRequest> List<ParameterValue> synthesizeValues(IntrospectionData data, InvokableMethod method,
                                                                              T request, Session session) {
        // Create the ExpressionValue context.
        Map<String, Object> context = new HashMap<>(2);
        context.put("session", session);
        context.put("request", request);

        final ArrayList<ParameterValue> parameterValues = new ArrayList<>();
        method.getParameters()
        .stream()
        .flatMap(param -> {
            Slot slotAnnotation = param.getParameter().getAnnotation(Slot.class);
            if (slotAnnotation != null) {
                try {
                    IntentRequest ir = (IntentRequest) request;
                    com.amazon.speech.slu.Slot slot = ir.getIntent().getSlot(slotAnnotation.name());
                    return slot == null ? Stream.empty() :
                            Stream.of(new ParameterValue(param.getName(), COERCION.coerce(slot.getValue(), param.getType())));
                } catch (ClassCastException e) {
                    throw new SkillzException(data.getType().getCanonicalName() + "." + method.getNativeMethod().getName() + " cannot declare a slot unless " +
                            "it is handling and IntentRequest.");
                }
            }
            ExpressionValue requestValue = param.getParameter().getAnnotation(ExpressionValue.class);
            if (requestValue != null) {
                Object value = evaluate(context, requestValue.value());
                return Stream.of(new ParameterValue(param.getName(), COERCION.coerce(value, param.getType())));
            }
            return Stream.empty();
        })
        .forEach(parameterValues::add);
        return parameterValues;
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

        public int compareTo(@Nullable Object o) {
            if(o == null){
                return -1;
            }
            MethodEvaluation that = (MethodEvaluation) o;
            if (this.score == 0 && that.score == 0) {
                int values = Integer.compare(that.values.size(), this.values.size());
                if(values == 0){
                    int delta =  Integer.compare(
                            Math.abs(this.method.getParameters().size() - this.values.size()),
                            Math.abs(that.method.getParameters().size() - that.values.size()));
                    if(delta == 0) {
                        return Integer.compare(this.method.getParameters().size(), that.method.getParameters().size());
                    } else {
                        return delta;
                    }
                } else {
                    return values;
                }
            } else {
                return Integer.compare(that.score, this.score);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodEvaluation)) return false;

            MethodEvaluation that = (MethodEvaluation) o;

            return score == that.score && (
                    method != null ?
                            method.equals(that.method) :
                            that.method == null &&
                            (values != null ?
                                    values.equals(that.values) :
                                    that.values == null));
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
