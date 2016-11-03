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
package net.kebernet.skillz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method parameter as being populated from a slot value.
 * <p>If a parameter is not of a type {@link String} then Skillz will use the
 * {@link net.kebernet.skillz.util.Coercion} class to attempt to convert it to
 * the appropriate value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.PARAMETER)
public @interface Slot {
    /**
     * The name of the slot to read the parameter value from.
     * @return The name of the slot.
     */
    String name();

    /** The type of the slot. This value is not used at runtime, but will be used to
     * generate the intents.json structure.
     * @return The type of the slot.
     */
    String type() default "";
}
