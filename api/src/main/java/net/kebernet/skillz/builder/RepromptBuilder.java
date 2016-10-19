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
package net.kebernet.skillz.builder;

import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;

/**
 * Created by rcooper on 10/18/16.
 */
public class RepromptBuilder {

    private RepromptBuilder(){
    }

    public static Reprompt withOutputSpeech(OutputSpeech speech){
        Reprompt result = new Reprompt();
        result.setOutputSpeech(speech);
        return result;
    }
}
