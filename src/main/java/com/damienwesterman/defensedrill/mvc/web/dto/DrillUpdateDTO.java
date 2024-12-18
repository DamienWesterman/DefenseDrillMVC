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

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for updating a Drill.
 * <br><br>
 * NOTE: Any changes here must also be reflected in the RestAPI repo.
 */
@Data
public class DrillUpdateDTO {
    @NotEmpty
    @Size(min = 1, max = 255)
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
}
