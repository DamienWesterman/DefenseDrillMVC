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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
    private final static String NAME_ENDPOINT = "/name/{name}";

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
        ResponseEntity<D> response =
            restTemplate.getForEntity(
                apiEndpoint + ID_ENDPOINT,
                categoryClass,
                id
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retError = null;
                retDto = response.getBody();
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
                retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                break;
        }

        return new BackendResponse<D>(retStatus, retDto, retError);
    }

    /**
     * Update an AbstractCategory.
     *
     * @param abstractCategory AbstractCategory to update.
     * @return BackendResponse containing the updated AbstractCategory.
     */
    @NonNull
    public BackendResponse<D> update(@NonNull AbstractCategoryDTO abstractCategory) {
        HttpStatusCode retStatus = null;
        D retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String> response =
            restTemplate.exchange(
                apiEndpoint + ID_ENDPOINT,
                HttpMethod.PUT,
                new HttpEntity<AbstractCategoryDTO>(abstractCategory),
                String.class,
                abstractCategory.getId()
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retError = null;

                // Extract returned object
                try {
                    retDto = objectMapper.readValue(response.getBody(), categoryClass);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                }
                break;

            case BAD_REQUEST:
                retStatus = HttpStatus.BAD_REQUEST;
                retDto = null;
                try {
                    retError = objectMapper.readValue(response.getBody(), ErrorMessageDTO.class);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                }
                break;

            case INTERNAL_SERVER_ERROR:
                // Fallthrough intentional
            default:
                retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                retDto = null;
                retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                break;
        }

        return new BackendResponse<D>(retStatus, retDto, retError);
    }

    /**
     * Delete an AbstractCategory by its ID. Always presumed to work.
     *
     * @param id ID of the AbstractCategory to delete.
     */
    public void delete(@NonNull Long id) {
        restTemplate.delete(apiEndpoint + ID_ENDPOINT, id);
    }

    /**
     * Create a new AbstractCategory.
     *
     * @param abstractCategory AbstractCategory to create.
     * @return BackendResponse containing the created AbstractCategory.
     */
    @NonNull
    public BackendResponse<D> create(@NonNull D abstractCategory) {
        HttpStatusCode retStatus = null;
        D retDto = null;
        ErrorMessageDTO retError = null;
        abstractCategory.setId(null);
        ResponseEntity<String> response =
            restTemplate.postForEntity(
                apiEndpoint,
                abstractCategory,
                String.class
            );

        switch((HttpStatus) response.getStatusCode()) {
            case CREATED:
                retStatus = HttpStatus.CREATED;
                retError = null;

                // Extract returned object
                try {
                    retDto = objectMapper.readValue(response.getBody(), categoryClass);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                }
                break;

            case BAD_REQUEST:
                retStatus = HttpStatus.BAD_REQUEST;
                retDto = null;
                try {
                    retError = objectMapper.readValue(response.getBody(), ErrorMessageDTO.class);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                }
                break;

            case INTERNAL_SERVER_ERROR:
                // Fallthrough intentional
            default:
                retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                retDto = null;
                retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                break;
        }

        return new BackendResponse<D>(retStatus, retDto, retError);
    }

    /**
     * Find one AbstractCategoryEntity by name (case insensitive).
     *
     * @param name Name of the AbstractCategory.
     * @return BackendResponse containing the found AbstractCategory.
     */
    @NonNull
    public BackendResponse<D> get(@NonNull String name) {
        HttpStatusCode retStatus = null;
        D retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<D> response =
            restTemplate.getForEntity(
                apiEndpoint + NAME_ENDPOINT,
                categoryClass,
                name
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retError = null;
                retDto = response.getBody();
                break;

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Category \"" + name + "\" does not exist."
                );
                break;

            case INTERNAL_SERVER_ERROR:
                // Fallthrough intentional
            default:
                retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                retDto = null;
                retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                break;
        }

        return new BackendResponse<D>(retStatus, retDto, retError);
    }
}
