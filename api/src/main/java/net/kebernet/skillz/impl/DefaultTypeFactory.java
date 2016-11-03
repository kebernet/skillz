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

import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.TypeFactory;

/**
 *  A default implementation of TypeFactory that builds everything assuming there
 *  is a no-args constructor.
 */
public class DefaultTypeFactory implements TypeFactory {

    @Override
    public <T> T create(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SkillzException("Unable to create type "+type.getCanonicalName()+" with no-args constructor.", e);
        }
    }
}
