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

import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import com.google.common.base.Strings;
import net.kebernet.skillz.util.OutputUtterances;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A subclass of Speechlet Servlet that delegates to the DynamicSpeechlet.
 */
public class DynamicServlet extends SpeechletServlet {
    private static final String UTTERANCES = "utterances";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String UTF_8 = "utf-8";
    private static final String INTENTS = "intents";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getQueryString();
        if(Strings.isNullOrEmpty(q)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        switch(q) {
            case UTTERANCES: {
                DynamicSpeechlet speechlet = (DynamicSpeechlet) getSpeechlet();
                OutputUtterances output = new OutputUtterances(speechlet.getData());
                resp.setContentType(TEXT_PLAIN);
                resp.setCharacterEncoding(UTF_8);
                output.writeTo(resp.getWriter());
                break;
            }
            case INTENTS: {
                break;
            }
            default:
                throw new ServletException("What does "+q+" mean??");
        }
    }
}
