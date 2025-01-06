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

package com.damienwesterman.defensedrill.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DefenseDrillMvcApplication {
	// TODO: Add loading animations on every form for proper user feedback
	// TODO: Create some kind of annotation or something that makes sure that a use does not stumble upon the htmx endpoints?
	// TODO: In the thymeleaf stuff, list the necessary model attributes for something (i.e. needs listImtes of type map with keys description and htmx endpoint, etc.)
	// TODO: Generate the links dynamically somehow? Different in dev from prod
	// TODO: Go through the css and html see if we can take anything out of the css, maybe make some variables in the css, and consolidate anything if necessary or make more classes
	// TODO: Favico icon thing?
	// TODO: Painstakingly go through each and every warning

	public static void main(String[] args) {
		SpringApplication.run(DefenseDrillMvcApplication.class, args);
	}

}
