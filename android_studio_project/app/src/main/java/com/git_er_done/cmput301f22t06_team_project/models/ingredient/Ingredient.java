package com.git_er_done.cmput301f22t06_team_project.models.ingredient;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.git_er_done.cmput301f22t06_team_project.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class Ingredient implements Cloneable{
    private String name;
    private String desc;
    private LocalDate bestBefore;
    private String location;
    private String unit;
    private String category;
    private Integer amount;
    private Integer color;

    //Grab singleton arrays for user defined attributes like location and category
    public static ArrayList<String> ingredientLocations = IngredientLocation.getInstance().getAllLocations();
    public static ArrayList<String> ingredientCategories = IngredientCategory.getInstance().getAllIngredientCategories();
    public static ArrayList<String> ingredientUnits = IngredientUnit.getInstance().getAllUnits();


    /**
     * Creates a new Ingredient object
     * @param name The name of the ingredient as a {@link String}.
     * @param desc A description of the ingredient as a {@link String}.
     * @param bestBefore The best before date of the ingredient as a {@link LocalDate}.
     * @param location The storage location of the ingredient as a {@link String}.
     * @param unit The unit of measure (UOM) of the ingredient as a {@link String}.
     * @param category The category of the ingredient as a {@link String}.
     * @param amount The stored amount of the ingredient in its UOM as a {@link Integer}.
     */
    public Ingredient(String name, String desc, LocalDate bestBefore, String location, String unit,
                      String category, Integer amount) {
        this.name = name;
        this.desc = desc;
        this.bestBefore = bestBefore;
        this.location = location;
        this.unit = unit;
        this.category = category;
        this.amount = amount;
        this.color = R.drawable.border;
    }

    public Ingredient(String name, String desc, LocalDate bestBefore, String location, String unit, String category, Integer amount, Integer color) {
        this.name = name;
        this.desc = desc;
        this.bestBefore = bestBefore;
        this.location = location;
        this.unit = unit;
        this.category = category;
        this.amount = amount;
        this.color = color;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(LocalDate best_before) {
        this.bestBefore = best_before;
    }

    public ArrayList<String> getBestBeforeStringArrayList(){
        ArrayList<String> bb4StringArray = new ArrayList<String>();
        String bb4year = Integer.toString(this.bestBefore.getYear());
        String bb4month = Integer.toString(this.bestBefore.getMonthValue());;
        String bb4date = Integer.toString(this.bestBefore.getDayOfMonth());
        bb4StringArray.add(bb4year);
        bb4StringArray.add(bb4month);
        bb4StringArray.add(bb4date);
        return bb4StringArray;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) { this.amount = amount; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Overriden equals method is required for indexOf call
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ingredient)) {
            return false;
        }
        Ingredient other = (Ingredient) o;
        return name.equalsIgnoreCase(other.getName());
    }

    @Override
    public Ingredient clone() {
        try {
            Ingredient clone = (Ingredient) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
