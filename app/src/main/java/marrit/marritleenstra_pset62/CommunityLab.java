package marrit.marritleenstra_pset62;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommunityLab {

    // variables
    private static CommunityLab sCommunityLab;
    private static DatabaseReference mDatabase;
    private static FirebaseUser mFirebaseUser;
    private static User mUser;
    private static Community mCommunity;
    private static final String TAG = "COMMUNITYLAB";

    // create instance
    public static CommunityLab getInstance() {
        System.out.println(TAG + ": getInstance called");
        if (sCommunityLab == null){
            sCommunityLab = new CommunityLab();
        }
        return sCommunityLab;
    }

    // create a new CommunityLab
    private CommunityLab() {
        System.out.println(TAG + ": made new CommunityLab()");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    // get communitydata
    public Community getCommunity(){
        System.out.println(TAG + ": GetCommunityData() called");
        return mCommunity;
    }

    // update community
    private void updateCommunity(int SumDays, double SumAnimals, double SumCO2, int SumParticipants, int SumParticipantsToday){
        mCommunity.setSumDays(SumDays);
        mCommunity.setSumAnimals(SumAnimals);
        mCommunity.setSumCO2(SumCO2);
        mCommunity.setSumParticipants(SumParticipants);
        mCommunity.setSumParticipantsToday(SumParticipantsToday);
    }

    // fill the lab with the sum of the userdata from the database
    public void fillCommunityData() {
        System.out.println(TAG + ": FillCommunityData() called");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // initiate community values, all zero when data changed
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

                    if (mCommunity == null){
                        mCommunity = new Community(mSumDays, mSumAnimals, mSumCO2, mSumParticipants, mSumParticipantsToday);
                    }
                    else{
                        updateCommunity(mSumDays, mSumAnimals, mSumCO2, mSumParticipants, mSumParticipantsToday);
                    }
                }
                System.out.println(TAG + ": done with fillCommunityData()");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG + ": Failed to read value.", databaseError.toException());

            }
        });
    }
}
