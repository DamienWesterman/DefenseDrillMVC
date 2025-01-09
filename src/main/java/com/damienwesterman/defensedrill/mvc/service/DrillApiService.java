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

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.damienwesterman.defensedrill.mvc.util.Constants;
import com.damienwesterman.defensedrill.mvc.web.BackendResponse;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillCreateDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillResponseDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillUpdateDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.ErrorMessageDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.InstructionsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to interact with the Drill Rest API backend.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DrillApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final static String API_ENDPOINT = Constants.REST_API_BASE_ADDRESS + "/drill";
    private final static String ID_ENDPOINT = "/id/{id}";
    private final static String NAME_ENDPOINT = "/name/{name}";
    private final static String ADD_CATEGORY_ENDPOINT = "/add_category/{categoryId}";
    private final static String ADD_SUB_CATEGORY_ENDPOINT = "/add_sub_category/{categoryId}";
    private final static String INSTRUCTIONS_LIST_ENDPOINT = ID_ENDPOINT + "/how-to";
    private final static String INSTRUCTIONS_DETAILS_ENDPOINT = INSTRUCTIONS_LIST_ENDPOINT + "/{number}";

    /**
     * Find a Drill by its ID.
     *
     * @param id ID of the Drill to find.
     * @return BackendResponse containing the found Drill.
     */
    @NonNull
    public BackendResponse<DrillResponseDTO> get(@NonNull Long id) {
        HttpStatusCode retStatus = null;
        DrillResponseDTO retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<DrillResponseDTO> response =
            restTemplate.getForEntity(
                API_ENDPOINT + ID_ENDPOINT,
                DrillResponseDTO.class,
                id
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retDto = response.getBody();
                retError = null;
                break;

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Drill " + id + " does not exist."
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

        return new BackendResponse<DrillResponseDTO>(retStatus, retDto, retError);
    }

    /**
     * Update a Drill.
     *
     * @param id ID of the Drill to update.
     * @param drill Updated Drill.
     * @return BackendResponse containing the updated Drill.
     */
    @NonNull
    public BackendResponse<DrillResponseDTO> update(@NonNull Long id, @NonNull DrillUpdateDTO drill) {
        HttpStatusCode retStatus = null;
        DrillResponseDTO retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String> response =
            restTemplate.exchange(
                API_ENDPOINT + ID_ENDPOINT,
                HttpMethod.PUT,
                new HttpEntity<DrillUpdateDTO>(drill),
                String.class,
                id
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                // Extract returned object
                try {
                    retDto = objectMapper.readValue(response.getBody(), DrillResponseDTO.class);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                }
                retError = null;
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

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Drill " + id + " does not exist."
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

        return new BackendResponse<DrillResponseDTO>(retStatus, retDto, retError);
    }

    /**
     * Update a list of drills to add the category.
     *
     * @param categoryId ID of the Category to add.
     * @param drillIds Drills to update.
     * @return Empty BackendResponse.
     */
    @NonNull
    public BackendResponse<String> updateCategories(@NonNull Long categoryId, @NonNull List<Long> drillIds) {
        HttpStatusCode retStatus = null;
        String retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String> response =
            restTemplate.exchange(
                API_ENDPOINT + ADD_CATEGORY_ENDPOINT,
                HttpMethod.PATCH,
                new HttpEntity<List<Long>>(drillIds),
                String.class,
                categoryId
            );

        switch((HttpStatus) response.getStatusCode()) {
            case NO_CONTENT:
                retStatus = HttpStatus.NO_CONTENT;
                retDto = "Successfully added Category to Drills.";
                retError = null;
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

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Category " + categoryId + " does not exist."
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

        return new BackendResponse<String>(retStatus, retDto, retError);
    }

    /**
     * Update a list of drills to add the sub-category.
     *
     * @param subCategoryId ID of the SubCategory to add.
     * @param drillIds Drills to update.
     * @return Empty BackendResponse.
     */
    @NonNull
    public BackendResponse<String> updateSubCategories(@NonNull Long subCategoryId, @NonNull List<Long> drillIds) {
        HttpStatusCode retStatus = null;
        String retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String> response =
            restTemplate.exchange(
                API_ENDPOINT + ADD_SUB_CATEGORY_ENDPOINT,
                HttpMethod.PATCH,
                new HttpEntity<List<Long>>(drillIds),
                String.class,
                subCategoryId
            );

        switch((HttpStatus) response.getStatusCode()) {
            case NO_CONTENT:
                retStatus = HttpStatus.NO_CONTENT;
                retDto = "Successfully added Sub-Category to Drills.";
                retError = null;
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

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Sub-Category " + subCategoryId + " does not exist."
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

        return new BackendResponse<String>(retStatus, retDto, retError);
    }

    /**
     * Dekete a Drill by its ID. Always presumed to work.
     *
     * @param id ID of the Drill to delete.
     */
    public void delete(@NonNull Long id) {
        restTemplate.delete(API_ENDPOINT + ID_ENDPOINT, id);
    }

    /**
     * Get all Drills from the database.
     *
     * @return BackendResponse containing a list of Drills.
     */
    @NonNull
    public BackendResponse<DrillResponseDTO[]> getAll() {
        HttpStatusCode retStatus = null;
        DrillResponseDTO[] retDto = null;
        ErrorMessageDTO retError = null;
         ResponseEntity<DrillResponseDTO[]> response =
            restTemplate.getForEntity(API_ENDPOINT, DrillResponseDTO[].class);

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retDto = response.getBody();
                retError = null;
                break;

            case NO_CONTENT:
                retStatus = HttpStatus.NO_CONTENT;
                retDto = new DrillResponseDTO[] { /* Empty */ };
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

        return new BackendResponse<DrillResponseDTO[]>(retStatus, retDto, retError);
    }

    /**
     * Create a new Drill.
     *
     * @param drill Drill to create.
     * @return BackendResponse containing the created Drill.
     */
    @NonNull
    public BackendResponse<DrillResponseDTO> create(@NonNull DrillCreateDTO drill) {
        HttpStatusCode retStatus = null;
        DrillResponseDTO retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String> response =
            restTemplate.postForEntity(
                API_ENDPOINT,
                drill,
                String.class
            );

        switch((HttpStatus) response.getStatusCode()) {
            case CREATED:
                retStatus = HttpStatus.CREATED;
                // Extract returned object
                try {
                    retDto = objectMapper.readValue(response.getBody(), DrillResponseDTO.class);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                }
                retError = null;
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

        return new BackendResponse<DrillResponseDTO>(retStatus, retDto, retError);
    }

    @NonNull
    public BackendResponse<DrillResponseDTO> get(@NonNull String name) {
        HttpStatusCode retStatus = null;
        DrillResponseDTO retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<DrillResponseDTO> response =
            restTemplate.getForEntity(
                API_ENDPOINT + NAME_ENDPOINT,
                DrillResponseDTO.class,
                name
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retDto = response.getBody();
                retError = null;
                break;

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Drill \"" + name + "\" does not exist."
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

        return new BackendResponse<DrillResponseDTO>(retStatus, retDto, retError);
    }

    /**
     * Retrieve a list of Instruction description for the given Drill ID>
     *
     * @param drillId Drill ID of the Instructions.
     * @return BackendResponse containing a list of strings of {@link InstructionsDTO#getDescription()}.
     */
    @NonNull
    public BackendResponse<String[]> getAllInstructions(@NonNull Long drillId) {
        HttpStatusCode retStatus = null;
        String[] retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String[]> response =
            restTemplate.getForEntity(
                API_ENDPOINT + INSTRUCTIONS_LIST_ENDPOINT,
                String[].class,
                drillId
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                retDto = response.getBody();
                retError = null;
                break;

            case NO_CONTENT:
                retStatus = HttpStatus.NO_CONTENT;
                retDto = new String[] { /* Empty */ };
                retError = null;
                break;

            case NOT_FOUND:
                retStatus = HttpStatus.NOT_FOUND;
                retDto = null;
                retError = new ErrorMessageDTO(
                    Constants.NOT_FOUND_ERROR,
                    "Drill " + drillId + " does not exist."
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

        return new BackendResponse<String[]>(retStatus, retDto, retError);
    }

    /**
     * Retrieve Instruction details based on the Drill's ID and the related Instruction's number.
     *
     * @param drillId Drill ID of the Instructions.
     * @param number Number of the Instructions.
     *               Corresponds to the index position found in {@link #getAllInstructions(Long)}
     * @return BackendResponse containing the InstructionsDTO.
     */
    @NonNull
    public BackendResponse<InstructionsDTO> getInstructionDetails(
                @NonNull Long drillId, @NonNull Long number) {
        HttpStatusCode retStatus = null;
        InstructionsDTO retDto = null;
        ErrorMessageDTO retError = null;
        ResponseEntity<String> response =
            restTemplate.getForEntity(
                API_ENDPOINT + INSTRUCTIONS_DETAILS_ENDPOINT,
                String.class,
                drillId,
                number
            );

        switch((HttpStatus) response.getStatusCode()) {
            case OK:
                retStatus = HttpStatus.OK;
                // Extract returned object
                try {
                    retDto = objectMapper.readValue(response.getBody(), InstructionsDTO.class);
                } catch (JsonProcessingException e) {
                    log.error(e.toString());
                    retStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    retDto = null;
                    retError = Constants.GENERIC_INTERNAL_ERROR_DTO;
                }
                retError = null;
                break;

            case NOT_FOUND:
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

        return new BackendResponse<InstructionsDTO>(retStatus, retDto, retError);
    }
}
