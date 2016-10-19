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

import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.TypeFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A really stupid simple fixed pool implementation.
 */
public class Pool<T> {

    private final LinkedBlockingQueue<T> queue;
    private final long waitTimeout;
    private final TimeUnit waitTimeoutUnit;

    /**
     * Constructor
     *
     * @param factory TypeFactory to create pooled objects.
     * @param type Type of object in the pool
     * @param count Number in the pool
     * @param waitTimeout time to wait during checkout
     * @param waitTimeoutUnit time unit to wait during checkout
     */
    public Pool(TypeFactory factory, Class<T> type, int count, long waitTimeout, TimeUnit waitTimeoutUnit){
        queue = new LinkedBlockingQueue<>(count);
        for(int i=0; i < count; i++){
            try {
                queue.put(factory.create(type));
            } catch (InterruptedException e) {
                throw new SkillzException("Failed to initialize pool of "+type.getCanonicalName());
            }
        }
        this.waitTimeout = waitTimeout;
        this.waitTimeoutUnit = waitTimeoutUnit;
    }

    /**
     * Constructor with a default wait time of 15 seconds.
     * @param factory Factory to create objects
     * @param type Type of pool
     * @param count Number in pool.
     */
    public Pool(TypeFactory factory, Class<T> type, int count){
        this(factory, type, count, 15, TimeUnit.SECONDS);
    }

    public T checkout(){
        T value = null;
        try {
            value = queue.poll(waitTimeout, waitTimeoutUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to get object from pool", e);
        }
        if(value == null){
            throw new RuntimeException("Pool empty.");
        }
        return value;
    }

    public void checkin(T value){
        if(queue.remainingCapacity() == 0){
            throw new RuntimeException("Attempt to check into a full pool.");
        }
        queue.offer(value);
    }


}
