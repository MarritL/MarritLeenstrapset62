package marrit.marritleenstra_pset62;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmailActivity extends AppCompatActivity {

    // variables
    private static final String TAG = "CHANGEEMAILACTIVITY";
    private String mUid;

    // UI references
    EditText mNewEmail;
    Button mChangeEmail;

    // firebase references
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_email);

        // initiate firebase references
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // make sure user is signed in.
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mFirebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // go to signinActivity
                    Intent intent = new Intent(ChangeEmailActivity.this, SignInActivity.class);
                    ChangeEmailActivity.this.startActivity(intent);
                    ChangeEmailActivity.this.finish();
                }
            }
        };

        mUid = mFirebaseUser.getUid();

        // initiate UI references
        mNewEmail = findViewById(R.id.ET_email);
        mChangeEmail = findViewById(R.id.BUTTON_change_email);

        // set listeners
        mChangeEmail.setOnClickListener(new changeEmailOnClick());

        // manage launch for mainActivity
        mDatabase.child("users").child(mUid).child("onLaunch").setValue(true);
    }

    private class changeEmailOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            // get current user info
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String mUID = user.getUid();

            boolean cancel = false;
            View focusView = null;

            // Check if new email is valid
            final String email = mNewEmail.getText().toString();

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mNewEmail.setError(getString(R.string.error_field_required));
                focusView = mNewEmail;
                cancel = true;
            } else if (!RegisterActivity.isEmailValid(email)) {
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
                user.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");

                                    // let the user know that he has to login with the new email adress
                                    Toast.makeText(ChangeEmailActivity.this, R.string.use_new_emailadress,Toast.LENGTH_SHORT).show();

                                    // get database reference
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                                    // update email address in database as well
                                    mDatabase.child("users").child(mUID).child("email").setValue(email);

                                    // return to settings
                                    Intent intent = new Intent(ChangeEmailActivity.this, MainActivity.class);
                                    ChangeEmailActivity.this.startActivity(intent);
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
