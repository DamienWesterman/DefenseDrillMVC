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

import org.springframework.core.GenericTypeResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import com.damienwesterman.defensedrill.mvc.web.dto.AbstractCategoryDTO;

import lombok.RequiredArgsConstructor;

/**
 * Abstract superclass for {@link CategoryApiService} and {@link SubCategoryApiService}.
 */
@RequiredArgsConstructor
public abstract class AbstractCategoryApiService<D extends AbstractCategoryDTO> {
    @SuppressWarnings("unchecked")
    private final Class<D> CATEGORY_CLASS =
        (Class<D>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractCategoryDTO.class);
    protected final RestTemplate restTemplate;
    private final String API_ENDPOINT;

    private final static String ID_ENDPOINT = "/id/{id}";

    /**
     * Get all AbstractCategories from the database.
     *
     * @return List of abstract AbstractCategories.
     */
    public abstract ResponseEntity<D[]> getAll();
    /* 
     * The above is difficult to implement here because the following doesn't work:
     *   return restTemplate.getForEntity(API_ENDPOINT, CATEGORY_CLASS[].class);
     */

     public ResponseEntity<D> get(@NonNull Long id) {
        return restTemplate.getForEntity(
            API_ENDPOINT + ID_ENDPOINT,
            CATEGORY_CLASS,
            id);
     }
}
