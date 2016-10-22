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
import com.amazon.speech.ui.PlainTextOutputSpeech;
import net.kebernet.skillz.builder.PlainTextOutputBuilder;
import org.junit.Test;


import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class FormatterMappingsTest {

    @Test
    public void addMappingFunctionProvider() throws Exception {
        FormatterMappings mappings = new FormatterMappings();
        mappings.addMappingFunctionProvider(String.class, ()->{
            Formatter f = (String, SpeechletRequest, Session)-> SpeechletResponse.newTellResponse(
                PlainTextOutputBuilder.withText("foo").build()
            );
            return f;
        } );
        SpeechletResponse response = (SpeechletResponse) mappings.findMappingFunction(String.class).apply("bar", null, null);
        assertEquals("foo", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());
    }

    @Test
    public void addMappingFunction() throws Exception {
        FormatterMappings mappings = new FormatterMappings();
        mappings.addMappingFunction(String.class,  (String, SpeechletRequest, Session)-> SpeechletResponse.newTellResponse(
                PlainTextOutputBuilder.withText("foo").build()
        ));
        SpeechletResponse response = mappings.findMappingFunction(String.class).apply("bar", null, null);
        assertEquals("foo", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());
    }

    @Test(expected = SkillzException.class)
    public void addMappingFunctionMiss() throws Exception {
        FormatterMappings mappings = new FormatterMappings();
        mappings.addMappingFunction(Integer.class,  (String, SpeechletRequest, Session)-> SpeechletResponse.newTellResponse(
                PlainTextOutputBuilder.withText("foo").build()
        ));
        mappings.findMappingFunction(String.class).apply("bar", null, null);

    }

    @Test(expected = SkillzException.class)
    public void addMappingFunctionProviderMiss() throws Exception {
        FormatterMappings mappings = new FormatterMappings();
        mappings.addMappingFunctionProvider(Integer.class, ()->{
            Formatter f = (String, SpeechletRequest, Session)-> SpeechletResponse.newTellResponse(
                    PlainTextOutputBuilder.withText("foo").build()
            );
            return f;
        } );
        mappings.findMappingFunction(String.class).apply("bar", null, null);
    }
}