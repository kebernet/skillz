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

import com.amazon.speech.ui.SimpleCard;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by rcooper on 10/22/16.
 */
public class SimpleCardBuilder {

    private SimpleCard card = new SimpleCard();

    public static SimpleCardBuilder withTitle(@Nonnull String title){
        SimpleCardBuilder builder = new SimpleCardBuilder();
        builder.card.setTitle(title);
        return builder;
    }

    public SimpleCardBuilder withContent(@Nonnull String content){
        card.setContent(content);
        return this;
    }

    public SimpleCard build(){
        checkState(card.getContent() != null, "No content set.");
        return card;
    }
}
