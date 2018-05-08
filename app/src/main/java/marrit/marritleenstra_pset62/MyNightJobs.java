package marrit.marritleenstra_pset62;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyNightJobs extends BroadcastReceiver implements RecipesHelper.Callback {

    // variables
    private static final String TAG = "MYNIGHTJOBS";

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println(TAG + " alarm received");

        updateRunstreak();

        // request new recipes from yummly api
        RecipesHelper recipesHelper = new RecipesHelper(context);
        recipesHelper.getRecipes(this);

    }

    @Override
    public void gotRecipes(ArrayList<Recipe> recipesArrayList, Context mContext) {

        System.out.println(TAG + " got recipes");
        RecipeLab recipeLab = RecipeLab.getInstance();
        recipeLab.safeToDatabase(recipesArrayList);
        recipeLab.fillRecipeArray();

    }

    @Override
    public void gotError(String message) {

        System.out.println(TAG + " got error: " + message);

    }

    // functionality to check if user clicked or not in that particular day, if not, consider as
    // a "NO" and set runstreak to 0.
    private void updateRunstreak() {

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // get user data from database
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get current user data
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                if (mUser != null) {
                    String uid = mUser.getUid();
                    User user = dataSnapshot.child("users").child(mUser.getUid()).getValue(User.class);

                    // if user didn't say whether he ate vegetarian or not, consider as a NO
                    if (!user.getClickedToday()) {
                        mDatabase.child("users").child(uid).child("runStreak").setValue(0);
                    }

                    // set clickedToday to false again
                    mDatabase.child("users").child(uid).child("clickedToday").setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(listener);
    }

}
