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

package com.damienwesterman.defensedrill.mvc.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.damienwesterman.defensedrill.mvc.util.Constants;
import com.damienwesterman.defensedrill.mvc.web.BackendResponse;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillResponseDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.ErrorMessageDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.UserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to interact with the User Rest API backend.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final static String API_ENDPOINT = Constants.SECURITY_API_BASE_ADDRESS;

    /**
     * TODO: All doc comments
     * @return
     */
    @NonNull
    public BackendResponse<UserInfoDTO[]> getAll() {
        HttpStatusCode retStatus = null;
        UserInfoDTO[] retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<UserInfoDTO[]> response =
            restTemplate.getForEntity(API_ENDPOINT, UserInfoDTO[].class);

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retDto = response.getBody();
                retError = null;
                break;

            case NO_CONTENT:
                retStatus = HttpStatus.NO_CONTENT;
                retDto = new UserInfoDTO[] { /* Empty */ };
                retError = null;
                break;

            case INTERNAL_SERVER_ERROR:
                // Fallthrough intentional
            default:
                retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                retDto = null;
                retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                break;
        }

        return new BackendResponse<UserInfoDTO[]>(retStatus, retDto, retError);
    }
}
