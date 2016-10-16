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

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class CoercionTest {

    private Coercion coercion = new Coercion();

    @Test
    public void testCoersions() {
        assertEquals(Integer.valueOf(3), coercion.coerce("3", Integer.class));
        assertEquals(Integer.valueOf(3), coercion.coerce("03", Integer.class));
        assertEquals(Integer.valueOf(3), coercion.coerce("3", int.class));
        assertEquals(Double.valueOf(3.1D), coercion.coerce("3.1", Double.class));
        assertEquals(Double.valueOf(3.1D), coercion.coerce("0003.1", Double.class));
        assertEquals(Double.valueOf(3.1D), coercion.coerce("3.10000", Double.class));
        assertEquals(Double.valueOf(3.1D), coercion.coerce("3.1", double.class));
        assertEquals(Boolean.TRUE, coercion.coerce("true", Boolean.class));
        assertEquals(Boolean.FALSE, coercion.coerce("false", Boolean.class));
        assertEquals(Boolean.TRUE, coercion.coerce("yes", Boolean.class));
        assertEquals(Boolean.FALSE, coercion.coerce("no", Boolean.class));


        //Dates in fixed format.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime(), coercion.coerce("2013-02-02 00:00:00", Date.class));


        //Identity for passthrough conversions
        Object four = Integer.valueOf(4);
        assertTrue(coercion.coerce(four, Integer.class) == four);

        //Except empty Strings become null.
        assertEquals(null, coercion.coerce("", String.class));

        assertEquals(TestEnum.FIRST, coercion.coerce("first", TestEnum.class));
        assertEquals(TestEnum.SECOND, coercion.coerce(1, TestEnum.class));
    }


    enum TestEnum {
        FIRST,
        SECOND
    }
}