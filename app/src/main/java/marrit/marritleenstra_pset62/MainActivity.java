package marrit.marritleenstra_pset62;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";

    // firebase references
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    // variables
    User mUser;
    String mUid;
    public static BottomNavigationView navigation;

    // variables for shared preferences
    boolean mOnLaunchDone;
    public static SharedPreferences settings;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String FIRST_LAUNCH_DONE = "FirstLaunchDone";

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
                    transaction.commitAllowingStateLoss();

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
                    newTransaction.commitAllowingStateLoss();

                    return true;

                case R.id.navigation_community:

                    // create new fragment
                    CommunityFragment communityFragment = new CommunityFragment();

                    // add the fragment to the 'fragment_container' framelayout
                    FragmentTransaction communityTransaction = getSupportFragmentManager().beginTransaction();
                    communityTransaction.replace(R.id.container_fragment, communityFragment);
                    communityTransaction.addToBackStack(null);
                    communityTransaction.commitAllowingStateLoss();

                    return true;

                case R.id.navigation_settings:

                    // create new fragment
                    SettingsFragment settingsFragment = new SettingsFragment();

                    // add the fragment to the 'fragment_container' framelayout
                    FragmentTransaction settingsTransaction = getSupportFragmentManager().beginTransaction();
                    settingsTransaction.replace(R.id.container_fragment, settingsFragment);
                    settingsTransaction.addToBackStack(null);
                    settingsTransaction.commitAllowingStateLoss();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // restore shared preferences
        settings = getSharedPreferences(PREFS_NAME, 0);
        mOnLaunchDone = settings.getBoolean(FIRST_LAUNCH_DONE, false);

        // set up fireBase
        setUpFirebase();

        // check if user is signed in.
        isUserSignedIn();

        // set up bottomnavigation
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // read from fireBase database
        readFromDatabase();

        // run everything that has to be done on first time launch
        if (!mOnLaunchDone) {
            setRecurringAlarm(MainActivity.this, 19, 00, AlarmReceiver.class);
            setRecurringAlarm(this, 03, 10, MyNightJobs.class);
            mOnLaunchDone = true;
        }

    }

    // schedule daily alarms
    private void setRecurringAlarm(Context context, int hour, int minute, Class receiver) {

        // set the alarm at given time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        Log.d(TAG, "alarm was set at" + calendar.getTimeInMillis());

        // set action
        Intent intent = new Intent(context, receiver);
        intent.putExtra("USERUID", mUid);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set repeating interval
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
    }


    // check if user is signed in
    private void isUserSignedIn(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
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
    }

    // read from database
    private void readFromDatabase(){
        // read from fireBase database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // update user data
                updateUser(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // update user data
    private void updateUser(DataSnapshot dataSnapshot){
        // get current user data
        mUser = dataSnapshot.child("users").child(mUid).getValue(User.class);

        // display displayName in the bottomNavigation
        if (mUser != null) {
            String mDisplayname = mUser.getDisplayName();
            navigation.getMenu().findItem(R.id.navigation_user).setTitle(mDisplayname);

            // on launch the hometab is opened (initiated here, because needs the user data)
            if (mUser.getOnLaunch()){
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        }
    }


    public void setUpFirebase(){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUid = firebaseUser.getUid();
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        // log out and go back to the sign in page
        FirebaseAuth.getInstance().signOut();

        Intent newIntent = new Intent(MainActivity.this, SignInActivity.class);
        MainActivity.this.startActivity(newIntent);
        MainActivity.this.finish();
    }

    @Override
    public void onStop(){
        super.onStop();

        // save sharedPreferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(FIRST_LAUNCH_DONE, mOnLaunchDone);
        editor.commit();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // manage onLaunch data
        mDatabase.child("users").child(mUid).child("onLaunch").setValue(false);

    }

}
