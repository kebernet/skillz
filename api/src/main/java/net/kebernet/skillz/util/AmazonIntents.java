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
public abstract class AmazonIntents {

    public String CANCEL = "AMAZON.CancelIntent";
    public String HELP = "AMAZON.HelpIntent";
    public String LOOP_OFF = "AMAZON.LoopOffIntent";
    public String LOOP_ON = "AMAZON.LoopOnIntent";
    public String NEXT = "AMAZON.NextIntent";
    public String NO = "AMAZON.NoIntent";
    public String PAUSE = "AMAZON.PauseIntent";
    public String PREVIOUS = "AMAZON.PreviousIntent";
    public String REPEAT = "AMAZON.RepeatIntent";
    public String RESUME = "AMAZON.ResumeIntent";
    public String SHUFFLE_OFF = "AMAZON.ShuffleOffIntent";
    public String SHUFFLE_ON = "AMAZON.ShuffleOnIntent";
    public String START_OVER = "AMAZON.StartOverIntent";
    public String STOP = "AMAZON.StopIntent";
    public String YES = "AMAZON.YesIntent";
}
