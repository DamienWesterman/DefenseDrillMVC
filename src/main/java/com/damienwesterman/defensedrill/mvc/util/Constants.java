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
* Copyright 2024 Damien Westerman
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

import com.damienwesterman.defensedrill.mvc.web.dto.ErrorMessageDTO;

/**
 * Public static constants common to the entire application.
 */
public class Constants {
    public final static String REST_API_BASE_ADDRESS = "lb://rest-api";

    public final static ErrorMessageDTO GENERIC_INTERNAL_ERROR_DTO = new ErrorMessageDTO(
        Constants.GENERIC_INTERNAL_ERROR,
        Constants.GENERIC_INTERNAL_ERROR_MESSAGE
    );

    // User feedback messages
    public final static String GENERIC_INTERNAL_ERROR = "Internal Error";
    public final static String GENERIC_INTERNAL_ERROR_MESSAGE = "Please try again later.";
    public final static String NOT_FOUND_ERROR = "Not Found";
}
