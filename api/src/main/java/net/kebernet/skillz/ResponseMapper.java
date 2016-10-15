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

import com.amazon.speech.speechlet.SpeechletResponse;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Created by rcooper on 10/15/16.
 */
public class ResponseMapper {

    private ConcurrentHashMap<Class<?>, Function<? extends Object, SpeechletResponse>> mappers = new ConcurrentHashMap<>();

    public ResponseMapper(){
    }

    public <T> void addMappingFunction(Class<T> type, Function<T, SpeechletResponse> mappingFunction){
        this.mappers.put(type, mappingFunction);
    }

    public Function<Object, SpeechletResponse> findMappingFunction(Class<?> type){
        return (Function<Object, SpeechletResponse>) Optional.ofNullable(mappers.get(type))
                .orElseThrow(()-> new SkillzException("Could not find mapping function to convert "+type.getCanonicalName()+" to a SpeechletResponse"));
    }
}
