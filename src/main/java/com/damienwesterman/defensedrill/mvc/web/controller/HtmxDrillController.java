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
import com.damienwesterman.defensedrill.mvc.web.dto.InstructionsDTO;

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

    /**
     * Used for essentially removing an element.
     */
    @GetMapping("/empty_htmx")
    @ResponseBody
    public String emptyHtmx() {
        return "";
    }

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
    public String viewOneDrill(Model model, @PathVariable Long id,
            @RequestParam(defaultValue = "/htmx/drill/view") String backEndpoint) {
        var response = drillApiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            var drill = response.getResponse();
            model.addAttribute("name", drill.getName());
            model.addAttribute("id", id);
            model.addAttribute("instructionsList", drill.getInstructions());
            model.addAttribute("categoriesList", drill.getCategories());
            model.addAttribute("subCategoriesList", drill.getSubCategories());
            model.addAttribute("relatedDrillsList", drill.getRelatedDrillIds());
        }

        model.addAttribute("backEndpoint", backEndpoint);

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
                model.addAttribute("successMessage", "Drill Created Successfully!");

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
    public String createNewInstructionsForm(Model model, @PathVariable Long drillId,
            @RequestParam String startingEndpoint) {
        var drillResponse = drillApiService.get(drillId);
        if (drillResponse.hasError()) {
            model.addAttribute("windowTitle", "Create Instructions");
        } else {
            model.addAttribute("windowTitle", "Create Instructions for Drill: "
                + drillResponse.getResponse().getName());
        }
        model.addAttribute("postEndpoint",
            "/htmx/drill/" + drillId + "/instructions/create?startingEndpoint=" + startingEndpoint);
        model.addAttribute("buttonText", "Create Instructions");
        model.addAttribute("backEndpoint", startingEndpoint);

        return "layouts/htmx/instructions_form :: instructionsForm";
    }

    @PostMapping("/{drillId}/instructions/create")
    public String createNewInstructions(Model model, @PathVariable Long drillId,
            @ModelAttribute InstructionsDTO instructions, @RequestParam String startingEndpoint) {
        var drillGetResponse = drillApiService.get(drillId);
        if (drillGetResponse.hasError()) {
            model.addAttribute("errorMessage", drillGetResponse.getError().toString());
        } else {
            DrillUpdateDTO drill = new DrillUpdateDTO(drillGetResponse.getResponse());

            if (null == drill.getInstructions()) {
                drill.setInstructions(new ArrayList<>());
            }
            drill.getInstructions().add(instructions);

            var drillUpdateResponse = drillApiService.update(drillId, drill);
            if (drillUpdateResponse.hasError()) {
                model.addAttribute("errorMessage", drillUpdateResponse.getError().toString());
            } else {
                model.addAttribute("successMessage", "Instructions Created Successfully!");
            }
        }

        return viewOneDrill(model, drillId, startingEndpoint);
    }

    @GetMapping("/instructions/add_step")
    public String instructionsAddStep() {
        return "layouts/htmx/instructions_add_step :: instructionsAddStep";
    }

    @GetMapping("/{drillId}/instructions/modify/{instructionsDescription}")
    public String modifyInstructionsForm(Model model, @PathVariable Long drillId,
            // We want to use the instructionsDescription so that there are no timing issues with changing index positions
            @PathVariable String instructionsDescription, @RequestParam String startingEndpoint) {
        // TODO: Properly implement
        // TODO: create accompnying html
        // TODO: create the post endpoint
        var drillResponse = drillApiService.get(drillId);
        if (drillResponse.hasError()) {
            model.addAttribute("errorMessage", drillResponse.getError().toString());
            return viewOneDrill(model, drillId, startingEndpoint);
        }
        DrillResponseDTO drill = drillResponse.getResponse();

        if (null == drill.getInstructions() || drill.getInstructions().isEmpty()) {
            // Nothing to modify, might be a timing issue
            model.addAttribute("errorMessage", instructionsDescription + " does not exist, please try again.");
            return viewOneDrill(model, drillId, startingEndpoint);
        }

        InstructionsDTO instructions = null;
        for (int i = 0; i < drill.getInstructions().size(); i++) {
            if (drill.getInstructions().get(i).getDescription().equals(instructionsDescription)) {
                instructions = drill.getInstructions().get(i);
                break;
            }
        }

        if (null == instructions) {
            // Instructions don't exist
            model.addAttribute("errorMessage", instructionsDescription + " does not exist, please try again.");
            return viewOneDrill(model, drillId, startingEndpoint);
        }


        model.addAttribute("windowTitle", "Modify Instructions for Drill: "
            + drill.getName());
        model.addAttribute("descriptionText", instructions.getDescription());
        model.addAttribute("videoIdText", instructions.getVideoId());
        model.addAttribute("firstSteps", instructions.getSteps().get(0));
        model.addAttribute("stepsListAfterFirst", 
            instructions.getSteps().subList(1, instructions.getSteps().size()));
        model.addAttribute("postEndpoint",
            "/htmx/drill/" + drillId + "/instructions/modify/" + instructionsDescription
                + "?startingEndpoint=" + startingEndpoint);
        model.addAttribute("buttonText", "Update Instructions");
        model.addAttribute("backEndpoint", startingEndpoint);

        return "layouts/htmx/instructions_form :: instructionsForm";
    }


    @PostMapping("/{drillId}/instructions/modify/{originalInstructionsDescription}")
    public String modifyInstructions(Model model, @PathVariable Long drillId, @ModelAttribute InstructionsDTO instructions,
            // We want to use the instructionsDescription so that there are no timing issues with changing index positions
            @PathVariable String originalInstructionsDescription, @RequestParam String startingEndpoint) {
        var drillGetResponse = drillApiService.get(drillId);
        if (drillGetResponse.hasError()) {
            model.addAttribute("errorMessage", drillGetResponse.getError().toString());
        } else {
            DrillUpdateDTO drill = new DrillUpdateDTO(drillGetResponse.getResponse());

            if (null == drill.getInstructions() || drill.getInstructions().isEmpty()) {
                // Nothing to modify, might be a timing issue
                model.addAttribute("errorMessage", originalInstructionsDescription + " does not exist, please try again.");
                return viewOneDrill(model, drillId, startingEndpoint);
            }

            InstructionsDTO instructionsToUpdate = null;
            for (int i = 0; i < drill.getInstructions().size(); i++) {
                if (drill.getInstructions().get(i).getDescription().equals(originalInstructionsDescription)) {
                    instructionsToUpdate = drill.getInstructions().get(i);
                    break;
                }
            }

            if (null == instructionsToUpdate) {
                // Instructions don't exist
                model.addAttribute("errorMessage", originalInstructionsDescription + " does not exist, please try again.");
                return viewOneDrill(model, drillId, startingEndpoint);
            }

            instructionsToUpdate.setDescription(instructions.getDescription());
            instructionsToUpdate.setVideoId(instructions.getVideoId());
            instructionsToUpdate.setSteps(instructions.getSteps());

            var drillUpdateResponse = drillApiService.update(drillId, drill);
            if (drillUpdateResponse.hasError()) {
                model.addAttribute("errorMessage", drillUpdateResponse.getError().toString());
            } else {
                model.addAttribute("successMessage", "Instructions Updated Successfully!");
            }
        }

        return viewOneDrill(model, drillId, startingEndpoint);
    }

    @GetMapping("/{drillId}/instructions/confirm_delete/{instructionsDescription}")
    public String deleteInstructionsConfirmation(Model model, @PathVariable Long drillId,
            // We want to use the instructionsDescription so that there are no timing issues with changing index positions
            @PathVariable String instructionsDescription, @RequestParam String startingEndpoint) {
        var response = drillApiService.get(drillId);
        if (response.hasError()) {
            model.addAttribute("name", instructionsDescription);
        } else {
            // Would be nice to remind the user what drill the instructions belond to if possible
            DrillResponseDTO drill = response.getResponse();
            model.addAttribute("name", drill.getName() + ": " + instructionsDescription);
        }

        model.addAttribute("windowTitle", "Confirm Instruction Deletion:");
        model.addAttribute("id", "Drill " + drillId);
        model.addAttribute("cancelEndpoint", "/htmx/drill/view/" + drillId + "?backEndpoint=" + startingEndpoint);
        model.addAttribute("confirmEndpoint",
            "/htmx/drill/" + drillId + "/instructions/delete/" + instructionsDescription + "?startingEndpoint=" + startingEndpoint);

        return "layouts/htmx/confirm_delete :: confirmDelete";
    }

    @PostMapping("/{drillId}/instructions/delete/{instructionsDescription}")
    public String deleteInstructions(Model model, @PathVariable Long drillId,
            // We want to use the instructionsDescription so that there are no timing issues with changing index positions
            @PathVariable String instructionsDescription, @RequestParam String startingEndpoint) {
        var drillGetResponse = drillApiService.get(drillId);
        if (drillGetResponse.hasError()) {
            model.addAttribute("errorMessage", drillGetResponse.getError().toString());
        } else {
            DrillUpdateDTO drill = new DrillUpdateDTO(drillGetResponse.getResponse());

            if (null == drill.getInstructions() || drill.getInstructions().isEmpty()) {
                // Nothing to delete
                return viewOneDrill(model, drillId, startingEndpoint);
            }

            for (int i = 0; i < drill.getInstructions().size(); i++) {
                if (drill.getInstructions().get(i).getDescription().equals(instructionsDescription)) {
                    drill.getInstructions().remove(i);
                    break;
                }
            }

            var drillUpdateResponse = drillApiService.update(drillId, drill);
            if (drillUpdateResponse.hasError()) {
                model.addAttribute("errorMessage", drillUpdateResponse.getError().toString());
            } else {
                model.addAttribute("successMessage", "Instructions Deleted Successfully!");
            }
        }

        return viewOneDrill(model, drillId, startingEndpoint);
    }

    @GetMapping("/delete")
    public String deleteDrillList(Model model) {
        var response = drillApiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<DrillResponseDTO> drills = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Choose Drill to Delete");
            model.addAttribute("buttonText", "Delete");

            // Add categories to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, categoryId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/drill/confirm_delete/" + categoryId
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

    @GetMapping("/confirm_delete/{id}")
    public String confirmDeleteDrill(Model model, @PathVariable Long id) {
        var response = drillApiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return deleteDrillList(model);
        }

        DrillResponseDTO drill = response.getResponse();

        model.addAttribute("windowTitle", "Confirm Drill Deletion:");
        model.addAttribute("id", id);
        model.addAttribute("name", drill.getName());
        model.addAttribute("cancelEndpoint", "/htmx/drill/delete");
        model.addAttribute("confirmEndpoint", "/htmx/drill/delete/" + id);

        return "layouts/htmx/confirm_delete :: confirmDelete";
    }

    @PostMapping("/delete/{id}")
    public String deleteOneDrill(Model model, @PathVariable Long id) {
        drillApiService.delete(id);
        model.addAttribute("successMessage", "Drill Successfully Deleted!");
        return deleteDrillList(model);
    }
}
