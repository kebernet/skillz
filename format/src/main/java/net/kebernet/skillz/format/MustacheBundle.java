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
import com.amazon.speech.ui.OutputSpeech;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.builder.PlainTextOutputBuilder;
import net.kebernet.skillz.builder.SsmlOutputBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkState;

/**
 * A Bundle implementation that used Mustache.java to format responses.
 *
 * <p>This class will allow you to format </p>
 */
@SuppressWarnings("WeakerAccess")
public class MustacheBundle implements Bundle {
    private static final Logger LOGGER = Logger.getLogger(ResourceBundle.class.getCanonicalName());
    private final MustacheFactory mf = new DefaultMustacheFactory();
    private final String bundleName;
    private final String languageCode;
    private Mustache ssmlTemplate;
    private Mustache txtTemplate;
    private Mustache cardTemplate;

    /**
     * Creates a new bundle form a classpath resource
     * @param bundleName The resource path. Should start with "/"
     * @param languageCode The language code, if needed.
     */
    public MustacheBundle(String bundleName, String languageCode) {
        this.bundleName = bundleName;
        this.languageCode = languageCode;
        init();
    }

    private void init(){
        this.ssmlTemplate = createMustacheFromResource(mf, bundleName, languageCode, "ssml");
        this.txtTemplate = createMustacheFromResource(mf, bundleName,languageCode, "txt");
        this.cardTemplate = createMustacheFromResource(mf, bundleName, languageCode, "card");
        checkState( ssmlTemplate != null || txtTemplate != null, "Could not find txt or ssml output for "+bundleName);
    }

    @Override
    public OutputSpeech createOutputSpeech(Object response, SpeechletRequest request, Session session){
        boolean useSSML = ssmlTemplate != null;
        String formatted = format(useSSML ? ssmlTemplate : txtTemplate, response, request, session);
        return useSSML ? SsmlOutputBuilder.withSsml(formatted).build() :
                PlainTextOutputBuilder.withText(formatted).build();
    }

    @Override
    public String createCardContent(Object response, SpeechletRequest request, Session session){
        String cardContent = cardTemplate == null ? null : format(cardTemplate, response, request, session);
        if(cardContent.lastIndexOf(" ") == -1){
            return cardContent.substring(0, 7999);
        }
        while(cardContent.length() > 8000){
            cardContent = cardContent.substring(0, cardContent.lastIndexOf(" ")) + "...";
        }
        return cardContent;
    }

    private static Mustache createMustacheFromResource(MustacheFactory mf, String resourceBaseName, String languageCode, String fileType){
        URL url = null;
        try {
            url = MustacheBundle.class.getResource(resourceBaseName+"."+languageCode+"."+fileType+".mustache");
            url = url == null ? MustacheBundle.class.getResource(resourceBaseName+"."+fileType+".mustache") : url;
            if(url != null) {
                return mf.compile(new InputStreamReader(url.openStream(), "utf-8"), resourceBaseName);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to parse " + url.toExternalForm(), e);
        }
        return null;
    }

    private static String format(Mustache template, Object response, SpeechletRequest request, Session session){
        Map<String, Object> ctx = new HashMap<>(3);
        ctx.put("response", response);
        ctx.put("request", request);
        ctx.put("session", session);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, "utf-8")) {
            template.execute(writer, ctx);
            writer.flush();
            return new String(baos.toByteArray(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new SkillzException("Failed to encode formatted response", e);
        } catch (IOException e) {
            throw new SkillzException("Failed for format response "+response, e);
        }
    }
}
