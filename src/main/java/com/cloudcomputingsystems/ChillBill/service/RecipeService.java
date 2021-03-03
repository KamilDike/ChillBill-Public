package com.cloudcomputingsystems.ChillBill.service;

import com.cloudcomputingsystems.ChillBill.model.recipe.Recipe;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RecipeService {

    @Autowired
    private Firestore firestore;

    public List<Recipe> getRecipes(String keyword) {
        List<Recipe> recipes = readRecipes(keyword);
        if (recipes.isEmpty()) {
            List<String> links = getLinks(keyword);
            links.forEach(link -> {
                Recipe details = getDetails(link);
                recipes.add(details);
                writeRecipe(details, keyword);
            });
        }
        return recipes;
    }

    public List<String> getLinks(String keyword) {
        List<String> links = new ArrayList<>();

        Document doc = UrlReader.readUrl("https://www.mojegotowanie.pl/search?q=" + keyword);
        Elements elements = doc.select(".recipe-index-inner");
        elements.forEach(element -> {
//            System.out.println(element.select("h2 > a").attr("href"));
            links.add(element.select("h2 > a").attr("href"));
        });

        return links;
    }

    public Recipe getDetails(String url) {
        String link = "https://www.mojegotowanie.pl" + url;
        Document doc = UrlReader.readUrl(link);

        String name = doc.select("h1").text();
        String image = doc.select(".ui-page-img-swipe").attr("src");
        String description = doc.select(".lead > p").text();
        ArrayList<String> ingredients = new ArrayList<>();
        Elements elements = doc.select(".recpie-ingredient > .inner > ul > li");
        elements.forEach(element -> {
            ingredients.add(element.select("li").text());
        });

        Recipe recipe = new Recipe(name, link, description, ingredients, image);

        return recipe;
    }

    public void writeRecipe(Recipe recipe, String collection) {
        ApiFuture<WriteResult> apiFuture = this.firestore.collection("Recipes")
                .document(collection).collection("Recipes").document().set(recipe);
        try {
            WriteResult writeResult = apiFuture.get();
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }

    public List<Recipe> readRecipes(String keyword) {
        List<Recipe> recipes = new ArrayList<>();
        List<QueryDocumentSnapshot> documents;
        ApiFuture<QuerySnapshot> future = this.firestore.collection("Recipes").document(keyword).collection("Recipes").get();
        try {
            documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                recipes.add(document.toObject(Recipe.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return recipes;
    }
}
