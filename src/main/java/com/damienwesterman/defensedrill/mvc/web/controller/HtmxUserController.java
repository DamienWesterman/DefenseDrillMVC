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
 * Copyright 2025 Damien Westerman
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
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.damienwesterman.defensedrill.mvc.service.UserApiService;
import com.damienwesterman.defensedrill.mvc.util.Constants;
import com.damienwesterman.defensedrill.mvc.web.dto.UserFormDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.UserInfoDTO;

import lombok.RequiredArgsConstructor;

/**
 * Controller for all HTMX element access
 */
@Controller
@RequestMapping("/htmx/user")
@RequiredArgsConstructor
public class HtmxUserController {
    private final UserApiService apiService;

    /**
     * Used for essentially removing an HTML element view HTMX.
     */
    @GetMapping("/empty_htmx")
    @ResponseBody
    public String emptyHtmx() {
        return "";
    }

    @GetMapping("/view")
    public String viewAllUsers(Model model) {
        var response = apiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            model.addAttribute("windowTitle", "Error");
            model.addAttribute("buttonText", "");
            model.addAttribute("listItems", Map.of());
        } else {
            List<UserInfoDTO> users = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Total Users: " + users.size());
            model.addAttribute("buttonText", "Details");

            // Add users to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, userId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/user/view/" + userId
                );
            List<Map<String, String>> listItems = new ArrayList<>(users.size());
            for (var user : users) {
                listItems.add(
                    createListItem.apply(user.getUsername(), user.getId())
                );
            }
            model.addAttribute("listItems", listItems);
        }

        return "layouts/htmx/view_window_list :: viewWindowList";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("windowTitle", "Create New User");
        model.addAttribute("postEndpoint", "/htmx/user/create");
        model.addAttribute("buttonText", "Create");

        List<Pair<String, Boolean>> rolesList = Constants.ALL_ROLES_LIST.stream()
            .map(role -> new Pair<String, Boolean>(role, Boolean.FALSE))
            .collect(Collectors.toList());
        model.addAttribute("rolesList", rolesList);

        return "layouts/htmx/user_form :: userForm";
    }

    @PostMapping("/create")
    @ResponseBody
    public String createUser(Model model, @ModelAttribute UserFormDTO user) {
        return user.toString();
    }
}
