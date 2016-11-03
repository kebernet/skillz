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
package net.kebernet.skillz.dropwizard;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.skillz.FormatterMappings;
import net.kebernet.skillz.SkillzFilter;
import net.kebernet.skillz.TypeFactory;
import net.kebernet.skillz.annotation.Skill;
import net.kebernet.skillz.impl.DefaultTypeFactory;
import org.eclipse.jetty.servlet.FilterHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by rcooper on 10/20/16.
 */
public class SkillzBundle implements Bundle {
    private static final Logger LOG = LoggerFactory.getLogger(SkillzBundle.class);
    private final String skillzPath;
    private final TypeFactory typeFactory;
    private final FormatterMappings formatters;

    public SkillzBundle(){
        this("/*");
    }

    public SkillzBundle(String skillzPath){
        checkPath(skillzPath);
        this.skillzPath = skillzPath;
        this.typeFactory = new DefaultTypeFactory();
        this.formatters = new FormatterMappings();;
    }

    private static void checkPath(@Nonnull String skillzPath) {
        checkNotNull(skillzPath);
        checkArgument(skillzPath.startsWith("/") && skillzPath.endsWith("/*"),
                "Your path should be in the form '/somepath/* with the leading / and the trailing /*");
    }

    public SkillzBundle(@Nonnull String skillzPath, @Nonnull TypeFactory factory){
        checkNotNull(skillzPath, "skillzPath is required.");
        checkNotNull(factory, "You must provide a TypeFactory with this constructor.");
        checkPath(skillzPath);
        this.skillzPath = skillzPath;
        this.typeFactory = factory;
        this.formatters = new FormatterMappings();
    }

    public SkillzBundle(@Nonnull String skillzPath, @Nonnull FormatterMappings formatters){
        checkPath(skillzPath);
        checkNotNull(skillzPath, "skillzPath is required.");
        checkNotNull(formatters, "You must provide FormatterMappings with this constructor.");
        this.skillzPath = skillzPath;
        this.typeFactory = new DefaultTypeFactory();
        this.formatters = formatters;
    }

    public SkillzBundle(@Nonnull String skillzPath, @Nonnull TypeFactory factory, @Nonnull FormatterMappings formatters) {
        checkPath(skillzPath);
        checkNotNull(skillzPath, "skillzPath is required.");
        checkNotNull(factory, "You must provide a TypeFactory with this constructor.");
        checkNotNull(formatters, "You must provide FormatterMappings with this constructor.");
        this.skillzPath = skillzPath;
        this.typeFactory = factory;
        this.formatters = formatters;
    }

    public FormatterMappings getFormatters(){
        return formatters;
    }


    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(Environment environment) {
        SkillzFilter filter = typeFactory.create(SkillzFilter.class);
        filter.setPathPrefix(skillzPath.substring(0, skillzPath.length() -2));
        FilterHolder holder = new FilterHolder(filter);
        environment.getApplicationContext().addFilter(holder, skillzPath,
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE) );
        StringBuilder sb = new StringBuilder("Skillz: ")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        String filterPath = skillzPath.substring(0, skillzPath.length() -1);
        for(IntrospectionData data :filter.getRegistry().getAllIntrospectionData()){
            String path = ((Skill) data.getType().getAnnotation(Skill.class)).path();
            sb.append(String.format("   SKILL    %s (%s)", filterPath+path, data.getType().getName())).append(System.lineSeparator());
        }
        LOG.info(sb.toString());
    }
}
