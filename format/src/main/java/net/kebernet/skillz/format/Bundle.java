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

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.ui.OutputSpeech;

/**
 * Created by rcooper on 10/22/16.
 */
public interface Bundle {

    /**
     * Creates an OutputSpeech object for the given values.
     * @param response The created response object.
     * @param request The request.
     * @param session The session.
     * @return Formatted output speech based on the response object.
     */
    OutputSpeech createOutputSpeech(Object response, SpeechletRequest request, Session session);
    /**
     * Creates card content for the given values.
     * @param response The created response object.
     * @param request The request.
     * @param session The session.
     * @return Card content based on the response object.
     */
    String createCardContent(Object response, SpeechletRequest request, Session session);
}
