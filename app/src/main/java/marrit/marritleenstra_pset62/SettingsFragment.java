package marrit.marritleenstra_pset62;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by Marrit Leenstra. 4-5-2018
 * Fragment where user can change the app and user settings
 */
public class SettingsFragment extends Fragment {

    // UI references
    TextView mChangePassword;
    TextView mChangeEmail;
    TextView mChangeDisplayname;
    TextView mLogOut;
    TextView mUnsubscribe;

    // variables
    private static final String TAG = "SETTINGSFRAGMENT";

    // firebase references
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //initiate UI references
        mChangeDisplayname = view.findViewById(R.id.change_displayname);
        mChangeEmail = view.findViewById(R.id.change_email);
        mChangePassword = view.findViewById(R.id.change_password);
        mLogOut = view.findViewById(R.id.log_out);
        mUnsubscribe = view.findViewById(R.id.unsubscribe);

        //set listeners on UI references
        mChangeEmail.setOnClickListener(new goToNextActivity());
        mChangePassword.setOnClickListener(new goToNextActivity());
        //mChangeDisplayname.setOnClickListener(new changeDisplayName());
        mLogOut.setOnClickListener(new goToNextActivity());
        //mUnsubscribe.setOnClickListener(new unsubscribeClicked());

        return view;
    }

    public class goToNextActivity implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent newIntent;

            // go to the right activity
            switch (view.getId()){
                case R.id.change_email:
                    newIntent = new Intent(getActivity(), ChangeEmailActivity.class);
                    getActivity().startActivity(newIntent);
                case R.id.change_password:
                    newIntent = new Intent(getActivity(), ChangePasswordActivity.class);
                    getActivity().startActivity(newIntent);
                case R.id.log_out:
                    FirebaseAuth.getInstance().signOut();
                    newIntent = new Intent(getContext(), SignInActivity.class);
                    getContext().startActivity(newIntent);
            }

        }

    }


}
