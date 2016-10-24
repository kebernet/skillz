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
package net.kebernet.skillz.util;

import com.google.common.base.Joiner;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.invoker.runtime.impl.InvokableMethod;
import net.kebernet.skillz.annotation.Intent;
import net.kebernet.skillz.annotation.Utterances;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a utility class that checks the annotation data on a skill and outputs
 * the utterances text for the Amazon system.
 */
public class OutputUtterances {

    private final IntrospectionData data;

    /**
     * Constructs a new instance around a set of introspection data.
     * @param data the IntrospectionData for the skill.
     */
    public OutputUtterances(IntrospectionData data){
        this.data = data;
    }

    /**
     * Builds the utterances as a String
     * @return String of utterance templates.
     */
    public String build(){
        List<String> lines = data.getMethods()
                .stream()
                // Only intents
                .filter(m-> m.getNativeMethod().getAnnotation(Intent.class) != null)
                // Grouped so overloaded methods are together
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                // Mapped to each utterance line in the class
                .flatMap(OutputUtterances::formatUtterances)
                //Into the list
                .collect(Collectors.toList());
        //Then line separated.
        return Joiner.on('\n').join(lines);
    }

    /**
     * Writes the utterances to a writer
     * @param writer Target writer.
     */
    public void writeTo(PrintWriter writer) {
        writer.println(build());
        writer.flush();
    }

    /**
     * Writes the utterances to an output stream
     * @param stream The stream to write to.
     */
    @SuppressWarnings("WeakerAccess")
    public void writeTo(OutputStream stream){
        writeTo(new PrintWriter(new OutputStreamWriter(stream, Charset.forName("UTF-8"))));
    }

    private static Stream<String> formatUtterances(InvokableMethod invokableMethod) {
        return Optional.ofNullable(invokableMethod.getNativeMethod().getAnnotation(Utterances.class))
                .map(utterances ->
                        Arrays.stream(utterances.value())
                        .map(s->
                                new StringBuilder(invokableMethod.getName())
                                        .append(" ")
                                        .append(s)
                                        .toString()
                        ))
                .orElse(Stream.empty());
    }
}
