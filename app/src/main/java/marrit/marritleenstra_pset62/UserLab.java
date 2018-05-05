package marrit.marritleenstra_pset62;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marrit on 15-1-2018.
 * The User lab contains all the methods to manipulate the data stash for User objects in
 * a singleton structure.
 */

public class UserLab {

    // variables
    private static UserLab sUserLab;
    private ArrayList<User> mUserArrayList;
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
        mUserArrayList = new ArrayList<>();
    }

    // create a new User
    public void newUser(String UID, String email, String displayName){
        User user = new User(UID, email, displayName);
        mDatabase.child("users").child(UID).setValue(user);
        System.out.println(TAG + ": added new user to database");
    }

    /*public User getUser(){
        System.out.print(TAG + ": getUser() called");
        return mUser;
    }*/

    // fill the ArrayList with the users from the database
    public void fillUserData() {
        System.out.println(TAG + ": fillUserData() called");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mFirebaseUser != null) {

                    for (DataSnapshot ds : dataSnapshot.child("users").getChildren()) {
                        User user = ds.getValue(User.class);
                        mUserArrayList.add(user);
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

    // find user with specified id
    public User getUser(String id) {
        for (User user : mUserArrayList) {
            if (user.getUID().equals(id)) {

                System.out.println(TAG + ": getUser(uid) called");
                return user;
            }
        }
        return null;
    }



}
