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
package net.kebernet.skillz.impl;

import com.amazon.speech.speechlet.Application;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.User;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import net.kebernet.invoker.runtime.impl.IntrospectionData;
import net.kebernet.invoker.runtime.impl.InvokableMethod;
import net.kebernet.skillz.FormatterMappings;
import net.kebernet.skillz.SkillzException;
import net.kebernet.skillz.annotation.ExpressionValue;
import net.kebernet.skillz.annotation.Intent;
import net.kebernet.skillz.annotation.Launched;
import net.kebernet.skillz.annotation.ResponseFormatter;
import net.kebernet.skillz.annotation.SessionEnded;
import net.kebernet.skillz.annotation.SessionStarted;
import net.kebernet.skillz.annotation.Skill;
import net.kebernet.skillz.annotation.Slot;
import net.kebernet.skillz.builder.PlainTextOutputBuilder;
import net.kebernet.skillz.util.Formatters;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleSpeechletInvocationTests {

    private Registry registry = new Registry(new HashSet<>(Collections.singletonList(InvokedTestSkill.class)));
    private Session session = Session.builder().withSessionId("sessionid")
            .withAttributes(new HashMap<>())
            .withIsNew(true)
            .withApplication(new Application("test"))
            .withUser(User.builder().withUserId("foo").withAccessToken("token").build())
            .build();

    @Test
    public void onSessionStarted() throws Exception {
        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);
        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        SessionStartedRequest request = SessionStartedRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date()).build();
        Stopwatch stopwatch = Stopwatch.createStarted();
        speechlet.onSessionStarted(request, session);
        Logger.getAnonymousLogger().info("onSessionStarted complete in "+stopwatch.elapsed(TimeUnit.MILLISECONDS));
        assertTrue(skill.onSessionStarted.get());
    }

    @Test
    public void onLaunch() throws Exception {
        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);

        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        LaunchRequest request = LaunchRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date()).build();
        SpeechletResponse response = speechlet.onLaunch(request, session);
        assertTrue(skill.onLaunch.get());
        assertEquals("launch", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());
    }

    @Test
    public void onIntent() throws Exception {

        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);

        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        Map<String, com.amazon.speech.slu.Slot> slots = new HashMap<>();
        com.amazon.speech.slu.Slot name = com.amazon.speech.slu.Slot.builder()
                .withName("arg")
                .withValue("bill")
                .build();
        slots.put("arg", name);
        com.amazon.speech.slu.Intent intent = com.amazon.speech.slu.Intent.builder()
                .withName("intent")
                .withSlots(slots)
                .build();
        IntentRequest request = IntentRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();

        SpeechletResponse response = speechlet.onIntent(request, session);
        assertTrue(skill.onIntent.get());
        assertEquals("hello, bill", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

    }

    @Test(expected = SpeechletException.class)
    public void onUnknownIntent() throws Exception {
        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);
        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));
        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());

        com.amazon.speech.slu.Intent intent = com.amazon.speech.slu.Intent.builder()
                .withName("missing")
                .build();
        IntentRequest request = IntentRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();

        speechlet.onIntent(request, session);
    }
    @Test(expected = SkillzException.class)
    public void onExceptionIntent() throws Exception {
        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);
        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));
        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());

        com.amazon.speech.slu.Intent intent = com.amazon.speech.slu.Intent.builder()
                .withName("throws")
                .build();
        IntentRequest request = IntentRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();

        speechlet.onIntent(request, session);
    }


    @Test
    public void onSelectInt() throws Exception {

        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);

        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        Map<String, com.amazon.speech.slu.Slot> slots = new HashMap<>();
        com.amazon.speech.slu.Slot name = com.amazon.speech.slu.Slot.builder()
                .withName("int")
                .withValue("5")
                .build();
        slots.put("int", name);
        com.amazon.speech.slu.Intent intent = com.amazon.speech.slu.Intent.builder()
                .withName("select")
                .withSlots(slots)
                .build();
        IntentRequest request = IntentRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();

        SpeechletResponse response = speechlet.onIntent(request, session);
        assertEquals("int", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

    }

    @Test
    public void onSelectString() throws Exception {

        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);

        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        Map<String, com.amazon.speech.slu.Slot> slots = new HashMap<>();
        com.amazon.speech.slu.Slot slot = com.amazon.speech.slu.Slot.builder()
                .withName("string")
                .withValue("foo")
                .build();
        slots.put("string", slot);
        com.amazon.speech.slu.Intent intent = com.amazon.speech.slu.Intent.builder()
                .withName("select")
                .withSlots(slots)
                .build();
        IntentRequest request = IntentRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();

        SpeechletResponse response = speechlet.onIntent(request, session);
        assertEquals("string", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

    }

    @Test
    public void onSelectIntAndString() throws Exception {

        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);

        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        Map<String, com.amazon.speech.slu.Slot> slots = new HashMap<>();
        com.amazon.speech.slu.Slot slot = com.amazon.speech.slu.Slot.builder()
                .withName("string")
                .withValue("foo")
                .build();
        slots.put("string", slot);
        slot  = com.amazon.speech.slu.Slot.builder()
                .withName("int")
                .withValue("5")
                .build();
        slots.put("int", slot);
        com.amazon.speech.slu.Intent intent = com.amazon.speech.slu.Intent.builder()
                .withName("select")
                .withSlots(slots)
                .build();
        IntentRequest request = IntentRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();

        SpeechletResponse response = speechlet.onIntent(request, session);
        assertEquals("intAndString", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());
    }

    @Test
    public void onSelectLongWithDirectReturn() throws Exception {

        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);

        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        Map<String, com.amazon.speech.slu.Slot> slots = new HashMap<>();
        com.amazon.speech.slu.Slot slot = com.amazon.speech.slu.Slot.builder()
                .withName("long")
                .withValue("5")
                .build();
        slots.put("long", slot);
        com.amazon.speech.slu.Intent intent = com.amazon.speech.slu.Intent.builder()
                .withName("select")
                .withSlots(slots)
                .build();
        IntentRequest request = IntentRequest.builder()
                .withRequestId("id")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();

        SpeechletResponse response = speechlet.onIntent(request, session);
        assertEquals("long", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());
    }



    @Test
    public void onSessionEnded() throws Exception {
        IntrospectionData data = registry.getDataForPath("/invoked").orElseThrow(RuntimeException::new);

        final ArrayListMultimap<String, InvokableMethod> methods = ArrayListMultimap.create();
        data.getMethods().forEach(m->methods.put(m.getName(), m));

        InvokedTestSkill skill = new InvokedTestSkill();
        DynamicSpeechlet speechlet = new DynamicSpeechlet(methods, data, new FormatterMappings(),
                registry, skill, new DefaultTypeFactory());
        SessionEndedRequest request = SessionEndedRequest.builder()
                .withRequestId("id")
                .withReason(SessionEndedRequest.Reason.USER_INITIATED)
                .withTimestamp(new Date()).build();
        Stopwatch stopwatch = Stopwatch.createStarted();
        speechlet.onSessionEnded(request, session);
        Logger.getAnonymousLogger().info("onSessionEnded complete in "+stopwatch.elapsed(TimeUnit.MILLISECONDS));
        assertTrue(skill.onSessionEnded.get());
        stopwatch = Stopwatch.createStarted();
        speechlet.onSessionEnded(request, session);
        Logger.getAnonymousLogger().info("onSessionEnded, second run complete in "+stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }


    @SuppressWarnings({"unused", "WeakerAccess"})
    @Skill(path="/invoked")
    public static class InvokedTestSkill {

        AtomicBoolean onSessionStarted = new AtomicBoolean(false);
        AtomicBoolean onLaunch = new AtomicBoolean(false);
        AtomicBoolean onSessionEnded = new AtomicBoolean(false);
        AtomicBoolean onIntent = new AtomicBoolean(false);

        @SessionStarted
        public void start(){
            onSessionStarted.set(true);
        }

        @SessionEnded
        public void end(@ExpressionValue("request.reason") String reason){
            assertEquals("USER_INITIATED", reason);
            onSessionEnded.set(true);
        }

        @Launched
        @ResponseFormatter(Formatters.SimplePlainTextTell.class)
        public String onLaunch(){
            onLaunch.set(true);
            return "launch";
        }

        @Intent("intent")
        @ResponseFormatter(Formatters.SimplePlainTextTell.class)
        public String onIntent(@Slot(name="arg") String name){
            onIntent.set(true);
            return "hello, "+name;
        }

        @Intent("throws")
        public String onThrows() {
            throw new IllegalStateException("Whatever.");
        }

        @Intent("select")
        @ResponseFormatter(Formatters.SimplePlainTextTell.class)
        public String select(@Slot(name="int") int intValue){
            return "int";
        }

        @Intent("select")
        @ResponseFormatter(Formatters.SimplePlainTextTell.class)
        public String select(@Slot(name="string") String stringValue){
            return "string";
        }

        @Intent("select")
        @ResponseFormatter(Formatters.SimplePlainTextTell.class)
        public String select(@Slot(name="int") int intValue, @Slot(name="string") String stringValue){
            return "intAndString";
        }

        @Intent("select")
        public SpeechletResponse select(@Slot(name="long") long longValue){
            return SpeechletResponse.newTellResponse(
                    PlainTextOutputBuilder.withText("long")
                    .build()
            );
        }



    }

}