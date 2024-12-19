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

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.damienwesterman.defensedrill.mvc.web.dto.DrillResponseDTO;

import lombok.RequiredArgsConstructor;

/**
 * Service to interact with the Drill Rest API backend.
 */
@Service
@RequiredArgsConstructor
public class DrillApiService {
    private final RestTemplate restTemplate;

    /**
     * Get all Drills from the database.
     *
     * @return List of Drills.
     */
    public ResponseEntity<DrillResponseDTO[]> getAll() {
        return restTemplate.getForEntity("lb://rest-api/drill", 
            DrillResponseDTO[].class);
    }
}
