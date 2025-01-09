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

package com.damienwesterman.defensedrill.mvc.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for updating a Drill.
 * <br><br>
 * NOTE: Any changes here must also be reflected in the RestAPI repo.
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillUpdateDTO {
    @NonNull
    private String name;

    @Nullable
    @JsonProperty("categories")
    private List<Long> categoryIds;

    @Nullable
    @JsonProperty("sub_categories")
    private List<Long> subCategoryIds;

    @Nullable
    @JsonProperty("related_drills")
    private List<Long> relatedDrills;

    @Nullable
    private List<InstructionsDTO> instructions;

    /**
     * Parameterized constructor from the backend's response.
     *
     * @param drill Backend DrillResponseDTO
     */
    public DrillUpdateDTO(DrillResponseDTO drill) {
        this.name = drill.getName();
        this.categoryIds = drill.getCategories().stream()
            .map(AbstractCategoryDTO::getId)
            .collect(Collectors.toList());
        this.subCategoryIds = drill.getSubCategories().stream()
            .map(AbstractCategoryDTO::getId)
            .collect(Collectors.toList());
        this.relatedDrills = drill.getRelatedDrills().stream()
            .map(DrillRelatedDTO::getId)
            .collect(Collectors.toList());
        this.instructions = drill.getInstructions();
    }

    /**
     * Fill in this DTO's information using DrillCreateHtmxDTO.
     *
     * @param drill DrillCreateHtmxDTO object.
     */
    public void fillInfo(DrillFormDTO drill) {
        this.name = drill.getName();
        this.categoryIds = drill.getCategoryIds();
        this.subCategoryIds = drill.getSubCategoryIds();
        this.relatedDrills = drill.getRelatedDrillIds();
        // Instructions are not saved in DrillCreateHtmxDTO, leave as it was
    }
}
