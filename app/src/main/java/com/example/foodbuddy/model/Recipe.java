package com.example.foodbuddy.model;

public class Recipe {

    private String name;
    private String duration;
    private String description;
    private Boolean favourite;

    public Recipe() {
    }

    public Recipe(String name, String duration, String description, Boolean favourite) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.favourite = favourite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}
