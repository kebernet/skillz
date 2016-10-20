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
 * Values for the built-in Amazon slot types.
 */
@SuppressWarnings("unused")
public abstract class AmazonSlotTypes {

    public static final String NUMBER = "AMAZON.NUMBER";
    public static final String DATE = "AMAZON.DATE";
    public static final String TIME = "AMAZON.TIME";
    public static final String DURATION = "AMAZON.DURATION";
    public static final String FOUR_DIGIT_NUMBER = "AMAZON.FOUR_DIGIT_NUMBER";
    public static final String LITERAL = "AMAZON.LITERAL";


    /**
     * Austria focused slot types.
     */
    public static abstract class Austria {
        public static final String CITY = "AMAZON.AT_CITY";
        public static final String REGION = "AMAZON.AT_REGION";
    }

    /**
     * Germany focused slot types.
     */
    public static abstract class Germany {
        public static final String CITY = "AMAZON.DE_CITY";
        public static final String FIRST_NAME = "AMAZON.DE_FIRST_NAME";
        public static final String REGION = "AMAZON.DE_REGION";
    }

    /**
     * Europe focused slot types.
     */
    public static abstract class Europe {
        public static final String CITY = "AMAZON.EUROPE_CITY";
    }

    /**
     * United Kingdom focused slot types.
     */
    public static abstract class UnitedKingdom {
        public static final String CITY = "AMAZON.GB_CITY";
        public static final String FIRST_NAME = "AMAZON.GB_FIRST_NAME";
        public static final String REGION = "AMAZON.GB_REGION";
    }

    /**
     * United States focused slot types.
     */
    public static abstract class UnitedStates {
        public static final String CITY = "AMAZON.US_CITY";
        public static final String FIRST_NAME = "AMAZON.US_FIRST_NAME";
        public static final String STATE = "AMAZON.US_STATE";
    }


}
