package marrit.marritleenstra_pset62;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marrit on 15-1-2018.
 * The User lab contains all the methods to manipulate the data stash for User objects in
 * a singleton structure.
 */

public class UserLab {

    // variables
    private static UserLab sUserLab;
    private static DatabaseReference mDatabase;
    private static FirebaseUser mFirebaseUser;
    private static User mUser;
    private static final String TAG = "USERLAB";

    // create instance
    public static UserLab getInstance() {
        System.out.println(TAG + ": Userlab getInstance called");
        if (sUserLab == null){
            sUserLab = new UserLab();
        }
        return sUserLab;
    }

    // create a new UserLab
    private UserLab() {
        System.out.println(TAG + ": made new UserLab()");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    // create a new User
    public void newUser(String UID, String email, String displayName){
        User user = new User(UID, email, displayName);
        mDatabase.child("users").child(UID).setValue(user);
        System.out.println(TAG + ": added new user to database");
    }

    public User getUser(){
        System.out.print(TAG + ": getUser() called");
        return mUser;
    }

    // fill the ArrayList with the recipes from the database
    public void fillUserData() {
        System.out.println(TAG + ": getUserData() called");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mFirebaseUser != null) {
                    String uid = mFirebaseUser.getUid();
                    mUser = dataSnapshot.child("users").child(uid).getValue(User.class);

                    // initate community values, all zero when data changed
                    int mSumDays = 0;
                    double mSumAnimals = 0;
                    double mSumCO2 = 0;
                    int mSumParticipantsToday = 0;
                    int mSumParticipants = 0;

                    // set the community values
                    for (DataSnapshot ds : dataSnapshot.child("users").getChildren()) {

                        // get values of all users in database
                        int DaysCommunityUser = Integer.valueOf(ds.child("daysVegetarian").getValue().toString());
                        double AnimalsCommunityUser = Double.valueOf(ds.child("animalsSaved").getValue().toString());
                        double CO2CommunityUser = Double.valueOf(ds.child("co2Avoided").getValue().toString());
                        boolean mClickedToday = Boolean.valueOf(ds.child("clickedToday").getValue().toString());

                        // make sums
                        mSumDays = mSumDays + DaysCommunityUser;
                        mSumAnimals = mSumAnimals + AnimalsCommunityUser;
                        mSumCO2 = mSumCO2 + CO2CommunityUser;
                        mSumParticipants += 1;
                        if (mClickedToday) {
                            mSumParticipantsToday += 1;
                        }
                    }

                    System.out.println(TAG + ": done with fillUserData()");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG + ": Failed to read value.", databaseError.toException());

            }
        });
    }

}
