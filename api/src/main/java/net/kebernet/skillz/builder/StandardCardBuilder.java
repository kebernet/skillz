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

import com.amazon.speech.ui.Image;
import com.amazon.speech.ui.StandardCard;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.kebernet.skillz.SkillzException;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by rcooper on 10/22/16.
 */
public class StandardCardBuilder {

    private static final Cache<String, Image> IMAGE_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(64)
            .maximumSize(100)
            .build();

    private StandardCard cart = new StandardCard();

    public static StandardCardBuilder withImages(@Nonnull String largeImageUrl, String smallImageUrl){
        StandardCardBuilder builder = new StandardCardBuilder();
        try {
            builder.cart.setImage(IMAGE_CACHE.get(largeImageUrl+"##"+smallImageUrl, ()->{
                Image image = new Image();
                image.setLargeImageUrl(largeImageUrl);
                image.setSmallImageUrl(smallImageUrl);
                return image;
            }));
        } catch (ExecutionException e) {
            throw new SkillzException("Unexpected exception creating image.", e);
        }
        return builder;
    }

    public StandardCardBuilder withTitle(@Nonnull String title){
        this.cart.setTitle(title);
        return this;
    }

    public StandardCardBuilder withText(@Nonnull String content){
        this.cart.setText(content);
        return this;
    }

    public StandardCard build(){
        checkState(cart.getTitle() != null, "Title not set.");
        checkState(cart.getText() != null, "Text not set.");
        return this.cart;
    }
}
