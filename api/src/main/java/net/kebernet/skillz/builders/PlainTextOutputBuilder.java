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
package net.kebernet.skillz.builders;

import com.amazon.speech.ui.PlainTextOutputSpeech;

/**
 * A builder for PlainTextOutputSpeech.
 * <p>
 *     Example:
 * <pre>
 *     PlainTextSpeedOutput output = PlainTextSpeechOutputBuilder.withText("Hello, world.")
 *                                                               .withId("id:hello")
 *                                                               .build();
*  </pre>
 * </p>
 */
public class PlainTextOutputBuilder {

    private final PlainTextOutputSpeech result = new PlainTextOutputSpeech();

    public PlainTextOutputBuilder(String text) {
        this.result.setText(text);
    }

    public PlainTextOutputBuilder withId(String id){
        this.result.setId(id);
        return this;
    }

    public PlainTextOutputSpeech build(){
        if(this.result.getId() == null){
            this.result.setId(Integer.toString(result.getText().hashCode()) );
        }
        return this.result;
    }

    public static PlainTextOutputBuilder withText(String text){
        return new PlainTextOutputBuilder(text);
    }
}
