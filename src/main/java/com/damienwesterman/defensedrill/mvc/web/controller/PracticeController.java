package com.damienwesterman.defensedrill.mvc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.damienwesterman.defensedrill.mvc.service.CategoryApiService;
import com.damienwesterman.defensedrill.mvc.service.DrillApiService;
import com.damienwesterman.defensedrill.mvc.service.SubCategoryApiService;
import com.damienwesterman.defensedrill.mvc.web.dto.CategoryDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.SubCategoryDTO;
import com.damienwesterman.defensedrill.mvc.web.dto.DrillResponseDTO;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class PracticeController {
    private final DrillApiService drillApiService;
    private final CategoryApiService categoryApiService;
    private final SubCategoryApiService subCategoryApiService;

    @GetMapping("/")
    @ResponseBody
    public String getMethodName() {
        StringBuilder sb = new StringBuilder();

        sb.append("<h1>Drills</h1><br>");
        DrillResponseDTO[] drills = drillApiService.getAll().getBody();
        for (DrillResponseDTO drill : drills) {
            sb.append(drill.getName());
            sb.append(" | ");
        }

        sb.append("<br><h1>Categories</h1><br>");
        CategoryDTO[] categories = categoryApiService.getAll().getResponse();
        for (CategoryDTO category : categories) {
            sb.append(category.getName());
            sb.append(" | ");
        }
        sb.append("<br>One Category<br>");
        sb.append(categoryApiService.get(45L).getResponse().getName());
        sb.append("<br>Not found category<br>");
        var category = categoryApiService.get(0L);
        sb.append(category.getError().getError());
        sb.append(": ");
        sb.append(category.getError().getMessage());

        sb.append("<br><h1>SubCategories</h1><br>");
        SubCategoryDTO[] subCategories = subCategoryApiService.getAll().getResponse();
        for (SubCategoryDTO subCategory : subCategories) {
            sb.append(subCategory.getName());
            sb.append(" | ");
        }

        return sb.toString();
    }
}
