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
import com.damienwesterman.defensedrill.mvc.web.dto.CategoryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for interacting with the Category backend.
 */
@Service
@Slf4j
public class CategoryApiService extends AbstractCategoryApiService<CategoryDTO> {
    private final static String API_ENDPOINT = Constants.REST_API_ADDRESS + "/category";

    public CategoryApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper, API_ENDPOINT);
    }

    @Override
    public BackendResponse<CategoryDTO[]> getAll() {
        BackendResponse<CategoryDTO[]> ret;
        ResponseEntity<String> response =
            restTemplate.getForEntity(API_ENDPOINT, String.class);

        switch( (HttpStatus) response.getStatusCode()) {
            case OK:
                try {
                    ret = new BackendResponse<>(
                        response.getStatusCode(),
                        objectMapper.readValue(response.getBody(), CategoryDTO[].class),
                        null);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    ret = new BackendResponse<>(
                        HttpStatus.NO_CONTENT,
                        null,
                        null);
                }
                break;
            case NO_CONTENT:
                // Fallthrough intentional
            default:
                ret = new BackendResponse<>(
                    response.getStatusCode(),
                    null,
                    null);
                break;
        }

        return ret;
    }
}
