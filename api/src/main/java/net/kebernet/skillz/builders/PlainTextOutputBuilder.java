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
 * Created by rcooper on 10/16/16.
 */
public class PlainTextOutputBuilder {

    private String text;
    private String id;

    public PlainTextOutputBuilder(String text) {
        this.text = text;
    }

    public PlainTextOutputBuilder withId(String id){
        this.id = id;
        return this;
    }

    public PlainTextOutputSpeech build(){
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(text);
        speech.setId( id == null ? Integer.toString(text.hashCode()) : id);
        return speech;
    }

    public static PlainTextOutputBuilder withText(String text){
        return new PlainTextOutputBuilder(text);
    }
}
