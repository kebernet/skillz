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

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import net.kebernet.skillz.Formatter;
import net.kebernet.skillz.builder.PlainTextOutputBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utility class with some general purpose Formatters.
 */
public abstract class Formatters {

    private Formatters(){}

    @Singleton
    public static class SimplePlainTextTell implements Formatter<String> {
        @Inject
        public SimplePlainTextTell(){}

        @Override
        public SpeechletResponse apply(String s, SpeechletRequest request, Session session) {
            return SpeechletResponse.newTellResponse(PlainTextOutputBuilder.withText(s).build());
        }
    }

}
