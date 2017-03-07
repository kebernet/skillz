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
package net.kebernet.skillz.format;

import com.amazon.speech.speechlet.Application;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.User;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class MustacheBundleTest {
    private static final HashMap<String, Object> ATTS = new HashMap<>();
    @SuppressWarnings("deprecation")
    private static final Session SESSION = Session.builder()
            .withApplication(new Application("foo"))
            .withUser(new User("foo@foo.com"))
            .withSessionId("bar")
            .withIsNew(true)
            .withAttributes(ATTS)
            .build();

    @Test
    public void testSimpleText(){
        MustacheBundle bundle = new MustacheBundle("/simpletest", "en");
        OutputSpeech speech = bundle.createOutputSpeech(
                Arrays.asList("one", "two", "three"),
                mock(IntentRequest.class), SESSION);
        assertTrue(speech instanceof PlainTextOutputSpeech);
        assertEquals("List: one two three", ((PlainTextOutputSpeech) speech).getText().trim());
    }


    @Test
    public void testLanguageSwitch(){
        MustacheBundle bundle = new MustacheBundle("/langswitch", "en");
        OutputSpeech speech = bundle.createOutputSpeech("Robert", mock(IntentRequest.class), SESSION);
        assertTrue(speech instanceof PlainTextOutputSpeech);
        assertEquals("Hello Robert", ((PlainTextOutputSpeech) speech).getText().trim());
        bundle = new MustacheBundle("/langswitch", "es");
        speech = bundle.createOutputSpeech("Roberto", mock(IntentRequest.class), SESSION);
        assertTrue(speech instanceof PlainTextOutputSpeech);
        assertEquals("Hola Roberto", ((PlainTextOutputSpeech) speech).getText().trim());
    }

    @Test
    public void testAllTypes(){
        List<String> response = Arrays.asList("This is a test of the emergency broadcast system",
                "The broadcasters in your area along with state, local, and federal authorities have" +
                        "developed this system to keep you informed in the event of an emergency",
                "If this had been an actual emergency, the attention signal you just heard would have been" +
                        "followed by news, weather, and traffic on the nines");
        MustacheBundle bundle = new MustacheBundle("/alltypes", "en");
        OutputSpeech speech = bundle.createOutputSpeech(response, mock(IntentRequest.class), SESSION);
        assertTrue(speech instanceof SsmlOutputSpeech);
        StringBuilder sb = new StringBuilder("<speak>");
        response.forEach(s-> sb.append("<s>").append(s).append("</s>"));
        sb.append("</speak>");
        assertEquals(sb.toString(), ((SsmlOutputSpeech) speech).getSsml().trim());


    }
}