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

import com.damienwesterman.defensedrill.mvc.service.CategoryApiService;
import com.damienwesterman.defensedrill.mvc.service.DrillApiService;
import com.damienwesterman.defensedrill.mvc.web.dto.AbstractCategoryCreateDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.AbstractCategoryDTO;

import lombok.RequiredArgsConstructor;

/**
 * Controller for all HTMX element access
 */
@Controller
@RequestMapping("/htmx/category")
@RequiredArgsConstructor
public class HtmxCategoryController {
    private final CategoryApiService categoryApiService;
    private final DrillApiService drillApiService;

    @GetMapping("/view")
    public String viewAllCategories(Model model) {
        var response = categoryApiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            model.addAttribute("windowTitle", "Error");
            model.addAttribute("buttonText", "");
            model.addAttribute("listItems", Map.of());
        } else {
            List<AbstractCategoryDTO> categories = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Total Categories: " + categories.size());
            model.addAttribute("buttonText", "Details");

            // Add categories to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, categoryId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/category/view/" + categoryId
                );
            List<Map<String, String>> listItems = new ArrayList<>(categories.size());
            for (var category : categories) {
                listItems.add(
                    createListItem.apply(category.getName(), category.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/view/{id}")
    public String viewOneCategory(Model model, @PathVariable Long id) {
        var response = categoryApiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return viewAllCategories(model);
        }

        var category = response.getResponse();
        model.addAttribute("name", category.getName());
        model.addAttribute("id", id);
        model.addAttribute("description", category.getDescription());
        model.addAttribute("backEndpoint", "/htmx/category/view");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("windowTitle", "Create New Category");
        model.addAttribute("postEndpoint", "/htmx/category/create");
        model.addAttribute("buttonText", "Create");

        var response = drillApiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else if (0 < response.getResponse().length) {
            model.addAttribute("displayDrillsList", true);
            model.addAttribute("drillsList", response.getResponse());
        }

        return "layouts/htmx/abstract_category_form :: abstractCategoryForm";
    }

    @PostMapping("/create")
    public String createCategory(Model model,
            @ModelAttribute AbstractCategoryCreateDTO category) {
        var createResponse = categoryApiService.create(category.toAbstractCategoryDTO());
        if (createResponse.hasError()) {
            model.addAttribute("errorMessage", createResponse.getError().toString());
            return createCategoryForm(model);
        }

        model.addAttribute("successMessage", "Category Created Successfully!");

        var newCategory = createResponse.getResponse();
        model.addAttribute("name", newCategory.getName());
        model.addAttribute("id", newCategory.getId());
        model.addAttribute("description", newCategory.getDescription());
        model.addAttribute("backEndpoint", "/htmx/category/create");

        // Update any checked drills
        if (null != category.getDrillIds() && !category.getDrillIds().isEmpty()) {
            var updateDrillsResponse = drillApiService.updateCategories(newCategory.getId(), category.getDrillIds());
            if (updateDrillsResponse.hasError()) {
                model.addAttribute("errorMessage", updateDrillsResponse.getError().toString());
            }
        }

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/modify")
    public String modifyCategoryList(Model model) {
        var response = categoryApiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            model.addAttribute("windowTitle", "Error");
            model.addAttribute("buttonText", "");
            model.addAttribute("listItems", Map.of());
        } else {
            List<AbstractCategoryDTO> categories = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Choose Category to Modify");
            model.addAttribute("buttonText", "Modify");

            // Add categories to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, categoryId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/category/modify/" + categoryId
                );
            List<Map<String, String>> listItems = new ArrayList<>(categories.size());
            for (var category : categories) {
                listItems.add(
                    createListItem.apply(category.getName(), category.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/modify/{id}")
    public String modifyOneCategoryForm(Model model, @PathVariable Long id) {
        var response = categoryApiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return modifyCategoryList(model);
        }

        AbstractCategoryDTO category = response.getResponse();

        model.addAttribute("windowTitle", "Modify Category: " + id);
        model.addAttribute("postEndpoint", "/htmx/category/modify/" + id);
        model.addAttribute("buttonText", "Update");
        model.addAttribute("nameText", category.getName());
        model.addAttribute("descriptionText", category.getDescription());

        return "layouts/htmx/abstract_category_form :: abstractCategoryForm";
    }

    @PostMapping("/modify/{id}")
    public String modifyOneCategory(Model model,
            @PathVariable Long id, @ModelAttribute AbstractCategoryDTO category) {
        category.setId(id);
        var response = categoryApiService.update(category);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return modifyCategoryList(model);
        }

        model.addAttribute("successMessage", "Category Modified Successfully!");

        var newCategory = response.getResponse();
        model.addAttribute("name", newCategory.getName());
        model.addAttribute("id", newCategory.getId());
        model.addAttribute("description", newCategory.getDescription());
        model.addAttribute("backEndpoint", "/htmx/category/modify");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/delete")
    public String deleteCategoryList(Model model) {
        var response = categoryApiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            model.addAttribute("windowTitle", "Error");
            model.addAttribute("buttonText", "");
            model.addAttribute("listItems", Map.of());
        } else {
            List<AbstractCategoryDTO> categories = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Choose Category to Delete");
            model.addAttribute("buttonText", "Delete");

            // Add categories to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, categoryId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/category/confirm_delete/" + categoryId
                );
            List<Map<String, String>> listItems = new ArrayList<>(categories.size());
            for (var category : categories) {
                listItems.add(
                    createListItem.apply(category.getName(), category.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/confirm_delete/{id}")
    public String confirmDeleteCategory(Model model, @PathVariable Long id) {
        var response = categoryApiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return deleteCategoryList(model);
        }

        AbstractCategoryDTO category = response.getResponse();

        model.addAttribute("windowTitle", "Confirm Category Deletion:");
        model.addAttribute("id", id);
        model.addAttribute("name", category.getName());
        model.addAttribute("cancelEndpoint", "/htmx/category/delete");
        model.addAttribute("confirmEndpoint", "/htmx/category/delete/" + id);

        return "layouts/htmx/confirm_delete :: confirmDelete";
    }

    @PostMapping("/delete/{id}")
    public String deleteOneCategory(Model model, @PathVariable Long id) {
        categoryApiService.delete(id);
        model.addAttribute("successMessage", "Category Successfully Deleted!");
        return deleteCategoryList(model);
    }
}
