package marrit.marritleenstra_pset62;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
    EditText mDisplayName;

    // variables
    private static final String TAG = "SETTINGSFRAGMENT";
    String mUid;
    User user;

    // firebase references
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;


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
        mChangeDisplayname.setOnClickListener(new changeDisplayName());
        mLogOut.setOnClickListener(new goToNextActivity());
        mUnsubscribe.setOnClickListener(new unsubscribeClicked());

        // initiate firebase references
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUid = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    public class goToNextActivity implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            //go to right fragment or activity
            if (view == mChangeEmail){
                // create new fragment
                ChangeEmailFragment emailFragment = new ChangeEmailFragment();

                // add the fragment to the 'container_fragment' framelayout
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_fragment, emailFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            if (view == mChangePassword){

                // create new fragment
                ChangePasswordFragment passwordFragment = new ChangePasswordFragment();

                // add the fragment to the 'container_fragment' framelayout
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_fragment, passwordFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
            if (view == mLogOut){
                FirebaseAuth.getInstance().signOut();
                Intent newIntent = new Intent(getActivity(), SignInActivity.class);
                getActivity().startActivity(newIntent);
                getActivity().finish();

                // manage onLaunch data to restart with homeFragment
                mDatabase.child("users").child(mUid).child("onLaunch").setValue(true);
            }
        }
    }

    public class changeDisplayName implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            showDisplayNameDialog();
        }
    }

    public class unsubscribeClicked implements View.OnClickListener {

        @Override
        public void onClick(View view){
            showAreYouSureDialog();
        }
    }

    // show dialog that makes sure if the user really wants to delete the account
    private void showAreYouSureDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = SettingsFragment.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_unsubscribe,null);
        dialogBuilder.setView(dialogView);

        // OK-button
        dialogBuilder.setPositiveButton("Delete account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                System.out.println(TAG + ": on delete called");

                // sign-out (before deleting from database)
                FirebaseAuth.getInstance().signOut();

                Intent newIntent = new Intent(getActivity(), SignInActivity.class);
                getActivity().startActivity(newIntent);

                // delete the userdata from the database
                mDatabase.child("recipes").child(mUid).removeValue();
                mDatabase.child("users").child(mUid).removeValue();

                System.out.println(TAG + ": after database values removed");

                // block of code from firebase guide on user management
                mFirebaseUser.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");

                                }
                            }
                        });
            }

        });

        // cancel button
        dialogBuilder.setNegativeButton("Cancel", new CancelListener());

        // when the building is done show the dialog in the app screen
        AlertDialog changeDisplayName = dialogBuilder.create();
        changeDisplayName.show();
    }

    // show a dialog which allows user to change the display name
    private void showDisplayNameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = SettingsFragment.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_display_name,null);
        dialogBuilder.setView(dialogView);

        mDisplayName = dialogView.findViewById(R.id.ET_displayname);

        // Let the user know what the dialog is for
        dialogBuilder.setMessage("Change Display name");

        // OK-button
        dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                String mNewDisplayName = mDisplayName.getText().toString();

                // check if user gave displayName
                if (mNewDisplayName.equals("")) {
                    // if not set default displayname
                    mNewDisplayName = mFirebaseUser.getEmail();
                    mNewDisplayName = mNewDisplayName.split("@")[0];
                }

                // update displayname in database
                mDatabase.child("users").child(mUid).child("displayName").setValue(mNewDisplayName);
                System.out.println(TAG + ": displayname changed");
            }

        });

        // cancel button
        dialogBuilder.setNegativeButton("Cancel", new CancelListener());

        // when the building is done show the dialog in the app screen
        AlertDialog changeDisplayName = dialogBuilder.create();
        changeDisplayName.show();
    }

    // cancel listener for dialog box
    public class CancelListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            // do nothing and go back
        }
    }

}
