package com.cloudcomputingsystems.ChillBill.model.recipe;

import java.util.ArrayList;


public class Recipe {
    String name;
    //https url
    String link;
    String description;
    ArrayList<String> ingredients;
    //url
    String image;

    public Recipe() {
    }

    public Recipe(String name, String link, String description, ArrayList<String> ingredients, String image) {
        this.name = name;
        this.link = link;
        this.description = description;
        this.ingredients = ingredients;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", ingredients=" + ingredients +
                ", image='" + image + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
