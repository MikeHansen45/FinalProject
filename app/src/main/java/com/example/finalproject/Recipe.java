package com.example.finalproject;

import android.graphics.Bitmap;

public class Recipe {

    private long id;
    private String recipeTitle;
    private String recipeLink;
    private String ingredients;
    private String thumbnailURL;

    public Recipe() {

    }

    public Recipe(long id, String title, String URL, String ingredients, String thumbnailURL) {
        this.id = id;
        recipeTitle = title;
        recipeLink = URL;
        this.ingredients = ingredients;
        this.thumbnailURL = thumbnailURL;
    }

    public Recipe(String title, String URL, String ingredients, String thumbnailURL) {
        this(0, title, URL, ingredients, thumbnailURL);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnailURL;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getRecipeLink() {
        return recipeLink;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setRecipeLink(String recipeLink) {
        this.recipeLink = recipeLink;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public String toString() {
        return getRecipeTitle();
    }
}
