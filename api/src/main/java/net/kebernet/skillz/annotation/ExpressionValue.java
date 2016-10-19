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
 * ExpressionValue allows you to read from arbitrary values on the request and
 * session objects. It allows you to write an
 * <a href="https://en.wikipedia.org/wiki/OGNL">OGNL</a> expression, addressing either
 * <code>request</code> or <code>session</code> as top level context objects.
 * <p>
 * For example, if you wanted to get access to the Alexa Session from your class, you
 * might say,
 * </p>
 * <blockquote>
 * <pre>
 *     public String myMethod(@ExpressionValue("session.attributes") Map&lt;String,Object&gt; contextMap) {
 *          //...
 * </pre>
 * or
 * <pre>
 *     public String myMethod(@ExpressionValue("session.attributes.myValue") Map&lt;String,Object&gt; myValue) {
 *          //...
 * </pre>
 * </blockquote>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.PARAMETER)
public @interface ExpressionValue {
    String value();
}
