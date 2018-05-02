package marrit.marritleenstra_pset62;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RecipeLab {

    // variables
    private static RecipeLab sRecipeLab;
    private static ArrayList<Recipe> mRecipeArrayList;
    private static DatabaseReference mDatabase;
    private static FirebaseUser mFirebaseUser;
    private static final String TAG = "RECIPELAB";

    public static RecipeLab getInstance() {
        System.out.println(TAG + ": RecipeLab getInstance called");
        if (sRecipeLab == null){
            sRecipeLab = new RecipeLab();
        }
        return sRecipeLab;
    }

    // create a new recipes ArrayList
    private RecipeLab() {
        System.out.println(TAG + ": made new RecipeLab()");
        mRecipeArrayList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    // get the recipes
    public ArrayList<Recipe> getRecipes(){
        //fillRecipeArray();
        return mRecipeArrayList;
    }


    // fill the ArrayList with the recipes from the database
    public void fillRecipeArray() {
        System.out.println("RECIPELAB: called fillRecipeArray()");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mFirebaseUser != null) {
                    String uid = mFirebaseUser.getUid();
                    //User user = dataSnapshot.child("users").child(uid).getValue(User.class); //TODO: delete when yummly api works again
                    String recipes = dataSnapshot.child("recipes").child(uid).getValue(String.class); //TODO: turn on when yummly api works again

                    // get recipes
                    //String recipes = user.getRecipes();   //TODO: delete when yummly api works again
                    mRecipeArrayList = castToArray(recipes);
                    System.out.println("RECIPELAB: done with fillRecipeArray()");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("RECIPELAB", "Failed to read value.", databaseError.toException());

            }
        });
    }

    // find recipe with specified id
    public Recipe getRecipe(String id) {
        for (Recipe recipe : mRecipeArrayList) {
            if (recipe.getId().equals(id)) {
                return recipe;
            }
        }
        return null;
    }

    // add recipe to arrayList
    public void addRecipe(Recipe recipe) {
        mRecipeArrayList.add(recipe);
    }

    // delete recipe from arrayList
    public void deleteRecipe(Recipe recipe) {
        mRecipeArrayList.remove(recipe);
    }

    // cast a string in JSON format (saved like that in database) to a ArrayList
    public static ArrayList<Recipe> castToArray(String stringJSONFormat) {

        Gson gson = new Gson();
        String jsonRecipes = stringJSONFormat;

        if (jsonRecipes != null) {
            Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();
            ArrayList<Recipe> mSavedRecipes = gson.fromJson(jsonRecipes, type);

            return mSavedRecipes;
        }
        return null;
    }

    public static void safeToDatabase(ArrayList<Recipe> recipeArrayList){
        Gson gson = new Gson();
        String jsonRecipes = gson.toJson(recipeArrayList);

        // save in database
        if (mFirebaseUser != null) {
            String uid = mFirebaseUser.getUid();
            mDatabase.child("recipes").child(uid).setValue(jsonRecipes);
            //mDatabase.child("users").child(uid).child("recipes").setValue(jsonRecipes);
            System.out.println("RECIPELAB: saved recipes in database");
        }
    }
}
