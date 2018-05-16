package marrit.marritleenstra_pset62;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Marrit on 4-12-2017.
 * Fragment displaying all the community data to the user
 */
public class CommunityFragment extends Fragment {

    // UI references
    TextView mTotalDaysCommunity;
    TextView mTotalAnimalsCommunity;
    TextView mTotalCO2Community;
    TextView mTotalParticipantsToday;
    TextView mTotalParticipants;

    // variables
    private Community mCommunity;


    public CommunityFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get community data (commuity is sum of all users)
        CommunityLab communityLab = CommunityLab.getInstance();
        mCommunity = communityLab.getCommunity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // initiate UI components
        mTotalDaysCommunity = view.findViewById(R.id.TV_number_total_days_community);
        mTotalAnimalsCommunity = view.findViewById(R.id.TV_number_total_animals_community);
        mTotalCO2Community = view.findViewById(R.id.TV_number_total_CO2_community);
        mTotalParticipantsToday = view.findViewById(R.id.TV_number_total_participants_today);
        mTotalParticipants = view.findViewById(R.id.TV_number_total_participants);

        // get community dataStrings
        String mSumDays = String.valueOf(mCommunity.getSumDays());
        String mSumAnimals = String.format("%.2f", mCommunity.getSumAnimals());
        String mSumCO2 = String.valueOf(mCommunity.getSumCO2());
        String mSumParticipants = String.valueOf(mCommunity.getSumParticipants());
        String mSumParticipantsToday = String.valueOf(mCommunity.getSumParticipantsToday());

        // display community data
        mTotalDaysCommunity.setText(mSumDays);
        mTotalAnimalsCommunity.setText(mSumAnimals);
        mTotalCO2Community.setText(mSumCO2);
        mTotalParticipants.setText(mSumParticipants);
        mTotalParticipantsToday.setText(mSumParticipantsToday);

        return view;
    }


}
