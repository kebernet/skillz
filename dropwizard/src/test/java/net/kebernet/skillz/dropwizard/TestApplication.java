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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import com.amazon.speech.Sdk;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.health.HealthCheck;
import com.google.common.io.Resources;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.kebernet.skillz.util.ConfigureSystemProperties;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by rcooper on 10/20/16.
 */
public class TestApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        System.setProperty(Sdk.TIMESTAMP_TOLERANCE_SYSTEM_PROPERTY, "60");
        new TestApplication().run("server", Resources.getResource("server.yml").getPath());
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        ConfigureSystemProperties.setForTesting();
        SkillzBundle bundle = new SkillzBundle("/skills/*");
        bootstrap.addBundle(bundle);
    }

    @Override
    public String getName() {
        return "Test App 1";
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.healthChecks().register("alive", new HealthCheck() {
            @Override
            protected HealthCheck.Result check() throws Exception {
                return HealthCheck.Result.healthy();
            }
        });
    }

    @Path("/api")
    @Produces(value = MediaType.APPLICATION_JSON)
    public static class MyResource {

        @Metered
        @GET
        public String get(@QueryParam(value = "name") String name) throws Exception {
            return "hello " + name;
        }
    }
}
