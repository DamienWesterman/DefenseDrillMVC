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
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/view/{id}")
    public String viewOneUser(Model model, @PathVariable Long id) {
        var response = apiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return viewAllUsers(model);
        }

        var user = response.getResponse();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("id", id);
        model.addAttribute("roles", user.getRoles());
        model.addAttribute("backEndpoint", "/htmx/user/view");

        return "layouts/htmx/user_view_one :: userDetails";
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
    public String createUser(Model model, @ModelAttribute UserFormDTO user) {
        var response = apiService.create(user);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return createUserForm(model);
        }

        model.addAttribute("successMessage", "User Created Successfully!");

        var newUser = response.getResponse();
        model.addAttribute("username", newUser.getUsername());
        model.addAttribute("id", newUser.getId());
        model.addAttribute("roles", newUser.getRoles());
        model.addAttribute("backEndpoint", "/htmx/user/create");

        return "layouts/htmx/user_view_one :: userDetails";
    }

    @GetMapping("/modify")
    public String modifyUsersList(Model model) {
        var response = apiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            model.addAttribute("windowTitle", "Error");
            model.addAttribute("buttonText", "");
            model.addAttribute("listItems", Map.of());
        } else {
            List<UserInfoDTO> users = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Choose User to Modify");
            model.addAttribute("buttonText", "Modify");

            // Add users to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, userId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/user/modify/" + userId
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
    @GetMapping("/modify/{id}")
    public String modifyOneUserForm(Model model, @PathVariable Long id) {
        var response = apiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return modifyUsersList(model);
        }

        UserInfoDTO user = response.getResponse();

        model.addAttribute("windowTitle", "Modify User: " + id);
        model.addAttribute("postEndpoint", "/htmx/user/modify/" + id);
        model.addAttribute("buttonText", "Update");
        model.addAttribute("usernameText", user.getUsername());

        List<Pair<String, Boolean>> rolesList = Constants.ALL_ROLES_LIST.stream()
            .map(role -> {
                Boolean checked = Boolean.FALSE;
                if (user.getRoles().contains(role)) {
                    checked = Boolean.TRUE;
                }
                return new Pair<String, Boolean>(role, checked);
            })
            .collect(Collectors.toList());
        model.addAttribute("rolesList", rolesList);

        return "layouts/htmx/user_form :: userForm";
    }

    @PostMapping("/modify/{id}")
    public String modifyOneUser(Model model,
            @PathVariable Long id, @ModelAttribute UserFormDTO user) {
        var response = apiService.update(id, user);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return modifyUsersList(model);
        }

        model.addAttribute("successMessage", "User Modified Successfully!");

        var updatedUser = response.getResponse();
        model.addAttribute("username", updatedUser.getUsername());
        model.addAttribute("id", updatedUser.getId());
        model.addAttribute("roles", updatedUser.getRoles());
        model.addAttribute("backEndpoint", "/htmx/user/modify");

        return "layouts/htmx/user_view_one :: userDetails";
    }

    @GetMapping("/delete")
    public String deleteUsersList(Model model) {
        var response = apiService.getAll();
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            model.addAttribute("windowTitle", "Error");
            model.addAttribute("buttonText", "");
            model.addAttribute("listItems", Map.of());
        } else {
            List<UserInfoDTO> users = List.of(response.getResponse());

            model.addAttribute("windowTitle",
                "Choose User to Delete");
            model.addAttribute("buttonText", "Delete");

            // Add users to list
            BiFunction<String, Long, Map<String, String>> createListItem =
                (description, userId) -> Map.of(
                    "itemDescription", description,
                    "htmxEndpoint", "/htmx/user/confirm_delete/" + userId
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

    @GetMapping("/confirm_delete/{id}")
    public String confirmDeleteUser(Model model, @PathVariable Long id) {
        var response = apiService.get(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
            return deleteUsersList(model);
        }

        UserInfoDTO user = response.getResponse();

        model.addAttribute("windowTitle", "Confirm User Deletion:");
        model.addAttribute("id", id);
        model.addAttribute("name", user.getUsername());
        model.addAttribute("cancelEndpoint", "/htmx/user/delete");
        model.addAttribute("confirmEndpoint", "/htmx/user/delete/" + id);

        return "layouts/htmx/confirm_delete :: confirmDelete";
    }

    @PostMapping("/delete/{id}")
    public String deleteOneUser(Model model, @PathVariable Long id) {
        var response = apiService.delete(id);
        if (response.hasError()) {
            model.addAttribute("errorMessage", response.getError().toString());
        } else {
            model.addAttribute("successMessage", "User Successfully Deleted!");
        }
        return deleteUsersList(model);
    }
}
