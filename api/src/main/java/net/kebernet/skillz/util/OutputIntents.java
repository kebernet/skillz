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
package net.kebernet.skillz.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.annotation.Intent;
import net.kebernet.skillz.annotation.Slot;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * Thi class takes an IntrospectionData object and outputs the appropriate intents/slots configuration
 * for the Alexa console.
 */
public class OutputIntents {

    private final IntrospectionData data;

    public OutputIntents(IntrospectionData data) {
        this.data = data;
    }

    /**
     * Writes the utterances to a writer
     * @param writer Target writer.
     */
    public void writeTo(PrintWriter writer) {
        try {
            writer.println(build());
            writer.flush();
        } catch (Exception e){
            throw new SkillzException("Failed to write json" ,e);
        }
    }

    /**
     * Writes the utterances to an output stream
     * @param stream The stream to write to.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public void writeTo(OutputStream stream){
        writeTo(new PrintWriter(new OutputStreamWriter(stream, Charset.forName("UTF-8"))));
    }

    /**
     * Builds up a bunch of JSON using maps and Jackson.
     * @return A String containing the Intents JSON for the class.
     * @throws JsonProcessingException Thrown if it can't serialize for whatever reason.
     */
    String build() throws JsonProcessingException {
        LinkedHashMap<String, LinkedHashMap<String, String>> mapOfIntentsToSlots = new LinkedHashMap<>();

        data.getMethods()
                .forEach( m -> {
                    if(m.getNativeMethod().getAnnotation(Intent.class) == null) {
                        return;
                    }
                    HashMap<String, String> slots = getOrCreateIntentSlots(mapOfIntentsToSlots, m.getName());
                    m.getParameters()
                            .forEach( p -> {
                                Slot s = p.getParameter().getAnnotation(Slot.class);
                                if(s != null){
                                    String oldType = slots.get(s.name());
                                    if(oldType != null && !oldType.equals(s.type())){
                                        throw new SkillzException("Mismatched declarations for slot "+s.name()+" "+oldType+" vs "+s.type());
                                    } else if(oldType == null){
                                        slots.put(s.name(), s.type());
                                    }
                                }
                            });
                });

        HashMap<String, Object> result = new HashMap<>();
        result.put("intents",
            mapOfIntentsToSlots.entrySet()
                    .stream()
                    .map(entry ->{
                        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                        map.put("intent", entry.getKey());
                        map.put("slots", entry.getValue().entrySet()
                                .stream()
                                .map(slotEntry -> {
                                    LinkedHashMap<String, String> slot = new LinkedHashMap<>();
                                    slot.put("name", slotEntry.getKey());
                                    slot.put("type", slotEntry.getValue());
                                    return slot;
                                }).collect(Collectors.toList())
                        );
                        return map;
                    }).collect(Collectors.toList())
        );
        return  new ObjectMapper().writer().writeValueAsString(result);
    }

    private HashMap<String, String> getOrCreateIntentSlots(LinkedHashMap<String, LinkedHashMap<String, String>> mapOfIntentsToSlots,
                                                           String name) {
        HashMap<String, String> result = mapOfIntentsToSlots.get(name);
        if(result != null) return result;
        LinkedHashMap<String, String> create = new LinkedHashMap<>();
        mapOfIntentsToSlots.put(name, create);
        return create;
    }
}
