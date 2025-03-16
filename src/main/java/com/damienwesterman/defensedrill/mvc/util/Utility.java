/****************************\
 *      ________________      *
 *     /  _             \     *
 *     \   \ |\   _  \  /     *
 *      \  / | \ / \  \/      *
 *      /  \ | / | /  /\      *
 *     /  _/ |/  \__ /  \     *
 *     \________________/     *
 *                            *
 \****************************/
/*
 * Copyright 2025 Damien Westerman
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

package com.damienwesterman.defensedrill.mvc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {
    /**
     * Convert a plain string to a URI encoded string. This is best for concatenated
    * strings or strings containing user input.
    *
    * @param plainString String to convert to URI
    * @return URI encoded String
    */
    public static String convertToUri(String plainString) {
        try {
            return URLEncoder.encode(plainString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // We should never get here
            log.error("UnsupportedEncodingException", e);
            return "";
        }
    }
}
