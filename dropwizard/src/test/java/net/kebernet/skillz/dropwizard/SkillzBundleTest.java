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

import com.google.common.io.Resources;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

public class SkillzBundleTest {

    private static Thread serverThread;

    @BeforeClass
    public static void setup() throws Exception {
        serverThread = new Thread(() -> {
            try {

                new TestApplication().run(new String[]{"server", Resources.getResource("server.yml").getPath()});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.setDaemon(true);
        serverThread.start();
    }

    @AfterClass
    public static void teardown(){
        serverThread.interrupt();
        serverThread.stop();
    }

    @Test
    public void testSleep() throws InterruptedException {
        Thread.sleep(10000);
        assertEquals(true, true);
    }

    @Test
    public void dummyTest(){

    }

}