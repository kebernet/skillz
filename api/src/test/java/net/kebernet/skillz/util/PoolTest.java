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

import net.kebernet.skillz.TypeFactory;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class PoolTest {

    @Test
    public void testSimple() {
        TypeFactory factory = new TypeFactory() {
            int count = 0;

            @Override
            public <T> T create(Class<T> type) {
                return (T) Integer.toString(count++);
            }
        };
        Pool<String> pool = new Pool<>(factory, String.class, 100);
        for (int i = 0; i < 100; i++) {
            String val = pool.checkout();
            assertEquals(Integer.toString(i), val);
            pool.checkin(val);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testFull() throws Exception {
        TypeFactory factory = new TypeFactory() {
            int count = 0;

            @Override
            public <T> T create(Class<T> type) {
                return (T) Integer.toString(count++);
            }
        };
        Pool<String> pool = new Pool<>(factory, String.class, 100);
        for (int i = 0; i < 100; i++) {
            String val = pool.checkout();
            assertEquals(Integer.toString(i), val);
            pool.checkin(val);
        }
        pool.checkin(Integer.toString(100));
    }

    @Test
    public void testWait() {
        TypeFactory factory = new TypeFactory() {
            int count = 0;

            @Override
            public <T> T create(Class<T> type) {
                return (T) Integer.toString(count++);
            }
        };
        Pool<String> pool = new Pool<>(factory, String.class, 100);
        String last = null;
        for (int i = 0; i < 100; i++) {
            last = pool.checkout();
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        String finalLast = last;
        // Schedule another thread to check into the empty pool
        scheduledExecutorService.schedule(() -> pool.checkin(finalLast), 500, TimeUnit.MILLISECONDS);
        String checked = pool.checkout();
        assertEquals(last, checked);
    }

    @Test(expected = RuntimeException.class)
    public void testWaitFail() {
        TypeFactory factory = new TypeFactory() {
            int count = 0;

            @Override
            public <T> T create(Class<T> type) {
                return (T) Integer.toString(count++);
            }
        };
        Pool<String> pool = new Pool<>(factory, String.class, 100, 100, TimeUnit.MILLISECONDS);
        // drain the pool
        for (int i = 0; i < 100; i++) {
            pool.checkout();
        }
        String checked = pool.checkout();
        System.out.println(checked);
    }

}