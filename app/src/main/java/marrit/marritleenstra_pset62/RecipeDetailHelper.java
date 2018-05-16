package marrit.marritleenstra_pset62;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeDetailHelper implements Response.Listener<JSONObject>, Response.ErrorListener{

    public interface FragmentCallback {
        void gotRecipeDetails(Recipe recipe, Context mContext);
        void gotError(String message);
    }

    // declare variables
    private final Context mContext;
    private FragmentCallback mCallback;
    private final String TAG = "RECIPESDETAILHELPER";
    private final String APIkey = "358842bcce6b938ba3d887ccba20a6a1";
    private final String APIid = "12ae1b10";

    // constructor
    RecipeDetailHelper(Context context) {
        mContext = context;
    }

    // request recipes from yummly API
    void getRecipeDetails(FragmentCallback fragment, String id) {

        mCallback = fragment;

        // create new queue
        RequestQueue recipeDetailQueue = Volley.newRequestQueue(mContext);

        // create url
        String mUrl = "http://api.yummly.com/v1/api/recipe/" + id + "?_app_id=" + APIid + "&_app_key=" + APIkey;

        // create JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                mUrl, null, this, this);
        recipeDetailQueue.add(jsonObjectRequest);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mCallback.gotError(error.getMessage());
        Log.d(TAG," error: " + error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response) {

        String image = "";

        // translate all data from JSON to the recipe-class
        try {
            JSONArray imagesArray = response.getJSONArray("images");
            JSONObject imagesObject = imagesArray.getJSONObject(0);
            if (imagesObject.getString("hostedLargeUrl") != null) {
                image = imagesObject.getString("hostedLargeUrl");
            } else if (imagesObject.getString("hostedMediumUlr") != null) {
                image = imagesObject.getString("hostedMediumUlr");
            } else if (imagesObject.getString("hostedSmallUrl") != null) {
                image = imagesObject.getString("hostedSmallUrl");
            }

            JSONObject sourceObject = response.getJSONObject("source");
            String sourceUrl = sourceObject.getString("sourceRecipeUrl");
            JSONArray ingredientArray = response.getJSONArray("ingredientLines");
            ArrayList<String> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientArray.length(); i++) {
                ingredients.add(ingredientArray.get(i).toString());
            }
            String servings = response.getString("numberOfServings");
            String time = response.getString("totalTime");
            String id = response.getString("id");

            RecipeLab recipeLab = RecipeLab.getInstance();
            Recipe recipe = recipeLab.getRecipe(id);
            if (image.equals("")) {
                image = recipe.getImages().get(0);
            }
            recipe.setLargeImageUrl(image);
            recipe.setSourceUrl(sourceUrl);
            recipe.setIngredients(ingredients);
            recipe.setServings(Integer.valueOf(servings));
            recipe.setTime(time);

            mCallback.gotRecipeDetails(recipe, mContext);


        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());

        }
    }

}
