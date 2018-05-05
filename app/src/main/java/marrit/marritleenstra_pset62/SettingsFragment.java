package marrit.marritleenstra_pset62;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
        mChangeDisplayname.setOnClickListener(new changeDisplayName());
        mLogOut.setOnClickListener(new goToNextActivity());
        mUnsubscribe.setOnClickListener(new unsubscribeClicked());

        return view;
    }

    public class goToNextActivity implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent newIntent;
            Activity activity = getActivity();

            //go to right activity
            if (view == mChangeEmail){
                newIntent = new Intent(activity, ChangeEmailActivity.class);
                activity.startActivity(newIntent);
            }
            if (view == mChangePassword){
                newIntent = new Intent(activity, ChangePasswordActivity.class);
                activity.startActivity(newIntent);
            }
            if (view == mLogOut){
                FirebaseAuth.getInstance().signOut();
                newIntent = new Intent(activity, SignInActivity.class);
                activity.startActivity(newIntent);
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

                // get current user info
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String mUID = user.getUid();

                // TODO: werkt nog niet delete user also from database
                // sign-out (before deleting from database)
                FirebaseAuth.getInstance().signOut();

                // destroy all fragements
                //MainActivity.getSupportFragmentManager().beginTransaction().remove(UserFragment.class).commit();

                Intent newIntent = new Intent(getActivity(), SignInActivity.class);
                getActivity().startActivity(newIntent);

                // delete the userdata from the database
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("recipes").child(mUID).removeValue();
                mDatabase.child("users").child(mUID).removeValue();

                System.out.println(TAG + ": after database values removed");

                // block of code from firebase guide on user management
                user.delete()
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

        final EditText mDisplayName = dialogView.findViewById(R.id.ET_displayname);

        // Let the user know what the dialog is for
        dialogBuilder.setMessage("Change Display name");

        // OK-button
        dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                String mNewDisplayName = mDisplayName.getText().toString();

                // check if user gave To-Do title
                if (!mNewDisplayName.equals("")) {

                    // get database reference
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    // get current user info
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String mUID = user.getUid();

                    // update email address in database as well
                    mDatabase.child("users").child(mUID).child("displayName").setValue(mNewDisplayName);

                    // empty edit-text
                    mDisplayName.getText().clear();

                } else {
                    /*mDisplayName.setError(getString(R.string.error_field_required));
                    View focusView = mDisplayName;
                    focusView.requestFocus();*/
                    // TODO: if user gave no title, yell at him with focus etc
                    Toast.makeText(getActivity(), "Give a display name!", Toast.LENGTH_SHORT).show();
                }
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
