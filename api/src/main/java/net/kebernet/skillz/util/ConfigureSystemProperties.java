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

import com.amazon.speech.Sdk;

/**
 *  This is a util class that configures the System Properties for local testing or
 *  production running.
 */
public class ConfigureSystemProperties {


    /**
     * This disables request signature testing and timestamp testing for development purposes.
     */
    public static void setForTesting(){
        System.getProperties().setProperty(Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY, "true");
        System.getProperties().setProperty(Sdk.TIMESTAMP_TOLERANCE_SYSTEM_PROPERTY, Long.toString(Long.MAX_VALUE));
    }

    /**
     * Sets the timestamp tolerance to 120.
     */
    public static void setForProduction(){
        System.getProperties().setProperty(Sdk.TIMESTAMP_TOLERANCE_SYSTEM_PROPERTY, "120");
    }


}
