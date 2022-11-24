package com.git_er_done.cmput301f22t06_team_project.dbHelpers;

import static android.service.controls.ControlsProviderService.TAG;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.git_er_done.cmput301f22t06_team_project.adapters.IngredientsRecyclerViewAdapter;
import com.git_er_done.cmput301f22t06_team_project.models.ingredient.Ingredient;
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
 * @see RecipeDBHelper
 */
public class IngredientDBHelper {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference ingredientsDB = db.collection("Ingredients");
    private static int selectedIngPos;
    private static IngredientDBHelper singleInstance = null;

    /**
     * Private constructor can only be called when an instance of this singleton is created
     */
    private IngredientDBHelper(){
        setupSnapshotListenerForLocalIngredientStorage();
    }

    // Static method to create instance of Singleton class
    public static IngredientDBHelper getInstance()
    {
        if (singleInstance == null)
            singleInstance = new IngredientDBHelper();
        return singleInstance;
    }

    private static ArrayList<Ingredient> ingredientInStorage= new ArrayList<>();

    //NO SETTER  - only the snapshot listener callback will update local storage accordinly.
    //  Ingredients add/edit/ deleted will rely on the static DB helper methods which will
    //  result in the snapshot listeners updating the local storage
    public static ArrayList<Ingredient> getIngredientsFromStorage(){
        return ingredientInStorage;
    }

    //TODO - Put newly added ingredients ontop of recyclerview top show user
    /**
     * This method adds an ingredient to our database in the incredient collection
     * @param ingredient of type {@link Ingredient}
     * @returns void
     * @see MealPlannerDBHelper
     * @see RecipeDBHelper
     */
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
     * @see RecipeDBHelper
     */
    public static void deleteIngredientFromDB(Ingredient ingredient, int position){
        String nameOfIngredient = ingredient.getName();
        selectedIngPos = position;
        ingredientsDB
                .document(nameOfIngredient)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Deleted has been deleted successfully!");
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
                    ingredientStorage.add(ingredient.getName() + ", " + ingredient.getUnit());
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

        if(!Objects.equals(newIngredient.getDesc(), oldIngredient.getDesc())){
            dr.update("description", newIngredient.getDesc());
        }

        if(!Objects.equals(newIngredient.getBestBefore().toString(), oldIngredient.getBestBefore().toString())){
            dr.update("best before", newIngredient.getBestBefore().toString());
        }

        if(!Objects.equals(newIngredient.getLocation(), oldIngredient.getLocation())){
            dr.update("location", newIngredient.getLocation());
        }

        if(!Objects.equals(newIngredient.getCategory(), oldIngredient.getCategory())){
            dr.update("category", newIngredient.getCategory());
        }

        if(!Objects.equals(newIngredient.getAmount(), oldIngredient.getAmount())){
            dr.update("amount", newIngredient.getAmount().toString());
        }

        if(!Objects.equals(newIngredient.getUnit(), oldIngredient.getUnit())){
            dr.update("unit", newIngredient.getUnit());
        }
    }


    //TODO - handle null exceptions in case the DB is broken so this doesnt crash the app
    /**
     * This method take a document from firestore and takes the data then converts it into an Ingredient object
     * to return
     * @param doc
     * @return ingredient of type {@link Ingredient}
     * @see MealPlannerDBHelper
     * @see RecipeDBHelper
     */
    private static Ingredient createIngredient(DocumentSnapshot doc) {
        String name = doc.getId();
        String desc = (String) doc.getData().get("description");
        LocalDate bestBefore = LocalDate.parse((String) doc.getData().get("best before"));
        String location = (String) doc.getData().get("location");
        String unit = (String) doc.getData().get("unit");
        String category = (String) doc.getData().get("category");
        Integer amount = Integer.parseInt((String) doc.getData().get("amount"));

        Ingredient newIngredient = new Ingredient(name, desc, bestBefore, location, unit, category, amount);
        return newIngredient;
    }


    /**
     *  Called when the DBHelper singleton is instantiated - this happens in main activity onCreate().
     *  This ensures the private arraylist of ingredients stored in this class is always up to date with
     *      the firestore DB.
     *  This has nothing to do with the ingredientFragment recyclerview and does not rely on an adapter instance.
     */
    public void setupSnapshotListenerForLocalIngredientStorage(){
        db.collection("Ingredients")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.e("DB ERROR", error.getMessage());
                            return;
                        }

                        for(DocumentChange dc : value.getDocumentChanges()){
                            Ingredient ingredient = createIngredient(dc.getDocument());
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                ingredientInStorage.add(ingredient);
                            }

                            if(dc.getType() == DocumentChange.Type.MODIFIED){
                                ingredientInStorage.set(selectedIngPos, ingredient);
                            }

                            if(dc.getType() == DocumentChange.Type.REMOVED){
                                int position = ingredientInStorage.indexOf(ingredient);
                                //If the rvAdapter returns a valid position
                                if(position != -1){
                                    ingredientInStorage.remove(position);
                                }
                                else{
                                    Log.e("DB ERROR", "ERROR REMOVING INGREDIENT FROM STORAGE");
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Sets up a snapshot listener to update the ingredient recyclerview adapter accordingly. Because
     * it relies on an adapter instance this method is called in the IngredientFragments onCreateView method after the
     * associated RecyclerView adapter is instantiated and attached to the ingredientsRecyclerView;
     * @param adapter Instance of the IngredientRecyclerViewAdapter that is to be updated via firebase snapshot listener api callbacks
     */
    public static void setupSnapshotListenerForIngredientRVAdapter(IngredientsRecyclerViewAdapter adapter){
        db.collection("Ingredients")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.e("DB ERROR", error.getMessage());
                            return;
                        }

                        for(DocumentChange dc : value.getDocumentChanges()){
                            Ingredient ingredient = createIngredient(dc.getDocument());
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                adapter.addItem(ingredient);
                            }

                            if(dc.getType() == DocumentChange.Type.MODIFIED){
                                adapter.modifyIngredient(ingredient, selectedIngPos);
                            }

                            if(dc.getType() == DocumentChange.Type.REMOVED){
                                int position = adapter.getIngredientsList().indexOf(ingredient);
                                //If the rvAdapter returns a valid position
                                if(position != -1){
                                    adapter.deleteItem(position);
                                }
                                else{
                                    Log.e("DB ERROR", "ERROR REMOVING INGREDIENT FROM RECYCLERVIEW ADAPTER");
                                }
                            }
                        }
                    }
                });
    }

}
