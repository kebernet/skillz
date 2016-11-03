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

import javax.inject.Singleton;

/**
 * This is a simple interfaces that is used to construct new instances of types by
 * the Skillz API. If you are using a DI framework, this would be the place you hook
 * that into Skillz to get your classes.
 * <p>
 *     This includes skill classes as well as Formatter instances when they are declared
 *     using {@link net.kebernet.skillz.annotation.ResponseFormatter}.
 * </p>
 *
 */
public interface TypeFactory {
    /**
     * Creates an instance of type T.
     * @param type Class reference for the type.
     * @param <T> The type to create.
     * @return And instance.
     */
    <T> T create(Class<T> type);
}
