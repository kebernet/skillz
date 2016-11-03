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
package net.kebernet.skillz.format;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.google.common.base.Splitter;
import net.kebernet.skillz.Formatter;
import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.builder.RepromptBuilder;
import net.kebernet.skillz.builder.SimpleCardBuilder;
import net.kebernet.skillz.builder.StandardCardBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Logger;

/**
 * An abstract Formatter implementation for creating a formatter from Bundles.
 */
@SuppressWarnings("unused")
public abstract class AbstractBundleFormatter<T> implements Formatter<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractBundleFormatter.class.getCanonicalName());
    private final Bundle bundle;
    private final Bundle repromptBundle;

    /**
     * Constructs an new instance. If you provide a reprompt bundle with the constructor, it will
     * build an Ask response.
     * @param bundle The Bundle to use to format the response.
     * @param repromptBundle Optional bundle for preprompt
     */
    public AbstractBundleFormatter(@Nonnull Bundle bundle, @Nullable Bundle repromptBundle) {
        this.bundle = bundle;
        this.repromptBundle = repromptBundle;
    }

    @Override
    public SpeechletResponse apply(T t, SpeechletRequest request, Session session) {
        OutputSpeech speech = bundle.createOutputSpeech(t, request, session);
        String cardContent = bundle.createCardContent(t, request, session);
        cardContent = truncateCardContent(cardContent);
        String cardLargeImage = getCardLargeImage(t,request, session);
        Card card = null;
        if(cardLargeImage != null){
            card = StandardCardBuilder.withImages(cardLargeImage, getCardSmallImage(t, request, session))
                    .withTitle(getCardTitle(t, request, session))
                    .withText(cardContent)
                    .build();
        } else if(cardContent != null){
            card = SimpleCardBuilder.withTitle(getCardTitle(t, request, session))
                    .withContent(cardContent)
                    .build();
        }
        if(isAskResponse(t, request, session)){
            Reprompt reprompt = null;
            if(repromptBundle != null){
                reprompt = RepromptBuilder.withOutputSpeech(
                        repromptBundle.createOutputSpeech(t, request, session)
                );
            }
            if(reprompt == null){
                throw new SkillzException("As response with no reprompt: "+t+" "+request);
            }
            SpeechletResponse response = SpeechletResponse.newAskResponse(speech, reprompt);
            response.setCard(card);
            return response;
        } else {
            SpeechletResponse response = SpeechletResponse.newTellResponse(speech);
            response.setCard(card);
            return response;
        }

    }

    private String truncateCardContent(String cardContent) {
        if(cardContent == null){
            return cardContent;
        }
        if(cardContent.length() < 8000){
            return cardContent;
        }
        StringBuilder sb = new StringBuilder();
        int lineCount = 0;
        for(String line : Splitter.on('\n')
                .split(cardContent)){
            if(sb.length() == 0 && line.length() >= 8000){
                LOGGER.warning("Card content is over the limit and will be truncated.");
                return line.substring(0, 7999);
            }
            if(sb.length() + line.length() >= 7999){
                LOGGER.warning("Card content is too long and will be truncated to "+lineCount);
                return sb.toString();
            }
            sb.append(line).append('\n');
            lineCount++;
        }
        return sb.toString();
    }

    /**
     * Toggles whether the Formatter is returning an Ask or Tell response.
     * @param t The value passed in.
     * @param request The SpeechletRequest
     * @param session The session
     * @return true if the response is asking a question.
     */
    @SuppressWarnings("WeakerAccess")
    public abstract boolean isAskResponse(T t, SpeechletRequest request, Session session);

    /**
     * Returns the title of the Alexa card, if needed.
     * @param t The value passed in.
     * @param request The SpeechletRequest
     * @param session The session
     * @return Title for the card.
     */
    @SuppressWarnings("WeakerAccess")
    public abstract String getCardTitle(T t, SpeechletRequest request, Session session);
    /**
     * Returns the large image url for the alexa card.
     * @param t The value passed in.
     * @param request The SpeechletRequest
     * @param session The session
     * @return URL for the image.
     */
    @SuppressWarnings("WeakerAccess")
    public abstract String getCardLargeImage(T t, SpeechletRequest request, Session session);
    /**
     * Returns the small image url for the alexa card.
     * @param t The value passed in.
     * @param request The SpeechletRequest
     * @param session The session
     * @return URL for the image
     */
    @SuppressWarnings("WeakerAccess")
    public abstract String getCardSmallImage(T t, SpeechletRequest request, Session session);
}
