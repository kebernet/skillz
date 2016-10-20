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
package net.kebernet.skillz.dropwizard;

import net.kebernet.skillz.annotation.Intent;
import net.kebernet.skillz.annotation.ResponseFormatter;
import net.kebernet.skillz.annotation.Skill;
import net.kebernet.skillz.annotation.Slot;
import net.kebernet.skillz.annotation.Utterances;
import net.kebernet.skillz.util.AmazonSlotTypes;
import net.kebernet.skillz.util.Formatters;

/**
 * Created by rcooper on 10/20/16.
 */
@Skill(path="/test1")
public class TestSkill1 {

    @Intent("TestyMcTestFace")
    @ResponseFormatter(Formatters.SimplePlainTextTell.class)
    @Utterances({
            "I am {name}",
            "my name is {name}",
            "hello, i'm {name}"
    })
    public String test(@Slot(name="name", type= AmazonSlotTypes.UnitedStates.FIRST_NAME) String name){
        return "waddup, "+name;
    }
}
