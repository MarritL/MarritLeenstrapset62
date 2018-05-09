package marrit.marritleenstra_pset62;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";

    // firebase references
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
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
                    transaction.commit();

                    return true;
                case R.id.navigation_user:

                    // create new fragment
                    UserFragment userFragment = new UserFragment();

                    // add user id
                    Bundle userData = new Bundle();
                    userData.putSerializable("USERDATA", mUser);
                    System.out.println(TAG + ": mUser in navigation_user = " + mUser);
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

        // restore shared preferences
        settings = getSharedPreferences(PREFS_NAME, 0);
        mOnLaunchDone = settings.getBoolean(FIRST_LAUNCH_DONE, false);
        System.out.println(TAG + " mFirstlaunchDone3: " + mOnLaunchDone);

        // set up fireBase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUid = firebaseUser.getUid();

        // check if user is signed in.
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

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // read from fireBase database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // get current user data
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
                        mDatabase.child("users").child(mUid).child("onLaunch").setValue(false);
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

        // run everything that has to be done on first time launch
        System.out.println(TAG + " mFirstLaunchDone1 =" + mOnLaunchDone);
        if (!mOnLaunchDone) {
            setRecurringAlarm(MainActivity.this, 12, 22, AlarmReceiver.class);
            setRecurringAlarm(this, 10, 22, MyNightJobs.class);

            mOnLaunchDone = true;
            System.out.println(TAG + " mFirstLauncheDone2 =" + mOnLaunchDone);
        }


    }

    // schedule daily alarms
    private void setRecurringAlarm(Context context, int hour, int minute, Class receiver) {

        // set the alarm at given time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        Log.d("DEBUG", "alarm was set at" + calendar.getTimeInMillis());

        // set action
        Intent intent = new Intent(context, receiver);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set repeating interval
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println(TAG + ": onStop() called");

        // save sharedPreferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(FIRST_LAUNCH_DONE, mOnLaunchDone);
        editor.commit();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        System.out.println(TAG + ": onDestroy() called");

        // manage onLaunch data to restart with homeFragment
        // DIT WERKT NIET
        //mDatabase.child("users").child(mUid).child("onLaunch").setValue(true);

    }


}
