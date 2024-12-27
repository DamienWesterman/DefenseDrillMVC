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
import com.damienwesterman.defensedrill.mvc.web.dto.SubCategoryDTO;

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
    public String viewSubCategories(Model model) {
        var response = subCategoryApiService.getAll();
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            List<SubCategoryDTO> subCategories = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Total SubCategories: " + subCategories.size());
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
    public String createSubCategory(Model model) {
        model.addAttribute("title", "Create New Sub-Category");
        model.addAttribute("postEndpoint", "/htmx/sub_category/create");
        model.addAttribute("buttonText", "Create");

        return "layouts/htmx/abstract_category_form :: abstractCategoryForm";
    }

    @PostMapping("/create")
    public String createSubCategory(Model model, @ModelAttribute SubCategoryDTO subCategory) {
        var response = subCategoryApiService.create(subCategory);
        if (response.hasError() || null == response.getResponse()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            model.addAttribute("successMessage", "Sub-Category Created Successfully!");

            var newSubCategory = response.getResponse();
            model.addAttribute("name", newSubCategory.getName());
            model.addAttribute("id", newSubCategory.getId());
            model.addAttribute("description", newSubCategory.getDescription());
        }

        model.addAttribute("backEndpoint", "/htmx/sub_category/create");

        return "layouts/htmx/abstract_category_view_one :: abstractCategoryDetails";
    }
}
