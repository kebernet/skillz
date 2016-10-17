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
package net.kebernet.skillz;

import javax.inject.Provider;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that contains default Formatter mappings per class type.
 */
public class FormatterMappings {

    private ConcurrentHashMap<Class<?>, Provider<Formatter<?>>> mappers = new ConcurrentHashMap<>();

    public FormatterMappings(){
    }

    /**
     * Adds a {@link javax.inject.Provider} to a Formatter for a given return type. This
     * can be useful if you wish to have dependencies injected into your formatter.
     * @param type A class reference for the return type to format.
     * @param provider A Provider that returns an instance of the Formatter for the type.
     */
    public void addMappingFunctionProvider(Class<?> type, Provider<Formatter<?>> provider){
        if(this.mappers.put(type, provider) != null){
            throw new SkillzException("You have attempted to register two formatters for the type "+
                type.getCanonicalName());
        }
    }

    /**
     * Adds a direct reference to a formatter instance for a type.
     * @param type Class reference of the type to format.
     * @param formatter The formatter for the given type.
     */
    public void addMappingFunction(Class<?> type, Formatter<?> formatter){
        if(this.mappers.put(type, ()->formatter) != null){
            throw new SkillzException("You have attempted to register two formatters for the type "+
                    type.getCanonicalName());
        }
    }

    /**
     * Looks up the formatter for the given type and returns an instance of it.
     * @param type Class reference of the type to format.
     * @return A formatter to convert the type to a SpeechletResponse.
     */
    @SuppressWarnings("unchecked")
    public  Formatter findMappingFunction(Class<?> type){
        return Optional.ofNullable(mappers.get(type))
                .orElseThrow(()-> new SkillzException("Could not find mapping function to convert "+type.getCanonicalName()+" to a SpeechletResponse"))
                .get();
    }
}
