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

import com.amazon.speech.ui.PlainTextOutputSpeech;

/**
 * A builder for PlainTextOutputSpeech.
 * <p>
 *     Example:
 * </p>
 * <pre>
 *     PlainTextSpeedOutput output = PlainTextOutputBuilder.withText("Hello, world.")
 *                                                          .withId("id:hello")
 *                                                          .build();
*  </pre>
 *
 */
public class PlainTextOutputBuilder {

    private final PlainTextOutputSpeech result = new PlainTextOutputSpeech();

    private PlainTextOutputBuilder(String text) {
        this.result.setText(text);
    }

    /**
     * Sets the ID for the PlainTextSpeechOutput
     * @param id The id
     * @return The builder
     */
    public PlainTextOutputBuilder withId(String id){
        this.result.setId(id);
        return this;
    }

    /**
     * Constructs a PlainTextSpeechOutput from the current values.
     * @return SpeechOutput.
     */
    public PlainTextOutputSpeech build(){
        if(this.result.getId() == null){
            this.result.setId(Integer.toString(result.getText().hashCode()) );
        }
        return this.result;
    }

    /** Begin a new Builder with the plain text value
     *
     * @param text Text to use
     * @return the builder
     */
    public static PlainTextOutputBuilder withText(String text){
        return new PlainTextOutputBuilder(text);
    }
}
