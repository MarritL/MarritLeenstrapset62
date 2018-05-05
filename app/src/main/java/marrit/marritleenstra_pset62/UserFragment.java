package marrit.marritleenstra_pset62;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * Created by Marrit on 4-12-2017.
 * Fragment displaying all the userdata to the user
 */
public class UserFragment extends Fragment {

    // variables
    User mUser;
    private static final String TAG = "USERFRAGMENT";

    // UI references
    TextView mUserName;
    TextView mRunStreak;
    TextView mTotalDays;
    TextView mTotalAnimals;
    TextView mTotalCO2;
    ImageButton mSettings;

    public UserFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get user data
        String userID = getArguments().getString("USERDATA");

        UserLab userLab = UserLab.getInstance();
        mUser = userLab.getUser(userID);
        System.out.println(TAG + ": user= " + mUser);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);

        // initiate UI components
        mUserName = view.findViewById(R.id.TV_username);
        mRunStreak = view.findViewById(R.id.TV_counter);
        mTotalDays = view.findViewById(R.id.TV_number_total_days);
        mTotalAnimals = view.findViewById(R.id.TV_number_total_animals);
        mTotalCO2 = view.findViewById(R.id.TV_number_total_CO2);
        mSettings = view.findViewById(R.id.IB_settings);

        // Set all UI components
        mUserName.setText(mUser.getDisplayName());
        mRunStreak.setText(String.valueOf(mUser.getRunStreak()));
        mTotalDays.setText(String.valueOf(mUser.getDaysVegetarian()));
        mTotalAnimals.setText(String.format("%.2f", mUser.getAnimalsSaved()));
        mTotalCO2.setText(String.format("%.1f", mUser.getCO2Avoided()));

        return view;
    }


}
