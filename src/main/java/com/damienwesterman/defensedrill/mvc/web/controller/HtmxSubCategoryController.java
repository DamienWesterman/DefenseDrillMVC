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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @ResponseBody
    public String viewCategories() {
        var response = subCategoryApiService.getAll();
        if (!response.hasError()) {
            return String.join(",  ", 
                List.of(
                    response.getResponse()).stream()
                        .map(SubCategoryDTO::getName)
                        .collect(Collectors.toList()));
        } else {
            return "ERROR";
        }
    }
}
