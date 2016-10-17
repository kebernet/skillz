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

import net.kebernet.invoker.runtime.Invoker;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.annotation.Intent;
import net.kebernet.skillz.annotation.Launched;
import net.kebernet.skillz.annotation.ExpressionValue;
import net.kebernet.skillz.annotation.SessionEnded;
import net.kebernet.skillz.annotation.SessionStarted;
import net.kebernet.skillz.annotation.Skill;
import net.kebernet.skillz.annotation.Slot;
import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by rcooper on 10/15/16.
 */
@Singleton
public class Registry {

    private static final Logger LOGGER = Logger.getLogger(Registry.class.getCanonicalName());
    private final Invoker invoker;
    private final Map<String, Class<?>> pathsToClasses;

    @Inject
    public Registry(){
        LOGGER.fine("Beginning scan for skills.");
        Reflections reflections = new Reflections(Thread.currentThread().getContextClassLoader());
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(Skill.class);
        LOGGER.fine("Scan for skills complete.");
        this.invoker = new Invoker(Registry::createMethodName, Registry::createParameterName);
        types.forEach(this.invoker::registerType);
        types.forEach(this::validateType);
        LOGGER.fine("Skill introspection complete. Found "+types.size()+" skills.");
        pathsToClasses = new HashMap<>(types.size());
        types.forEach((c)-> {
            String path = c.getAnnotation(Skill.class).path();
            Class<?> previous = pathsToClasses.put(path, c);
            if(previous != null){
                // TODO, allow multiple implemetnations to handle different intents.
                throw new SkillzException("Both "+previous.getCanonicalName()+" and "+
                    c.getCanonicalName()+" are attempting to register for '"+path+"'");
            }
        });
    }

    private void validateType(Class<?> aClass) {

    }

    public Optional<IntrospectionData> getDataForPath(String path){
        return Optional.ofNullable(pathsToClasses.get(path))
                .map(invoker::lookupType);
    }

    public Set<IntrospectionData> getAllIntrospectionData(){
        return this.pathsToClasses.values().stream()
                .map(invoker::lookupType)
                .collect(Collectors.toSet());
    }

    public Invoker getInvoker(){
        return this.invoker;
    }

    public static String createParameterName(Parameter p){
        if(!checkMaxOneOf(p, Slot.class, ExpressionValue.class)){
            throw new SkillzException(p.getType()+" "+p.getName()+" has more than a single value annotation on it.");
        }
        Slot slot = p.getAnnotation(Slot.class);
        if(slot != null){
            return "slot."+slot.name();
        }
        ExpressionValue requestValue = p.getAnnotation(ExpressionValue.class);
        if(requestValue != null){
            return requestValue.value();
        }

        return null;
    }

    static String createMethodName(Method m){
        if(!checkMaxOneOf(m, Intent.class, SessionEnded.class, SessionStarted.class, Launched.class)){
            throw new SkillzException(m.getDeclaringClass().getCanonicalName()+"."+m.getName()+
                    " has more than a single operational annotation on it.");
        }
        Intent i = m.getAnnotation(Intent.class);
        if(i != null){
            return i.value();
        }

        SessionStarted sessionStarted = m.getAnnotation(SessionStarted.class);
        if(sessionStarted != null){
            return sessionStarted.getClass().getSimpleName();
        }

        SessionEnded sessionEnded = m.getAnnotation(SessionEnded.class);
        if(sessionEnded != null){
            return sessionEnded.getClass().getSimpleName();
        }

        Launched launched = m.getAnnotation(Launched.class);
        if(launched != null){
            return Launched.class.getSimpleName();
        }

        return null;
    }

    static boolean checkMaxOneOf(AnnotatedElement target, Class<? extends Annotation>... annotations){
        int count = 0;
        for(Class<? extends Annotation> type : annotations){
            count += target.getAnnotation(type) != null ? 1 : 0;
            if(count > 1){
                return false;
            }
        }
        return true;

    }
}
