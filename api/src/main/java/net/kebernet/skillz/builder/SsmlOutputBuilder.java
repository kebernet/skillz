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

import com.amazon.speech.ui.SsmlOutputSpeech;

/**
 * A builder for PlainTextOutputSpeech.
 * <p>
 *     Example:
 * </p>
 * <pre>
 *     SsmlOutputBuilder output = SsmlOutputBuilder.withText("Hello, world.")
 *                                                 .withId("id:hello")
 *                                                 .build();
 * </pre>
 *
 */
@SuppressWarnings("WeakerAccess")
public class SsmlOutputBuilder {

    private final SsmlOutputSpeech result = new SsmlOutputSpeech();

    private SsmlOutputBuilder(String ssml) {
        this.result.setSsml(ssml);
    }

    public SsmlOutputBuilder withId(String id){
        this.result.setId(id);
        return this;
    }

    public SsmlOutputSpeech build(){
        if(this.result.getId() == null){
            this.result.setId(Integer.toString(result.getSsml().hashCode()) );
        }
        return this.result;
    }

    public static SsmlOutputBuilder withSsml(String ssml){
        return new SsmlOutputBuilder(ssml);
    }
}
