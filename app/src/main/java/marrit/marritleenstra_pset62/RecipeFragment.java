package marrit.marritleenstra_pset62;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Marrit on 5-3-2018.
 * Fragment displaying details of the recipes
 */
public class RecipeFragment extends Fragment implements RecipeDetailHelper.FragmentCallback {

    // declare UI components
    TextView mSourceName;
    TextView mRecipeName;
    TextView mNumberIngredients;
    ImageView mLargeImage;
    TextView mTime;
    ListView mIngredientsList;
    Button mGetDirections;

    // variables
    String mSourceUrl;
    ArrayList<String> mIngredientsArray;
    Recipe recipe;
    private static final String TAG = "RECIPEFRAGMENT";


    public RecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get recipe details
        recipe = (Recipe) getArguments().getSerializable("RECIPEDATA");

        RecipeDetailHelper recipeDetailHelper = new RecipeDetailHelper(getContext());
        recipeDetailHelper.getRecipeDetails(this, recipe.getId());

        RecipeLab recipeLab = RecipeLab.getInstance();
        recipe = recipeLab.getRecipe(recipe.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_recipe, container, false);

        // initialise UI components
        mLargeImage = view.findViewById(R.id.IV_detail_recipe);
        mNumberIngredients = view.findViewById(R.id.TV_detail_number_ingredients);
        mRecipeName = view.findViewById(R.id.TV_detail_recipename);
        mSourceName = view.findViewById(R.id.TV_detail_sourcename);
        mTime = view.findViewById(R.id.TV_detail_time);
        mGetDirections = view.findViewById(R.id.Button_click_for_details);
        mIngredientsList = view.findViewById(R.id.LV_detail_ingredientslist);

        return view;
    }

    @Override
    public void gotRecipeDetails(Recipe recipe, Context mContext) {

        System.out.println(TAG + ": gotRecipe: " + recipe);

        // update UI
        if(!recipe.getLargeImageUrl().equals("")){
            Picasso.with(getContext())
                    .load(recipe.getLargeImageUrl())
                    .into(mLargeImage);
        }
        mIngredientsArray = recipe.getIngredients();
        mNumberIngredients.setText(String.valueOf(mIngredientsArray.size()));
        mTime.setText(recipe.getTime());
        mRecipeName.setText(recipe.getRecipeName());
        mSourceName.setText(recipe.getSourceName());
        mSourceUrl = recipe.getSourceUrl();

        // initialise list array of ingredients with standard array adapter
        ArrayAdapter<String> ingredientsAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
                mIngredientsArray);

        mIngredientsList.setAdapter(ingredientsAdapter);

        // attach onClickListener to Button
        mGetDirections.setOnClickListener(new getDirectionsOnClick());
    }

    @Override
    public void gotError(String message) {
        System.out.println(TAG + ": gotERROR: " + message);

    }

    // class to open the recipe source url
    // source: http://android.okhelp.cz/open-url-with-browser-if-button-clicked-android-example/
    public class getDirectionsOnClick implements Button.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(mSourceUrl));
            startActivity(browserIntent);
        }
    }


}
