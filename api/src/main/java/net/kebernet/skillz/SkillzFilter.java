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

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.invoker.runtime.impl.InvokableMethod;
import net.kebernet.skillz.impl.DefaultTypeFactory;
import net.kebernet.skillz.impl.DynamicServlet;
import net.kebernet.skillz.impl.DynamicSpeechlet;
import net.kebernet.skillz.impl.Registry;
import net.kebernet.skillz.util.Pool;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 *  This is the servlet Filter that handles dispatching requests to the declared
 *  Skill classes.
 *
 */
public class SkillzFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(SkillzFilter.class.getCanonicalName());
    private final Pool<DynamicServlet> pool;
    private final TypeFactory factory;
    private final Registry registry;
    private final FormatterMappings mappings;


    /**
     * The standard Dependency-Injected constructor.
     * @param registry The registry to use.
     * @param factory The TypeFactory to build instances with.
     * @param mappings FormatterMappings for building SpeechletResponses
     */
    @Inject
    public SkillzFilter(Registry registry, TypeFactory factory, FormatterMappings mappings){
        this.factory = factory;
        this.registry = registry;
        this.mappings = mappings;
        this.pool =  new Pool<>(factory, DynamicServlet.class, 512);
    }


    /**
     *  For simple JavaEE type usage, the default constructor will create a registry,
     *  a DefaultTypeFactory, and a FormatterMappings instance for the filter.
     */
    public SkillzFilter(){
        this(new Registry(), new DefaultTypeFactory(), new FormatterMappings());
    }

    public Registry getRegistry(){
        return registry;
    }

    public TypeFactory getFactory(){
        return factory;
    }

    public FormatterMappings getMappings(){
        return mappings;
    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getPathInfo();
        LOGGER.finer("Checking for Skill at "+path);
        if(!Strings.isNullOrEmpty(path)){
            Optional<IntrospectionData> handler = registry.getDataForPath(path);
            if(!handler.isPresent()){ // Nothing to handle.
                chain.doFilter(request, response);
                return;
            }

            IntrospectionData data = handler.get();
            LOGGER.fine("Handling skill request for "+path+" with "+data.getType());
            Object instance = factory.create(data.getType());
            final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
            data.getMethods().forEach(m->methods.put(m.getName(), m));

            // Check a servlet out and invoke it.
            DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, mappings, registry, instance, factory);
            DynamicServlet servlet;
            try {
                servlet = pool.checkout();
                servlet.setSpeechlet(speechlet);
                servlet.service(req, res);
                servlet.setSpeechlet(null);
            } catch (RuntimeException e) {
                throw new ServletException(e);
            }
            pool.checkin(servlet);
        }
    }

    public void destroy() {

    }

}
