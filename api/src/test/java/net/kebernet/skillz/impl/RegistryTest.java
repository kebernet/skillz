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

import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.skillz.test.BurnsAndAllen;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegistryTest {
    Registry registry = new Registry();

    @Test
    public void simpleDiscoveryTest() throws Exception {
        Set<Class> classes = registry.getAllIntrospectionData()
                .stream().map(IntrospectionData::getType)
                .collect(Collectors.toSet());
        assertTrue(classes.contains(BurnsAndAllen.class));
    }

    @Test
    public void findByPathSuccess() throws Exception {
        assertEquals(BurnsAndAllen.class,
                registry.getDataForPath("/burnsallen")
                    .orElseThrow(RuntimeException::new)
                    .getType());
    }

    @Test(expected = RuntimeException.class)
    public void findByPathFail() throws Exception {
        registry.getDataForPath("/not_a_path").orElseThrow(RuntimeException::new);
    }



}