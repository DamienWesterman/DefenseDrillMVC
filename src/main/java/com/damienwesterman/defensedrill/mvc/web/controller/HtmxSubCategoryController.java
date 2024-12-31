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

import com.damienwesterman.defensedrill.mvc.service.DrillApiService;
import com.damienwesterman.defensedrill.mvc.service.SubCategoryApiService;
import com.damienwesterman.defensedrill.mvc.web.dto.AbstractCategoryCreateDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.AbstractCategoryDTO;

import lombok.RequiredArgsConstructor;

/**
 * Controller for all HTMX element access
 */
@Controller
@RequestMapping("/htmx/sub_category")
@RequiredArgsConstructor
public class HtmxSubCategoryController {
    private final SubCategoryApiService subCategoryApiService;
    private final DrillApiService drillApiService;

    @GetMapping("/view")
    public String viewAllSubCategories(Model model) {
        var response = subCategoryApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<AbstractCategoryDTO> subCategories = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Total Sub-Categories: " + subCategories.size());
            model.addAttribute("buttonText", "Details");

            // Add sub-categories to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, subCategoryId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/sub_category/view/" + subCategoryId
                );
            List<Map<String, String>> listItems = new ArrayList<>(subCategories.size());
            for (var subCategory : subCategories) {
                listItems.add(
                    createListItem.apply(subCategory.getName(), subCategory.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/view/{id}")
    public String viewOneSubCategory(Model model, @PathVariable Long id) {
        var response = subCategoryApiService.get(id);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            var subCategory = response.getResponse();
            model.addAttribute("name", subCategory.getName());
            // ID already included
            model.addAttribute("description", subCategory.getDescription());
        }

        model.addAttribute("backEndpoint", "/htmx/sub_category/view");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/create")
    public String createSubCategoryForm(Model model) {
        model.addAttribute("title", "Create New Sub-Category");
        model.addAttribute("postEndpoint", "/htmx/sub_category/create");
        model.addAttribute("buttonText", "Create");

        var response = drillApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            model.addAttribute("displayDrillsList", true);
            model.addAttribute("drillsList", response.getResponse());
        }

        return "layouts/htmx/abstract_category_form :: abstractCategoryForm";
    }

    @PostMapping("/create")
    public String createSubCategory(Model model,
            @ModelAttribute AbstractCategoryCreateDTO subCategory) {
        var createResponse = subCategoryApiService.create(subCategory.toAbstractCategoryDTO());
        if (createResponse.hasError() || null == createResponse.getResponse()) {
            model.addAttribute("errorMessage", createResponse.getError().toString());
        } else {
            model.addAttribute("successMessage", "Sub-Category Created Successfully!");

            var newSubCategory = createResponse.getResponse();
            model.addAttribute("name", newSubCategory.getName());
            model.addAttribute("id", newSubCategory.getId());
            model.addAttribute("description", newSubCategory.getDescription());

            if (null != subCategory.getDrillIds() && !subCategory.getDrillIds().isEmpty()) {
                var updateDrillsResponse = drillApiService.updateSubCategories(newSubCategory.getId(), subCategory.getDrillIds());
                if (updateDrillsResponse.hasError()) {
                    model.addAttribute("errorMessage", updateDrillsResponse.getError().toString());
                }
            }
        }

        model.addAttribute("backEndpoint", "/htmx/sub_category/create");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/modify")
    public String modifySubCategoryList(Model model) {
        var response = subCategoryApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<AbstractCategoryDTO> subCategories = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Choose Sub-Category to Modify");
            model.addAttribute("buttonText", "Modify");

            // Add sub-categories to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, subCategoryId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/sub_category/modify/" + subCategoryId
                );
            List<Map<String, String>> listItems = new ArrayList<>(subCategories.size());
            for (var subCategory : subCategories) {
                listItems.add(
                    createListItem.apply(subCategory.getName(), subCategory.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/modify/{id}")
    public String modifyOneSubCategoryForm(Model model, @PathVariable Long id) {
        var response = subCategoryApiService.get(id);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            AbstractCategoryDTO subCategory = response.getResponse();

            model.addAttribute("title", "Modify Sub-Category: " + id);
            model.addAttribute("postEndpoint", "/htmx/sub_category/modify/" + id);
            model.addAttribute("buttonText", "Update");
            model.addAttribute("nameText", subCategory.getName());
            model.addAttribute("descriptionText", subCategory.getDescription());
        }

        return "layouts/htmx/abstract_category_form :: abstractCategoryForm";
    }

    @PostMapping("/modify/{id}")
    public String modifyOneSubCategory(Model model,
            @PathVariable Long id, @ModelAttribute AbstractCategoryDTO subCategory) {
        subCategory.setId(id);
        var response = subCategoryApiService.update(subCategory);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            model.addAttribute("successMessage", "Sub-Category Modified Successfully!");

            var newSubCategory = response.getResponse();
            model.addAttribute("name", newSubCategory.getName());
            model.addAttribute("id", newSubCategory.getId());
            model.addAttribute("description", newSubCategory.getDescription());
        }

        model.addAttribute("backEndpoint", "/htmx/sub_category/modify");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/delete")
    public String deleteSubCategoryList(Model model) {
        var response = subCategoryApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<AbstractCategoryDTO> subCategories = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Choose Sub-Category to Delete");
            model.addAttribute("buttonText", "Delete");

            // Add sub-categories to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, subCategoryId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/sub_category/confirm_delete/" + subCategoryId
                );
            List<Map<String, String>> listItems = new ArrayList<>(subCategories.size());
            for (var subCategory : subCategories) {
                listItems.add(
                    createListItem.apply(subCategory.getName(), subCategory.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/confirm_delete/{id}")
    public String confirmDeleteSubCategory(Model model, @PathVariable Long id) {
        var response = subCategoryApiService.get(id);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return deleteSubCategoryList(model);
        }

        AbstractCategoryDTO subCategory = response.getResponse();

        model.addAttribute("windowTitle", "Confirm Sub-Category Deletion:");
        // ID already set
        model.addAttribute("name", subCategory.getName());
        model.addAttribute("cancelEndpoint", "/htmx/sub_category/delete");
        model.addAttribute("confirmEndpoint", "/htmx/sub_category/delete/" + id);

        return "layouts/htmx/confirm_delete :: confirmDelete";
    }

    @PostMapping("/delete/{id}")
    public String deleteOneSubCategory(Model model, @PathVariable Long id) {
        subCategoryApiService.delete(id);
        model.addAttribute("successMessage", "Sub-Category Successfully Deleted!");
        return deleteSubCategoryList(model);
    }
}
