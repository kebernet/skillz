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
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.skillz.impl.Registry;

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
 * Created by rcooper on 10/15/16.
 */
public class SkillzFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(SkillzFilter.class.getCanonicalName());
    private final TypeFactory factory;
    private final Registry registry;

    @Inject
    public SkillzFilter(TypeFactory factory, FormatterMappings responseBuilder){
        this.factory = factory;
        this.registry = new Registry();
    }


    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getPathInfo();
        if(!Strings.isNullOrEmpty(path)){
            Optional<IntrospectionData> handler = registry.getDataForPath(path);
            if(!handler.isPresent()){ // Nothing to handle.
                chain.doFilter(request, response);
                return;
            }
        }


    }

    public void destroy() {

    }

}
