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

package com.damienwesterman.defensedrill.mvc.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.damienwesterman.defensedrill.mvc.util.Constants;
import com.damienwesterman.defensedrill.mvc.web.BackendResponse;
import com.damienwesterman.defensedrill.mvc.web.dto.SubCategoryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for interacting with the SubCategory backend.
 */
@Service
@Slf4j
public class SubCategoryApiService extends AbstractCategoryApiService<SubCategoryDTO> {
    private final static String API_ENDPOINT = Constants.REST_API_ADDRESS + "/sub_category";

    public SubCategoryApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper, API_ENDPOINT);
    }

    @Override
    public BackendResponse<SubCategoryDTO[]> getAll() {
        BackendResponse<SubCategoryDTO[]> ret;
        ResponseEntity<String> response = restTemplate.getForEntity(API_ENDPOINT, String.class);

        if (HttpStatus.OK == response.getStatusCode()) {
            try {
                ret = new BackendResponse<>(
                    response.getStatusCode(),
                    objectMapper.readValue(response.getBody(), SubCategoryDTO[].class),
                    null);
            } catch (JsonProcessingException e) {
                log.error(e.toString());
                ret = new BackendResponse<>(
                    HttpStatus.NO_CONTENT,
                    null,
                    null);
            }
        } else {
            // Status 204 NO_CONTENT
            ret = new BackendResponse<>(
                response.getStatusCode(),
                null,
                null);
        }

        return ret;
    }
}