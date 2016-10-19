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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by rcooper on 10/19/16.
 */
public class OutputUtterancesTest {


    @Test
    public void testSimple() throws IOException {
        Registry registry = new Registry(new HashSet<>(Arrays.asList(BurnsAndAllen.class)));
        OutputUtterances outputUtterances =  new OutputUtterances(
                registry.getDataForPath("/burnsallen").orElseThrow(RuntimeException::new)
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outputUtterances.writeTo(baos);
        assertEquals(BurnsAndAllen.EXPECTED_UTTERANCES, new String(baos.toByteArray(), "UTF-8"));
    }

}