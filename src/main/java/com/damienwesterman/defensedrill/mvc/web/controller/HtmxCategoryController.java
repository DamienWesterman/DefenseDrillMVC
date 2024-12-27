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
import org.springframework.web.bind.annotation.ResponseBody;

import com.damienwesterman.defensedrill.mvc.service.CategoryApiService;
import com.damienwesterman.defensedrill.mvc.service.DrillApiService;
import com.damienwesterman.defensedrill.mvc.web.dto.CategoryDTO;

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
    public String viewCategories(Model model) {
        var response = categoryApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<CategoryDTO> categories = List.of(response.getResponse());

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
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            var category = response.getResponse();
            model.addAttribute("name", category.getName());
            // ID already included
            model.addAttribute("description", category.getDescription());
        }

        model.addAttribute("backEndpoint", "/htmx/category/view");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/create")
    public String createCategory(Model model) {
        model.addAttribute("title", "Create New Category");
        model.addAttribute("postEndpoint", "/htmx/category/create");
        model.addAttribute("buttonText", "Create");

        return "layouts/htmx/abstract_category_form :: abstractCategoryForm";
    }

    @PostMapping("/create")
    public String createCategory(Model model, @ModelAttribute CategoryDTO category) {
        var response = categoryApiService.create(category);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            model.addAttribute("successMessage", "Category Created Successfully!");

            var newCategory = response.getResponse();
            model.addAttribute("name", newCategory.getName());
            model.addAttribute("id", newCategory.getId());
            model.addAttribute("description", newCategory.getDescription());
        }

        model.addAttribute("backEndpoint", "/htmx/category/create");

        // TODO: ask if the user wants to tie it to any drills

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }

    @GetMapping("/modify")
    public String modifyCategory(Model model) {
        var response = categoryApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<CategoryDTO> categories = List.of(response.getResponse());

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
    public String modifyOneCategory(Model model, @PathVariable Long id) {
        var response = categoryApiService.get(id);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            CategoryDTO category = response.getResponse();

            model.addAttribute("title", "Modify Category: " + id);
            model.addAttribute("postEndpoint", "/htmx/category/modify/" + id);
            model.addAttribute("buttonText", "Update");
            model.addAttribute("nameText", category.getName());
            model.addAttribute("descriptionText", category.getDescription());
        }

        return "layouts/htmx/abstract_category_form :: abstractCategoryForm";
    }

    @PostMapping("/modify/{id}")
    public String modifyOneCategory(Model model,
            @PathVariable Long id, @ModelAttribute CategoryDTO category) {
        // TODO: FIXME START HERE
        category.setId(id);
        var response = categoryApiService.update(category);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            model.addAttribute("successMessage", "Category Created Successfully!");

            var newCategory = response.getResponse();
            model.addAttribute("name", newCategory.getName());
            model.addAttribute("id", newCategory.getId());
            model.addAttribute("description", newCategory.getDescription());
        }

        model.addAttribute("backEndpoint", "/htmx/category/modify");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }
}
