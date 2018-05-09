package marrit.marritleenstra_pset62;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


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
    ImageButton mInfo;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get user data
        //String userID = getArguments().getString("USERDATA");
        mUser = (User) getArguments().getSerializable("USERDATA");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // initiate UI components
        mUserName = view.findViewById(R.id.TV_username);
        mRunStreak = view.findViewById(R.id.TV_counter);
        mTotalDays = view.findViewById(R.id.TV_number_total_days);
        mTotalAnimals = view.findViewById(R.id.TV_number_total_animals);
        mTotalCO2 = view.findViewById(R.id.TV_number_total_CO2);
        mInfo = view.findViewById(R.id.IB_info);

        // Set all UI components
        mUserName.setText(mUser.getDisplayName());
        mRunStreak.setText(String.valueOf(mUser.getRunStreak()));
        mTotalDays.setText(String.valueOf(mUser.getDaysVegetarian()));
        mTotalAnimals.setText(String.format("%.2f", mUser.getAnimalsSaved()));
        mTotalCO2.setText(String.format("%.1f", mUser.getCO2Avoided()));

        // set listener
        mInfo.setOnClickListener(new onInfoClickListener());

        return view;
    }

    // on click listener
    private class onInfoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            showInfoDialog();

        }
    }

    // show dialog that makes sure if the user really wants to delete the account
    private void showInfoDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info, null);
        dialogBuilder.setView(dialogView);

        // Let the user know what the dialog is for
        dialogBuilder.setMessage("Info about calculations used");

        // OK-button
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing and close dialog
            }
        });

        // when the building is done show the dialog in the app screen
        AlertDialog InfoDialog = dialogBuilder.create();
        InfoDialog.show();
    }

}