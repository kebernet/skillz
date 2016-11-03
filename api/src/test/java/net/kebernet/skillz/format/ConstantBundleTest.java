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
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.User;
import com.amazon.speech.ui.SsmlOutputSpeech;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by rcooper on 10/24/16.
 */
public class ConstantBundleTest {

    private static final HashMap<String, Object> ATTS = new HashMap<>();
    private static final Session SESSION = Session.builder()
            .withApplication(new Application("foo"))
            .withUser(new User("foo@foo.com"))
            .withSessionId("bar")
            .withIsNew(true)
            .withAttributes(ATTS)
            .build();

    @Test
    public void testNoLang(){
        ConstantBundle bundle = new ConstantBundle("/constant", "en");
        assertEquals("Hello, world!", bundle.createCardContent(null, mock(SpeechletRequest.class), SESSION).trim());
        assertEquals("<speech>\n    <s>Hello, world!</s>\n</speech>", ((SsmlOutputSpeech)bundle.createOutputSpeech(null, mock(SpeechletRequest.class), SESSION)).getSsml().trim());
    }

}