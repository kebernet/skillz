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

import net.kebernet.skillz.impl.Registry;
import net.kebernet.skillz.test.BurnsAndAllen;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class OutputIntentsTest {
    @Test
    public void buildIntentsAndSlots() throws Exception {
        Registry registry = new Registry(new HashSet<>(Arrays.asList(BurnsAndAllen.class)));
        OutputIntents outputIntents = new OutputIntents(registry.getAllIntrospectionData().iterator().next());
        assertEquals("{\"intents\":[{\"intent\":\"GeorgeAndGracie\",\"slots\":[{\"name\":\"greeting\",\"type\":\"AMAZON.LITERAL\"},{\"name\":\"name\",\"type\":\"AMAZON.US_FIRST_NAME\"}]}]}", outputIntents.build());
    }

}