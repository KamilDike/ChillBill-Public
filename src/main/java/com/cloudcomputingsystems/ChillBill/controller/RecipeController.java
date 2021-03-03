package com.cloudcomputingsystems.ChillBill.controller;

import com.cloudcomputingsystems.ChillBill.model.recipe.Recipe;
import com.cloudcomputingsystems.ChillBill.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @GetMapping("/get")
    public List<Recipe> getRecipes(@RequestParam String keyword) {
        return recipeService.getRecipes(keyword);
    }

}
