package marrit.marritleenstra_pset62;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";

    // firebase references
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabase;

    // variables
    int mSumDays;
    double mSumAnimals;
    double mSumCO2;
    int mSumParticipantsToday;
    int mSumParticipants;
    User mUser;
    String mUid;
    public static BottomNavigationView navigation;

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    // create new fragment
                    HomeFragment homeFragment = new HomeFragment();

                    // add user id
                    Bundle dataUser = new Bundle();
                    dataUser.putSerializable("USERDATA", mUser);
                    homeFragment.setArguments(dataUser);

                    // add the fragment to the 'fragment_container' framelayout
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container_fragment, homeFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    return true;
                case R.id.navigation_user:

                    // create new fragment
                    UserFragment userFragment = new UserFragment();

                    // add user id
                    Bundle userData = new Bundle();
                    userData.putSerializable("USERDATA", mUser);
                    userFragment.setArguments(userData);

                    // add the fragment to the 'fragment_container' framelayout
                    FragmentTransaction newTransaction = getSupportFragmentManager().beginTransaction();
                    newTransaction.replace(R.id.container_fragment, userFragment);
                    newTransaction.addToBackStack(null);
                    newTransaction.commit();

                    return true;

                case R.id.navigation_community:

                    // create new fragment
                    CommunityFragment communityFragment = new CommunityFragment();

                    // add the fragment to the 'fragment_container' framelayout
                    FragmentTransaction communityTransaction = getSupportFragmentManager().beginTransaction();
                    communityTransaction.replace(R.id.container_fragment, communityFragment);
                    communityTransaction.addToBackStack(null);
                    communityTransaction.commit();

                    return true;

                case R.id.navigation_settings:

                    // create new fragment
                    SettingsFragment settingsFragment = new SettingsFragment();

                    // add the fragment to the 'fragment_container' framelayout
                    FragmentTransaction settingsTransaction = getSupportFragmentManager().beginTransaction();
                    settingsTransaction.replace(R.id.container_fragment, settingsFragment);
                    settingsTransaction.addToBackStack(null);
                    settingsTransaction.commit();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        // check if user is signed in.
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // go to sign in page
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        };

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // get current user data
                mUid = firebaseUser.getUid();
                mUser = dataSnapshot.child("users").child(mUid).getValue(User.class);

                // display displayName in the bottomNavigation
                // check again if user is not null (evoked error when user unsubscribed)
                if (mUser != null) {
                    String mDisplayname = mUser.getDisplayName();
                    navigation.getMenu().findItem(R.id.navigation_user).setTitle(mDisplayname);


                    //TODO: after turning the screen errors (if already been in other tab)
                    // on launch the hometab is opened (initiated here, because needs the user data)

                    if (mUser.getOnLaunch()){
                        Log.d(TAG,"in mUser.getOnLaunch()");
                        navigation.setSelectedItemId(R.id.navigation_home);
                    }

                    /*if (savedInstanceState == null) {
                        Log.d(TAG,"in onDataChange if savedInstancestate is null");
                        navigation.setSelectedItemId(R.id.navigation_home);
                    }*/
                }

                // when data changed set all the community values to 0
                mSumDays = 0;
                mSumAnimals = 0;
                mSumCO2 = 0;
                mSumParticipantsToday = 0;
                mSumParticipants = 0;

                // set the community values
                for (DataSnapshot ds : dataSnapshot.child("users").getChildren()) {


                    // get values of all users in database
                    int DaysCommunityUser = Integer.valueOf(ds.child("daysVegetarian").getValue().toString());
                    double AnimalsCommunityUser = Double.valueOf(ds.child("animalsSaved").getValue().toString());
                    double CO2CommunityUser = Double.valueOf(ds.child("co2Avoided").getValue().toString());
                    boolean mClickedToday = Boolean.valueOf(ds.child("clickedToday").getValue().toString());


                    mSumDays = mSumDays + DaysCommunityUser;
                    mSumAnimals = mSumAnimals + AnimalsCommunityUser;
                    mSumCO2 = mSumCO2 + CO2CommunityUser;
                    mSumParticipants += 1;
                    if (mClickedToday){
                        mSumParticipantsToday +=1;
                    }
                }


                //Log.d(TAG, "mOnStartedisTrue");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // manage navigation display
        //mDatabase.child("users").child(mUid).child("onLaunch").setValue(false);
        /*if (savedInstanceState == null) {
                    Log.d(TAG,"in onDataChange if savedInstancestate is null");
                    navigation.setSelectedItemId(R.id.navigation_home);
        }*/

    }

}
