package net.kebernet.skillz.format;

import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.ui.OutputSpeech;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.io.CharStreams;
import net.kebernet.skillz.builder.PlainTextOutputBuilder;
import net.kebernet.skillz.builder.SsmlOutputBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 * This is a Bundle that just reads from files and doesn't transform them in any way.
 */
public class ConstantBundle implements Bundle {
    private static final Logger LOGGER = Logger.getLogger(ResourceBundle.class.getCanonicalName());
    private final String bundleName;
    private String ssml;
    private String txt;
    private String card;

    public ConstantBundle(String bundleName, String languageCode) {
        this.bundleName = bundleName;
        this.ssml = readFromResource(bundleName, languageCode, "ssml");
        this.txt = readFromResource(bundleName, languageCode, "txt");
        this.card = readFromResource(bundleName, languageCode, "card");
    }

    private static String readFromResource(String resourceBaseName, String languageCode, String fileType){
        URL url = null;
        try {
            url = ConstantBundle.class.getResource(resourceBaseName+"."+languageCode+"."+fileType);
            url = url == null ? MustacheBundle.class.getResource(resourceBaseName+"."+fileType) : url;
            if(url != null) {
                return CharStreams.toString(new InputStreamReader(url.openStream(), "utf-8"));
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to parse " + url.toExternalForm(), e);
        }
        return null;
    }


    @Override
    public OutputSpeech createOutputSpeech(Object response, SpeechletRequest request, Session session) {
        return ssml != null ? SsmlOutputBuilder.withSsml(ssml).withId("urn:ssml:"+this.bundleName).build()
                : PlainTextOutputBuilder.withText(txt).withId("urn:txt:"+this.bundleName).build();
    }

    @Override
    public String createCardContent(Object response, SpeechletRequest request, Session session) {
        return card;
    }
}
