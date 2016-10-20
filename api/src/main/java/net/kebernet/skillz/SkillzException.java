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

/**
 * Thrown when there is a problem within the Skillz API.
 */
public class SkillzException extends RuntimeException {

    /**
     * Default constructor
     */
    public SkillzException() {
        super();
    }

    /**
     * Constructor with message
     * @param message The message
     */
    public SkillzException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * @param message The message
     * @param cause The cause
     */
    public SkillzException(String message, Throwable cause) {
        super(message, cause);
    }

}
