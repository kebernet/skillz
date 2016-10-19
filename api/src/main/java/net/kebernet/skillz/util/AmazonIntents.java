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

/**
 * A set of constants for the well-known Amazon intent types.
 * <p>
 *     See also:
 *     <a href="https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/implementing-the-built-in-intents#available-built-in-intents">the official docs</a>.
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AmazonIntents {

    public static final String CANCEL = "AMAZON.CancelIntent";
    public static final String HELP = "AMAZON.HelpIntent";
    public static final String LOOP_OFF = "AMAZON.LoopOffIntent";
    public static final String LOOP_ON = "AMAZON.LoopOnIntent";
    public static final String NEXT = "AMAZON.NextIntent";
    public static final String NO = "AMAZON.NoIntent";
    public static final String PAUSE = "AMAZON.PauseIntent";
    public static final String PREVIOUS = "AMAZON.PreviousIntent";
    public static final String REPEAT = "AMAZON.RepeatIntent";
    public static final String RESUME = "AMAZON.ResumeIntent";
    public static final String SHUFFLE_OFF = "AMAZON.ShuffleOffIntent";
    public static final String SHUFFLE_ON = "AMAZON.ShuffleOnIntent";
    public static final String START_OVER = "AMAZON.StartOverIntent";
    public static final String STOP = "AMAZON.StopIntent";
    public static final String YES = "AMAZON.YesIntent";
}
