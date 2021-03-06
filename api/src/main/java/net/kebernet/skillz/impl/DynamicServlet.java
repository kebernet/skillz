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
import net.kebernet.skillz.util.OutputIntents;
import net.kebernet.skillz.util.OutputUtterances;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A subclass of Speechlet Servlet that delegates to the DynamicSpeechlet.
 */
public class DynamicServlet extends SpeechletServlet {
    private static final String UTTERANCES = "utterances";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "utf-8";
    private static final String INTENTS = "intents";
    private static final Logger LOGGER = Logger.getLogger(SpeechletServlet.class.getCanonicalName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch(Exception e){
            LOGGER.log(Level.WARNING, "Unexpected exception in servlet", e);
        }
    }

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
                resp.flushBuffer();
                break;
            }
            case INTENTS: {
                DynamicSpeechlet speechlet = (DynamicSpeechlet) getSpeechlet();
                OutputIntents output = new OutputIntents(speechlet.getData());
                resp.setContentType(APPLICATION_JSON);
                resp.setCharacterEncoding(UTF_8);
                output.writeTo(resp.getWriter());
                resp.flushBuffer();
                break;
            }
            default:
                throw new ServletException("What does "+q+" mean??");
        }
    }
}
