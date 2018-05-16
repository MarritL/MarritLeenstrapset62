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
    private String mUid;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "alarm received");
        mUid = intent.getStringExtra("USERUID");

        updateRunstreak(mUid);

        // request new recipes from yummly api
        RecipesHelper recipesHelper = new RecipesHelper(context);
        recipesHelper.getRecipes(this);

    }

    @Override
    public void gotRecipes(ArrayList<Recipe> recipesArrayList, Context mContext) {

        // save recipes in database
        RecipeLab recipeLab = RecipeLab.getInstance();
        recipeLab.safeToDatabase(recipesArrayList);
        recipeLab.fillRecipeArray();

    }

    @Override
    public void gotError(String message) {

        Log.d(TAG, "got error: " + message);

    }

    // functionality to check if user clicked or not in that particular day, if not, consider as
    // a "NO" and set runstreak to 0.
    private void updateRunstreak(String Uid) {

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final String mUID = Uid;

        // get user data from database
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.child("users").child(mUID).getValue(User.class);

                // if user didn't say whether he ate vegetarian or not, consider as a NO
                if (!user.getClickedToday()) {
                    mDatabase.child("users").child(mUID).child("runStreak").setValue(0);
                }

                // set clickedToday to false again
                mDatabase.child("users").child(mUID).child("clickedToday").setValue(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(listener);
    }

}
