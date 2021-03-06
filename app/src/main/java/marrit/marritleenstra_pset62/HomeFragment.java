package marrit.marritleenstra_pset62;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;


/**
 * Created by Marrit on 4-12-2017.
 * Fragment displaying recipes to the user.
 */
public class HomeFragment extends Fragment {

    // UI references
    Button mYesButton;
    Button mNoButton;
    TextView mQuestion;
    TextView mClickedNo;
    TextView mClickedYes;
    public ImageView mIVRecipe;
    private GridView mGridView;
    TextView mYummlySearch;
    ImageView mYummlyLogo;

    // variables
    ArrayList<Recipe> recipesArrayList;
    User mUser;
    String mSourceUrl;
    String mLogoUrl;
    String mUid;

    // Firebase references
    DatabaseReference mDatabase;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get user data
        mUser = (User) getArguments().getSerializable("USERDATA");
        mUid = mUser.getUID();

        // initiate firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // get recipe data
        RecipeLab recipeLab = RecipeLab.getInstance();
        recipesArrayList = recipeLab.getRecipes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, fragment_container, false);

        // initiate UI components
        mYesButton = v.findViewById(R.id.button_yes);
        mNoButton = v.findViewById(R.id.button_no);
        mQuestion = v.findViewById(R.id.TV_question);
        mClickedNo = v.findViewById(R.id.TV_clicked_no);
        mClickedYes = v.findViewById(R.id.TV_clicked_yes);
        mIVRecipe = v.findViewById(R.id.IV_recipe);
        mGridView = v.findViewById(R.id.gridView);
        mYummlySearch = v.findViewById(R.id.TV_yummly_search);
        mYummlyLogo = v.findViewById(R.id.IV_yummly_logo);

        // control visibility of vegetarian question and answers
        if (mUser.getClickedToday() & (mUser.getRunStreak() == 0)) {    // clicked NO
            mQuestion.setVisibility(View.INVISIBLE);
            mClickedYes.setVisibility(View.INVISIBLE);
            mYesButton.setVisibility(View.INVISIBLE);
            mNoButton.setVisibility(View.INVISIBLE);
        } else if(mUser.getClickedToday()){                             // clicked YES
            mQuestion.setVisibility(View.INVISIBLE);
            mClickedNo.setVisibility(View.INVISIBLE);
            mYesButton.setVisibility(View.INVISIBLE);
            mNoButton.setVisibility(View.INVISIBLE);
        }
        else {
            mClickedNo.setVisibility(View.INVISIBLE);
            mClickedYes.setVisibility(View.INVISIBLE);
        }

        // display recipes
        GridViewAdapter mAdapter = new GridViewAdapter(getContext(), R.layout.grid_item, recipesArrayList);
        mGridView.setAdapter(mAdapter);

        // initiate yummly api use requirements
        mSourceUrl =  recipesArrayList.get(0).getYummlyUrl();
        mYummlySearch.setText(recipesArrayList.get(0).getYummlySearch());
        mLogoUrl = recipesArrayList.get(0).getYummlyLogoUrl();

        // display yummly logo obligatory by api use requirements
        Picasso.with(getContext())
                .load(mLogoUrl)
                .resize(120,120)
                .centerInside()
                .into(mYummlyLogo);

        // attach listeners
        mYesButton.setOnClickListener(new TodayVegetarianClickListener());
        mNoButton.setOnClickListener(new TodayVegetarianClickListener());
        mGridView.setOnItemClickListener(new MyRecipeClickedListener());
        mYummlySearch.setOnClickListener(new goToSourceOnClick());

        return v;
    }

    // update user's values in database based on if they clicked yes or no
    private class TodayVegetarianClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            // get the user's values
            String mUID = mUser.getUID();
            int daysVegetarian = mUser.getDaysVegetarian();
            int runStreak = mUser.getRunStreak();
            double co2 = mUser.getCO2Avoided();
            double animals = mUser.getAnimalsSaved();

            // hide question
            mQuestion.setVisibility(View.INVISIBLE);
            mYesButton.setVisibility(View.INVISIBLE);
            mNoButton.setVisibility(View.INVISIBLE);

            // update database based on button clicked
            if (view == mYesButton) {

                // display clickedYes message
                mClickedYes.setVisibility(View.VISIBLE);

                // update user's values
                daysVegetarian++;
                runStreak++;
                co2 = co2 +1.5;
                animals = animals+0.2;

                // update user's values
                mDatabase.child("users").child(mUID).child("clickedToday").setValue(true);
                mDatabase.child("users").child(mUID).child("daysVegetarian").setValue(daysVegetarian);
                mDatabase.child("users").child(mUID).child("runStreak").setValue(runStreak);
                mDatabase.child("users").child(mUID).child("co2Avoided").setValue(co2);
                mDatabase.child("users").child(mUID).child("animalsSaved").setValue(animals);
            }
            else if (view == mNoButton) {

                // display clickedNO message
                mClickedNo.setVisibility(View.VISIBLE);

                mDatabase.child("users").child(mUID).child("clickedToday").setValue(true);
                mDatabase.child("users").child(mUID).child("runStreak").setValue(0);
            }
        }
    }

    // class to open the recipe source url
    // source: http://android.okhelp.cz/open-url-with-browser-if-button-clicked-android-example/
    public class goToSourceOnClick implements Button.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(mSourceUrl));
            startActivity(browserIntent);
        }
    }

    // handle click on one of the recipes
    private class MyRecipeClickedListener implements GridView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // get the recipe clicked
            Recipe mRecipe = (Recipe) parent.getItemAtPosition(position);

            // create new fragment
            RecipeFragment recipeFragment = new RecipeFragment();

            // add Recipe data
            Bundle dataRecipe = new Bundle();
            dataRecipe.putSerializable("RECIPEDATA", mRecipe);
            recipeFragment.setArguments(dataRecipe);

            // add the fragment to the 'container_fragment' framelayout
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_fragment, recipeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
