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

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.damienwesterman.defensedrill.mvc.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for interacting with the Category backend.
 */
@Service
public class CategoryApiService extends AbstractCategoryApiService{
    private final static String API_ENDPOINT = Constants.REST_API_BASE_ADDRESS + "/category";

    public CategoryApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper, API_ENDPOINT);
    }
}
