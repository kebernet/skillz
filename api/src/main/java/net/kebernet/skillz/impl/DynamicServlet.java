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
import com.google.common.collect.ArrayListMultimap;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.invoker.runtime.impl.InvokableMethod;
import net.kebernet.skillz.FormatterMappings;
import net.kebernet.skillz.TypeFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A subclass of Speechlet Servlet that delegates to the DynamicSpeechlet.
 */
public class DynamicServlet extends SpeechletServlet {
    private static final Logger LOGGER = Logger.getLogger(DynamicServlet.class.getCanonicalName());
    public DynamicServlet(final FormatterMappings responseMapper, final Registry registry, final Object implementation, final IntrospectionData data, final TypeFactory typeFactory){
        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));
        this.setSpeechlet(new DynamicSpeechlet(methods, data, responseMapper, registry, implementation, typeFactory));
     }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
