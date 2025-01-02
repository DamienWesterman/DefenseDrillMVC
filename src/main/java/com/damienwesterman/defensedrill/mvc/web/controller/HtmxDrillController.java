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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.damienwesterman.defensedrill.mvc.service.CategoryApiService;
import com.damienwesterman.defensedrill.mvc.service.DrillApiService;
import com.damienwesterman.defensedrill.mvc.service.SubCategoryApiService;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillCreateHtmxDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillResponseDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillUpdateDTO;

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
        if (response.hasError()) {
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
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            var drill = response.getResponse();
            model.addAttribute("name", drill.getName());
            // ID already included
            model.addAttribute("instructionsList", drill.getInstructions());
            model.addAttribute("categoriesList", drill.getCategories());
            model.addAttribute("subCategoriesList", drill.getSubCategories());
            model.addAttribute("relatedDrillsList", drill.getRelatedDrillIds());
        }

        model.addAttribute("backEndpoint", "/htmx/drill/view");

        return "layouts/htmx/drill_view_one :: drillDetails";
    }

    @GetMapping("/create")
    public String createDrillForm(Model model) {
        model.addAttribute("windowTitle", "Create New Drill");
        model.addAttribute("postEndpoint", "/htmx/drill/create");
        model.addAttribute("buttonText", "Create");

        var drillListResponse = drillApiService.getAll();
        if (drillListResponse.hasError()) {
            model.addAttribute("errorMessage", drillListResponse.getError().toString());
        } else if (0 < drillListResponse.getResponse().length) {
            model.addAttribute("drillsList", drillListResponse.getResponse());
        }

        var categoriesListResponse = categoryApiService.getAll();
        if (categoriesListResponse.hasError()) {
            model.addAttribute("errorMessage", categoriesListResponse.getError().toString());
        } else if (0 < categoriesListResponse.getResponse().length) {
            model.addAttribute("categoriesList", categoriesListResponse.getResponse());
        }

        var subCategoriesListResponse = subCategoryApiService.getAll();
        if (subCategoriesListResponse.hasError()) {
            model.addAttribute("errorMessage", subCategoriesListResponse.getError().toString());
        } else if (0 < subCategoriesListResponse.getResponse().length) {
            model.addAttribute("subCategoriesList", subCategoriesListResponse.getResponse());
        }

        return "layouts/htmx/drill_form :: drillForm";
    }

    @PostMapping("/create")
    public String createDrill(Model model,
            @ModelAttribute DrillCreateHtmxDTO drill) {
        // Create the drill first (just creates a drill with a name)
        var drillCreateResponse = drillApiService.create(drill.toDrillCreateDto());
        if (drillCreateResponse.hasError()) {
            model.addAttribute("errorMessage", drillCreateResponse.getError().toString());
        } else {
            DrillResponseDTO createdDrill = drillCreateResponse.getResponse();

            //Now we update the created drill with the rest of the information
            DrillUpdateDTO updateDrill = new DrillUpdateDTO(createdDrill);
            updateDrill.fillInfo(drill);
            var drillUpdateResponse = drillApiService.update(
                createdDrill.getId(), updateDrill);
            if (drillUpdateResponse.hasError()) {
                model.addAttribute("errorMessage", drillCreateResponse.getError().toString());
            } else {
                DrillResponseDTO newDrill = drillUpdateResponse.getResponse();
                model.addAttribute("name", newDrill.getName());
                model.addAttribute("id", newDrill.getId());
                model.addAttribute("instructionsList", newDrill.getInstructions());
                model.addAttribute("categoriesList", newDrill.getCategories());
                model.addAttribute("subCategoriesList", newDrill.getSubCategories());
                model.addAttribute("relatedDrillsList", newDrill.getRelatedDrillIds());
            }
        }

        model.addAttribute("backEndpoint", "/htmx/drill/create");

        return "layouts/htmx/drill_view_one :: drillDetails";
    }

    @GetMapping("/{drillId}/instructions/create")
    @ResponseBody
    public String createNewInstructions(Model model, @PathVariable Long drillId,
            @RequestParam String startingEndpoint) {
        // TODO: properly implement
        // TODO: create accompanying html
        // TODO: create the post endpoint
        return "Creating new drill for Drill ID: " + drillId + "<br>Using back button: " + startingEndpoint;
    }

    @GetMapping("/{drillId}/instructions/modify")
    @ResponseBody
    public String modifyInstructions(Model model, @PathVariable Long drillId,
            @RequestParam String startingEndpoint) {
        // TODO: Properly implement
        // TODO: create accompnying html
        // TODO: create the post endpoint
        return "Creating new drill for Drill ID: " + drillId + "<br>Using back button: " + startingEndpoint;
    }

    @GetMapping("/{drillId}/instructions/delete")
    @ResponseBody
    public String deleteInstructionsConfirmation(Model model, @PathVariable Long drillId,
            @RequestParam String startingEndpoint) {
        // TODO: properly implement
        // TODO: creat the accompanying html
        // TODO: create the post endpoint
        return "Creating new drill for Drill ID: " + drillId + "<br>Using back button: " + startingEndpoint;
    }
}
