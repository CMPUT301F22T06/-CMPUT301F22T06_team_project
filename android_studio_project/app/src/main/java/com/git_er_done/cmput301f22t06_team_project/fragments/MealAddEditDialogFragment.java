package com.git_er_done.cmput301f22t06_team_project.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.git_er_done.cmput301f22t06_team_project.R;
import com.git_er_done.cmput301f22t06_team_project.adapters.IngredientsRecyclerViewAdapter;
import com.git_er_done.cmput301f22t06_team_project.adapters.MealsRecyclerViewAdapter;
import com.git_er_done.cmput301f22t06_team_project.customViews.IngredientMealItemView;
import com.git_er_done.cmput301f22t06_team_project.customViews.RecipeMealItemView;
import com.git_er_done.cmput301f22t06_team_project.dbHelpers.IngredientDBHelper;
import com.git_er_done.cmput301f22t06_team_project.dbHelpers.MealDBHelper;
import com.git_er_done.cmput301f22t06_team_project.models.ingredient.Ingredient;
import com.git_er_done.cmput301f22t06_team_project.models.meal.Meal;
import com.git_er_done.cmput301f22t06_team_project.models.recipe.Recipe;

import java.time.LocalDate;
import java.util.ArrayList;


public class MealAddEditDialogFragment extends DialogFragment{

    private static Meal selectedMeal = null;
    private Button btnAddIngredientToMeal;
    private Button btnAddRecipeToMeal;

    private static boolean isAddingNewMeal = false;
    private static boolean isEdittingExistingMeal = false;

    View mealAddEditDialogView;

    //Linear layouts for holding the ingredient and recipes the user has selected for the new meal
    View ingredientsLinearLayout;
    View recipesLinearLayout;

    public static ArrayList<Ingredient> selectedIngredientsToAddToMeal = new ArrayList<>();
    public static ArrayList<Recipe> selectedRecipesToAddToMeal = new ArrayList<>();

    /**
     * Required empty public constructor
     */
    public MealAddEditDialogFragment() {}

    /**
     * For adding a new meal
     * @return
     */
    public static MealAddEditDialogFragment newInstance(){
        MealAddEditDialogFragment frag = new MealAddEditDialogFragment();
        isAddingNewMeal = true;
        return frag;
    }

    /**
     * For edditing an existing meal
     * @param selectedMeal
     * @return
     */
    public static MealAddEditDialogFragment newInstance(Meal selectedMeal){
        //Assign local references to arguments passed to this fragment
//        si = selectedIngredient;
        MealAddEditDialogFragment frag = new MealAddEditDialogFragment();
        Bundle args = new Bundle();
//        args.putString("name",  selectedMeal.getName());
//        frag.setArguments(args);
        isEdittingExistingMeal = true;
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mealAddEditDialogView = inflater.inflate(R.layout.fragment_meal_add_edit_dialog, container, false);
        return mealAddEditDialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnCancel;
        Button btnSave;
        selectedIngredientsToAddToMeal.clear(); //clear static list of added ing incase there were some recently added
        selectedRecipesToAddToMeal.clear();

        btnAddIngredientToMeal = view.findViewById(R.id.btn_meal_add_edit_ingredient_add);
        btnAddRecipeToMeal = view.findViewById(R.id.btn_meal_add_edit_recipe_add);
        btnCancel = view.findViewById(R.id.btn_meal_add_edit_cancel);
        btnSave = view.findViewById(R.id.btn_meal_add_edit_save);
        ingredientsLinearLayout = view.findViewById(R.id.ll_meal_add_edit_ingredients);
        recipesLinearLayout = view.findViewById(R.id.ll_meal_add_edit_recipes);

        btnAddIngredientToMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIngredientDialog();
            }
        });

        btnAddRecipeToMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddRecipeDialog();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isAddingNewMeal) {
                    Meal newMeal = new Meal(selectedRecipesToAddToMeal, selectedIngredientsToAddToMeal, MealPlannerFragment.getSelectedDate());
                    MealDBHelper.addMealToDB(newMeal);

                    selectedIngredientsToAddToMeal.clear(); //Clear selected ingredients after creating meal and adding to the db
                    selectedRecipesToAddToMeal.clear();
                    isAddingNewMeal = false;
                    dismiss();

                }
            }
        });

    }

    public void updateLinearLayoutIngredientItems(){
        IngredientMealItemView ingredientMealItemView;

        for(int i = 0; i < selectedIngredientsToAddToMeal.size(); i++){
            ingredientMealItemView = new IngredientMealItemView(getContext());

            ingredientMealItemView.setName(selectedIngredientsToAddToMeal.get(i).getName());
            ingredientMealItemView.setAmount(selectedIngredientsToAddToMeal.get(i).getAmount());
            ingredientMealItemView.setUnit(selectedIngredientsToAddToMeal.get(i).getUnit());
            ((LinearLayout) ingredientsLinearLayout).addView(ingredientMealItemView);
        }
    }

    //TODO - this USE DBHELPER NOT ADAPTERS
    /**
     * This function checks to see if the name of the ingredient inputted already exists in the DB. otherwise show
     * toast and make it so that the user can't save without having a name.
     * @return True if there already exists a name in the database that exists already. False if it doesn't exist.
     */
    boolean checkDuplicateInDB(){
//        for (Ingredient i : rvAdapter.getIngredientsList()){ // Checks to see if there exists an ingredient of the same name already
//            if (i.getName().equals(etName.getText().toString())) {
//                Toast.makeText(getActivity(), "An ingredient of the same name exists already.", Toast.LENGTH_LONG).show();
//                return true;
//            }
//        }
        return false;
    }

    private void showAddIngredientDialog() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        MealAddIngredientDialogFragment addIngredientDialogFragment =
                MealAddIngredientDialogFragment.newInstance();
        addIngredientDialogFragment.show(fm, "fragment_meal_add_ingredient_dialog");
    }

    private void showAddRecipeDialog() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        MealAddRecipeDialogFragment addRecipeDialogFragment =
                MealAddRecipeDialogFragment.newInstance();
        addRecipeDialogFragment.show(fm, "fragment_meal_add_recipe_dialog");
    }

}