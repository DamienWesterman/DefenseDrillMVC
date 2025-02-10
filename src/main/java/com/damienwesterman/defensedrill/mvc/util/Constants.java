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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.damienwesterman.defensedrill.mvc.web.dto.ErrorMessageDTO;

/**
 * Public static constants common to the entire application.
 */
@Component
public class Constants {
    public Constants(Environment environment) {
        SERVER_IP_ADDRESS = environment.getProperty("LOCAL_IP_ADDRESS", "localhost");
    }

    public final static String REST_API_BASE_ADDRESS = "lb://rest-api";
    public final static String SECURITY_API_BASE_ADDRESS = "lb://security/user";

    public final static ErrorMessageDTO GENERIC_INTERNAL_ERROR_DTO = new ErrorMessageDTO(
        Constants.GENERIC_INTERNAL_ERROR,
        Constants.GENERIC_INTERNAL_ERROR_MESSAGE
    );

    // User feedback messages
    public final static String GENERIC_INTERNAL_ERROR = "Internal Error";
    public final static String GENERIC_INTERNAL_ERROR_MESSAGE = "Please try again later.";
    public final static String NOT_FOUND_ERROR = "Not Found";

    public static String SERVER_IP_ADDRESS;

    public static enum UserRoles {
        USER("USER"),
        ADMIN("ADMIN");

        private String roleString;

        UserRoles(String roleString) {
            this.roleString = roleString;
        }

        public String getStringRepresentation() {
            return this.roleString;
        }
    }

    public static final List<String> ALL_ROLES_LIST = List.of(UserRoles.values()).stream()
        .map(UserRoles::getStringRepresentation)
        .collect(Collectors.toList());
}
