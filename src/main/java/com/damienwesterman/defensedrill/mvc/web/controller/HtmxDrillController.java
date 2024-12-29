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

package com.damienwesterman.defensedrill.mvc.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.damienwesterman.defensedrill.mvc.service.CategoryApiService;
import com.damienwesterman.defensedrill.mvc.service.DrillApiService;
import com.damienwesterman.defensedrill.mvc.service.SubCategoryApiService;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillResponseDTO;

import lombok.RequiredArgsConstructor;

/**
 * Controller for all HTMX element access
 */
@Controller
@RequestMapping("/htmx/drill")
@RequiredArgsConstructor
public class HtmxDrillController {
    private final DrillApiService drillApiService;
    private final CategoryApiService categoryApiService;
    private final SubCategoryApiService subCategoryApiService;

    @GetMapping("/view")
    public String viewAllDrills(Model model) {
        var response = drillApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<DrillResponseDTO> drills = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Total Drills: " + drills.size());
            model.addAttribute("buttonText", "Details");

            // Add drills to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, drillId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/drill/view/" + drillId
                );
            List<Map<String, String>> listItems = new ArrayList<>(drills.size());
            for (var drill : drills) {
                listItems.add(
                    createListItem.apply(drill.getName(), drill.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/view/{id}")
    public String viewOneDrill(Model model, @PathVariable Long id) {
        var response = drillApiService.get(id);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            var drill = response.getResponse();
            model.addAttribute("name", drill.getName());
            // ID already included
            model.addAttribute("description", "temp");
            model.addAttribute("instructionsList", drill.getInstructions());
            // TODO: Change endpoint to retrieve a list of AbstractCategoryDTO and RelatedDrillDTO (only name and ID) instead of lists of Longs
            model.addAttribute("categoriesList", drill.getCategories());
            model.addAttribute("subCategoriesList", drill.getSubCategories());
            model.addAttribute("relatedDrillsList", drill.getRelatedDrillIds());
        }
        // TODO: FIXME FINISH THIS AND THE CORRESPONDING html (using the newly updated API)

        model.addAttribute("backEndpoint", "/htmx/drill/view");

        return "layouts/htmx/drill_view_one :: drillDetails";
    }
}
