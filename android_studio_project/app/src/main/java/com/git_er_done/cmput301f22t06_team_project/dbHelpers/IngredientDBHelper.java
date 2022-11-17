package com.git_er_done.cmput301f22t06_team_project.dbHelpers;

import static android.service.controls.ControlsProviderService.TAG;

import static com.git_er_done.cmput301f22t06_team_project.models.Ingredient.Ingredient.createIngredientList;
import static com.git_er_done.cmput301f22t06_team_project.models.Ingredient.Ingredient.testIngredients;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.git_er_done.cmput301f22t06_team_project.controllers.IngredientsRecyclerViewAdapter;
import com.git_er_done.cmput301f22t06_team_project.fragments.IngredientAddEditDialogFragment;
import com.git_er_done.cmput301f22t06_team_project.models.Ingredient.Ingredient;
import com.git_er_done.cmput301f22t06_team_project.models.Recipe;
import com.git_er_done.cmput301f22t06_team_project.models.RecipeIngredient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Saheel Sarker
 * @ingredientsFragment (for now)
 * @Version 1 (Because I didn't write the version before writing this)
 * @see MealPlannerDBHelper
 * @see RecipesDBHelper
 */
public class IngredientDBHelper {

    private static IngredientsRecyclerViewAdapter rvAdapter;
    public static int selectedIngPos;

    public IngredientDBHelper(IngredientsRecyclerViewAdapter adapter){
        rvAdapter = adapter;
        eventChangeListener(); //Initialize eventListener for RecyclerView
    }


    /**
     * This method adds an ingredient to our database in the incredient collection
     * @param ingredient of type {@link Ingredient}
     * @returns void
     * @see MealPlannerDBHelper
     * @see RecipesDBHelper
     */
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference ingredientsDB = db.collection("Ingredients");

    public static void addIngredientToDB(Ingredient ingredient){
        String name = ingredient.getName().toLowerCase();
        String desc = ingredient.getDesc().toLowerCase();
        String bestBefore = ingredient.getBestBefore().toString();
        String location = ingredient.getLocation().toLowerCase();
        String units = ingredient.getUnit();
        String category = ingredient.getCategory();
        Integer amount = ingredient.getAmount();
        String amountString = amount.toString();

        HashMap<String, Object> ingredientAttributes = new HashMap<>();

        ingredientAttributes.put("name", name);
        ingredientAttributes.put("description", desc);
        ingredientAttributes.put("best before", bestBefore);
        ingredientAttributes.put("location",location);
        ingredientAttributes.put("unit", units);
        ingredientAttributes.put("category",category);
        ingredientAttributes.put("amount",amountString);

        ingredientsDB
                .document(name)
                .set(ingredientAttributes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
//                        testIngredients.add(ingredient);
//                        rvAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }

    /**
     * Take a string and searches the ingredients database for it and deletes the document
     * with that name if it's found
     * @param ingredient of type {@link String}
     * @returns void
     * @see MealPlannerDBHelper
     * @see RecipesDBHelper
     */

    public static void deleteIngredientFromDB(Ingredient ingredient, int position){
        String nameOfIngredient = ingredient.getName();
        ingredientsDB
                .document(nameOfIngredient)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Deleted has been deleted successfully!");
//                        testIngredients.remove(ingredient);
//                        rvAdapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be deleted!" + e.toString());
                    }
                });
    }

    public static void setSpIngredientsDropDownAdapter(ArrayAdapter<String> recipeAdapter, ArrayList<String> ingredientStorage){
        ingredientsDB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot docs = task.getResult();
                for(QueryDocumentSnapshot doc: docs) {
                    Ingredient ingredient = createIngredient(doc);
                    ingredientStorage.add(ingredient.getName());
                }
                recipeAdapter.notifyDataSetChanged();
                // The adapter will be here
            }
        });
    }

//    TODO - only modify attributes that have changed -  for now it modifies all of them
    public static void modifyIngredientInDB(Ingredient newIngredient, Ingredient oldIngredient, int pos){
        String nameOfIngredient = oldIngredient.getName();
        selectedIngPos = pos;

        DocumentReference dr = ingredientsDB.document(nameOfIngredient);
        if(!Objects.equals(newIngredient.getName(), oldIngredient.getName())){
            dr.update("name", newIngredient.getName());
        }

        if(!Objects.equals(newIngredient.getDesc(), oldIngredient.getDesc())){
            dr.update("description", newIngredient.getDesc());
        }

        if(!Objects.equals(newIngredient.getAmount(), oldIngredient.getAmount())){
            dr.update("amount", newIngredient.getAmount().toString());
        }


    }


    /**
     * Just a random function to search stuff in the db for possible future needs but
     * it's not used right now and I don't know if it works
     * @param ingredient of type {@link String}
     * @param ingredientsFirebaseCallBack of type IngredientsFirebaseCallBack
     * @returns void
     * @see MealPlannerDBHelper
     * @see RecipesDBHelper
     */
    public void searchForIngredient(String ingredient, IngredientsFirebaseCallBack ingredientsFirebaseCallBack) {
        ArrayList<Ingredient> retrieved = new ArrayList<Ingredient>();
        Log.d(TAG, "The name I'm looking for is " + ingredient);
        ingredientsDB.document(ingredient).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    Log.d(TAG, "The document name is " + doc.getId());
                    Ingredient ingredient = createIngredient(doc);
                    ingredientsFirebaseCallBack.onCallback(ingredient);
                }
            }
        });
    }

    /**
     * This method take a document from firestore and takes the data then converts it into an Ingredient object
     * to return
     * @param doc
     * @return ingredient of type {@link Ingredient}
     * @see MealPlannerDBHelper
     * @see RecipesDBHelper
     */
    private static Ingredient createIngredient(DocumentSnapshot doc) {
        String name = doc.getId();
        String desc = (String) doc.getData().get("description");
        LocalDate bestBefore = LocalDate.parse((String) doc.getData().get("best before"));
//        LocalDate bestBefore = LocalDate.now();
        String location = (String) doc.getData().get("location");
        String unit = (String) doc.getData().get("unit");
        String category = (String) doc.getData().get("category");
        Integer amount = Integer.parseInt((String) doc.getData().get("amount"));

        Ingredient newIngredient = new Ingredient(name, desc, bestBefore, location, unit, category, amount);
        return newIngredient;
    }

    public void eventChangeListener(){
        db.collection("Ingredients")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.e("DB ERROR", error.getMessage());
                            return;
                        }

                        for(DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                Ingredient ingredient = createIngredient(dc.getDocument());
                                testIngredients.add(ingredient);
                                rvAdapter.notifyDataSetChanged();
                            }

                            if(dc.getType() == DocumentChange.Type.MODIFIED){
                                Ingredient ingredient = createIngredient(dc.getDocument());
                                testIngredients.set(selectedIngPos ,ingredient );
                                rvAdapter.notifyDataSetChanged();
                            }

                            if(dc.getType() == DocumentChange.Type.REMOVED){
                                Ingredient ingredient = createIngredient(dc.getDocument());
                                int position = testIngredients.indexOf(ingredient);
                                testIngredients.remove(ingredient);
                                rvAdapter.notifyItemRemoved(position);
                            }
                        }
                    }
                });
    }

}
