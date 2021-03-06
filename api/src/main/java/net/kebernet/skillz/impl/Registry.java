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

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.kebernet.invoker.runtime.Invoker;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.annotation.ExpressionValue;
import net.kebernet.skillz.annotation.Intent;
import net.kebernet.skillz.annotation.Launched;
import net.kebernet.skillz.annotation.SessionEnded;
import net.kebernet.skillz.annotation.SessionStarted;
import net.kebernet.skillz.annotation.Skill;
import net.kebernet.skillz.annotation.Slot;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *  This is the class that managed discovery and metadata for the Skill implementations.
 *
 */
@Singleton
public class Registry {

    private static final Logger LOGGER = Logger.getLogger(Registry.class.getCanonicalName());
    private final Invoker invoker;
    private final Map<String, Class<?>> pathsToClasses;


    public Registry(Set<Class<?>> types){
        this.invoker = new Invoker(Registry::createMethodName, Registry::createParameterName);
        this.pathsToClasses = new HashMap<>(types.size());
        init(types);
    }

    /**
     * Default constructor that uses Reflections to search for skills on the
     * classpath.
     */
    @Inject
    public Registry(){
        LOGGER.fine("Beginning scan for skills.");
        HashSet<Class<?>> types = new HashSet<>();
        new FastClasspathScanner().matchClassesWithAnnotation(Skill.class, types::add)
                .scan();
        this.invoker = new Invoker(Registry::createMethodName, Registry::createParameterName);
        this.pathsToClasses = new HashMap<>(types.size());
        LOGGER.fine("Scan for skills complete.");
        init(types);
    }

    private void init(Set<Class<?>> types) {
        StringBuilder sb = new StringBuilder();
        types.forEach(t->sb.append(t.getCanonicalName()).append(' '));
        LOGGER.info("Introspecting: "+sb.toString());
        types.forEach(this.invoker::registerType);
        types.forEach(this::validateType);
        LOGGER.info("Skill introspection complete. Found "+types.size()+" skills.");
        types.forEach((c)-> {
            String path = c.getAnnotation(Skill.class).path();
            if(!path.startsWith("/")){
                path = "/"+path;
            }
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

    @SuppressWarnings("WeakerAccess")
    public Invoker getInvoker(){
        return this.invoker;
    }

    @SuppressWarnings("WeakerAccess")
    public static String createParameterName(Parameter p){
        //noinspection unchecked
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

    @SuppressWarnings("WeakerAccess")
    static String createMethodName(Method m){
        //noinspection unchecked
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
            return SessionStarted.class.getSimpleName();
        }

        SessionEnded sessionEnded = m.getAnnotation(SessionEnded.class);
        if(sessionEnded != null){
            return SessionEnded.class.getSimpleName();
        }

        Launched launched = m.getAnnotation(Launched.class);
        if(launched != null){
            return Launched.class.getSimpleName();
        }

        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    static boolean checkMaxOneOf(AnnotatedElement target, Class<? extends Annotation>... annotations){
        int count = 0;
        for(@SuppressWarnings("unchecked") Class<? extends Annotation> type : annotations == null ? new Class[0] : annotations){
            count += target.getAnnotation(type) != null ? 1 : 0;
            if(count > 1){
                return false;
            }
        }
        return true;

    }
}
