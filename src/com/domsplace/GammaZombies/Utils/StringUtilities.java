/*
 * Copyright 2013 Dominic Masters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.GammaZombies.Utils;

/**
 *
 * @author Dominic Masters
 */
public class StringUtilities {
    public static final String[] VOWELS = new String[] {
        "a", "e", "i", "o", "u"
    };
    
    public static boolean doesStartWithVowel(String x) {
        for(String vowel : VOWELS) {
            if(x.toLowerCase().startsWith(vowel.toLowerCase())) return true;
        }
        return false;
    }
    
    public static String anOrA(String x) {
        return (doesStartWithVowel(x) ? "An" : "A");
    }
}
