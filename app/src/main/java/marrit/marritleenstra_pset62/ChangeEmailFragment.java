package marrit.marritleenstra_pset62;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Created by Marrit 5-52018
 * Fragment where user can change his email adress
 */
public class ChangeEmailFragment extends Fragment {

    // variables
    private static final String TAG = "CHANGEEMAILFRAGMENT";

    // UI references
    EditText mNewEmail;
    Button mChangeEmail;
    String mUID;
    String mEmail;

    // firebase references
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;


    public ChangeEmailFragment() {
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
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        // initiate UI references
        mNewEmail = view.findViewById(R.id.ET_email);
        mChangeEmail = view.findViewById(R.id.BUTTON_change_email);

        // set listeners
        mChangeEmail.setOnClickListener(new changeEmailOnClick());


        return view;
    }

    private class changeEmailOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            // get current user info
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mUID = mFirebaseUser.getUid();

            boolean cancel = false;
            View focusView = null;

            // Check if new email is valid
            mEmail = mNewEmail.getText().toString();

            // Check for a valid email address.
            if (TextUtils.isEmpty(mEmail)) {
                mNewEmail.setError(getString(R.string.error_field_required));
                focusView = mNewEmail;
                cancel = true;
            } else if (!RegisterActivity.isEmailValid(mEmail)) {
                mNewEmail.setError(getString(R.string.error_invalid_email));
                focusView = mNewEmail;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt to change password and focus
                // form field with an error.
                focusView.requestFocus();
                System.out.println(TAG + ": before return");
            }
            else {
                // change emailadress
                // block of code from firebase guide on user management
                mFirebaseUser.updateEmail(mEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");

                                    // let the user know that he has to login with the new email adress
                                    Toast.makeText(getContext(), R.string.use_new_emailadress,Toast.LENGTH_SHORT).show();

                                    // get database reference
                                    mDatabase = FirebaseDatabase.getInstance().getReference();

                                    // update email address in database as well
                                    mDatabase.child("users").child(mUID).child("email").setValue(mEmail);

                                    // return to settings
                                    MainActivity.navigation.setSelectedItemId(R.id.navigation_settings);
                                }
                                //TODO check if re-authentication is needed
                                /*else if(!task.isSuccessful()) {
                                    try {
                                        throw task.getException()
                                    } catch(FirebaseAuthRecentLoginRequiredException e) {
                                        // Get auth credentials from the user for re-authentication. The example below shows
                                        // email and password credentials but there are multiple possible providers,
                                        // such as GoogleAuthProvider or FacebookAuthProvider.
                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential(email, "password1234");

/                                       // Prompt the user to re-provide their sign-in credentials
                                        user.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "User re-authenticated.");
                                                    }
                                                });
                                    }
                                }*/


                            }
                        });
            }
        }
    }


}
