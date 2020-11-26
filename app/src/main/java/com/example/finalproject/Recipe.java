package com.example.finalproject;

import android.graphics.Bitmap;

public class Recipe {

    private String recipeTitle;
    private String recipeLink;
    private String ingredients;
    private Bitmap thumbnail;

    public Recipe() {

    }

    public Recipe(String title, String URL, String ingredients) {
       this(title, URL, ingredients, null);
    }

    public Recipe(String title, String URL, String ingredients, Bitmap thumbnail) {
        recipeTitle = title;
        recipeLink = URL;
        this.ingredients = ingredients;
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
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
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
      return getRecipeTitle();
    }
}
