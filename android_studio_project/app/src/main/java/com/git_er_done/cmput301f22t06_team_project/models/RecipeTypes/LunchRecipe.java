package com.git_er_done.cmput301f22t06_team_project.models.RecipeTypes;

import com.git_er_done.cmput301f22t06_team_project.models.Recipe;

public class LunchRecipe extends Recipe {

    public LunchRecipe(String title, String comments, String category, int prep_time, int servings) {
        super(title, comments, category, prep_time, servings);
    }
}
