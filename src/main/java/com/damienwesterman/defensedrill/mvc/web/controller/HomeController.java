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
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.damienwesterman.defensedrill.mvc.util.Constants;

/**
 * Controller for all basic UI/template endpoints.
 */
@Controller
public class HomeController {
    private static final String PROD_PROFILE_STRING = "prod";
    private static final String DEV_API_DOCUMENTATION_ENDPOINT =
        "http://" + Constants.SERVER_IP_ADDRESS + ":5433/swagger-ui/index.html#/";
    private static final String PROD_API_DOCUMENTATION_ENDPOINT = "/docs/index.html";

    private final String API_DOCUMENTATION_ENDPOINT;

    public HomeController(Environment environment) {
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        if (activeProfiles.contains(PROD_PROFILE_STRING)) {
            API_DOCUMENTATION_ENDPOINT = PROD_API_DOCUMENTATION_ENDPOINT;
        } else {
            API_DOCUMENTATION_ENDPOINT = DEV_API_DOCUMENTATION_ENDPOINT;
        }
    }

    /**
     * Used to create a list item for the vertical tabs on the left of the modify SPA page.
     */
    private final static BiFunction<String, String, Map<String, String>> createListItem =
        (name, htmxEndpoint) -> Map.of(
            "name", name,
            "htmxEndpoint", htmxEndpoint
        );

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("inetAddress", Constants.SERVER_IP_ADDRESS);
        model.addAttribute("apiDocumentationUri", API_DOCUMENTATION_ENDPOINT);

        return "home";
    }

    @GetMapping("/modify")
    public String modifyPage() {
        return "modify";
    }

    @GetMapping("/modify/category")
    public String modifyCategories(Model model) {
        model.addAttribute("tabTitle", "Modify Category");
        model.addAttribute("pageTitle", "Modify Category");

        List<Map<String, String>> listItems = List.of(
            createListItem.apply("View Categories", "/htmx/category/view"),
            createListItem.apply("Create Category", "/htmx/category/create"),
            createListItem.apply("Modify Category", "/htmx/category/modify"),
            createListItem.apply("Delete Category", "/htmx/category/delete")
        );
        model.addAttribute("listItems", listItems);

        return "tab_with_window";
    }

    @GetMapping("/modify/sub_category")
    public String modifySubCategories(Model model) {
        model.addAttribute("tabTitle", "Modify Sub-Category");
        model.addAttribute("pageTitle", "Modify Sub-Category");

        List<Map<String, String>> listItems = List.of(
            createListItem.apply("View Sub-Categories", "/htmx/sub_category/view"),
            createListItem.apply("Create Sub-Category", "/htmx/sub_category/create"),
            createListItem.apply("Modify Sub-Category", "/htmx/sub_category/modify"),
            createListItem.apply("Delete Sub-Category", "/htmx/sub_category/delete")
        );
        model.addAttribute("listItems", listItems);

        return "tab_with_window";
    }

    @GetMapping("/modify/drill")
    public String modifyDrills(Model model) {
        model.addAttribute("tabTitle", "Modify Drill");
        model.addAttribute("pageTitle", "Modify Drill");

        List<Map<String, String>> listItems = List.of(
            createListItem.apply("View Drills", "/htmx/drill/view"),
            createListItem.apply("Create Drills", "/htmx/drill/create"),
            createListItem.apply("Modify Drills", "/htmx/drill/modify"),
            createListItem.apply("Delete Drills", "/htmx/drill/delete")
        );
        model.addAttribute("listItems", listItems);

        return "tab_with_window";
    }

    @GetMapping("/modify/user")
    public String modifyUsers(Model model) {
        model.addAttribute("tabTitle", "Modify User");
        model.addAttribute("pageTitle", "Modify User");

        List<Map<String, String>> listItems = List.of(
            createListItem.apply("View Users", "/htmx/user/view"),
            createListItem.apply("Create Users", "/htmx/user/create"),
            createListItem.apply("Modify Users", "/htmx/user/modify"),
            createListItem.apply("Delete Users", "/htmx/user/delete")
        );
        model.addAttribute("listItems", listItems);

        return "tab_with_window";
    }
}
