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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import com.damienwesterman.defensedrill.mvc.util.Constants;
import com.damienwesterman.defensedrill.mvc.web.BackendResponse;
import com.damienwesterman.defensedrill.mvc.web.dto.AbstractCategoryDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.ErrorMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract superclass for {@link CategoryApiService} and {@link SubCategoryApiService}.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCategoryApiService<D extends AbstractCategoryDTO> {
    private final Class<D> categoryClass;
    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;
    private final String apiEndpoint;

    private final static String ID_ENDPOINT = "/id/{id}";

    /*
     * The below is difficult to implement here because the following doesn't work:
     *   objectMapper.readValue(response.getBody(), CATEGORY_CLASS[].class);
     */
    /**
     * Get all AbstractCategories from the database.
     *
     * @return BackendResponse containing a List of AbstractCategories.
     */
    @NonNull
    public abstract BackendResponse<D[]> getAll();

    /**
     * Find one AbstractCategory by ID.
     *
     * @param id ID of the AbstractCategory.
     * @return BackenResponse containing the found AbstractCategory.
     */
    @NonNull
    public BackendResponse<D> get(@NonNull Long id) {
        HttpStatusCode retStatus = null;
        D retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String> response =
            restTemplate.getForEntity(
                apiEndpoint + ID_ENDPOINT,
                String.class,
                id
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retError = null;

                // Extract the desired DTO
                try {
                    retDto = objectMapper.readValue(response.getBody(), categoryClass);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());

                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = new ErrorMessageDTO(
                        Constants.GENERIC_INTERNAL_ERROR,
                        Constants.GENERIC_INTERNAL_ERROR_MESSAGE
                    );
                }
                break;

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Category " + id + " does not exist."
                );
                break;

            case INTERNAL_SERVER_ERROR:
                // Fallthrough intentional
            default:
                retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.GENERIC_INTERNAL_ERROR,
                    Constants.GENERIC_INTERNAL_ERROR_MESSAGE
                );
                break;
        }

        return new BackendResponse<D>(retStatus, retDto, retError);
    }


}
