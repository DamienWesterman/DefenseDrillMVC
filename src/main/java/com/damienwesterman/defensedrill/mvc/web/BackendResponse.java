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

package com.damienwesterman.defensedrill.mvc.web;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.damienwesterman.defensedrill.mvc.web.dto.ErrorMessageDTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Wapper class that contains all the information from a request to the RestAPI backend.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class BackendResponse<T> {
    @NonNull
    private final HttpStatusCode status;

    @Nullable
    private final T response;

    @Nullable
    private final ErrorMessageDTO error;

    public boolean hasError() {
        return null != error;
    }
}
