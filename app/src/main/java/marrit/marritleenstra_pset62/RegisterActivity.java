package marrit.marritleenstra_pset62;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Marrit on 10-12-2017.
 * Activity where a user registers for the use of the app.
 * Uses FireBase authentication
 */

public class RegisterActivity extends AppCompatActivity implements RecipesHelper.Callback {

    // variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    //private DatabaseReference mDatabase;
    private static final String TAG = "REGISTERACTIVITY";

    // UI references.
    private EditText mEmailView;
    private EditText mDisplaynameView;
    private EditText mPasswordView;
    private EditText mPasswordRepeatView;
    private View mProgressView;
    private View mRegisterFormView;
    public Button mEmailRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // set up the register form.
        mEmailView = findViewById(R.id.email_register);
        mPasswordView = findViewById(R.id.password_register);
        mPasswordRepeatView = findViewById(R.id.password_repeat);
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
        mEmailRegisterButton = findViewById(R.id.email_register_button);
        mDisplaynameView = findViewById(R.id.displayname_register);

        // set listeners
        mEmailRegisterButton.setOnClickListener(new registerOnClick());

        // setup firebase
        mAuth = FirebaseAuth.getInstance();
    }

    // register user onClick
    public class registerOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            System.out.println(TAG + "onClick called");
            attemptRegister();
        }
    }

    // check if user gave valid email
    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    // check if user gave valid password
    public static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    // check if user did not make typos in password
    public static boolean isPasswordSame(String password, String repeat) {
        return password.equals(repeat);
    }


    // register user with firebase authentication
    private void attemptRegister() {

        // reset errors
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // store values at the time of the register attempt
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordRepeat = mPasswordRepeatView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // check for a valid password
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if(TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(passwordRepeat) || !isPasswordSame(password, passwordRepeat)) {
            mPasswordRepeatView.setError(getString(R.string.error_invalid_repeat));
            focusView = mPasswordRepeatView;
            cancel = true;
        }


        // check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // there was an error; don't attempt login and focus the first
            // form field with an error
            focusView.requestFocus();
            System.out.println("before return");
            return;
        } else {
            // kick off a background task to perform the user login attempt.
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // if sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    mPasswordView.setError(getString(R.string.error_invalid_password));
                                    mPasswordView.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    mEmailView.setError(getString(R.string.error_invalid_email));
                                    mEmailView.requestFocus();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    mEmailView.setError(getString(R.string.error_user_exists));
                                    mEmailView.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.auth_succes,
                                        Toast.LENGTH_SHORT).show();


                                // first download of recipes
                                RecipesHelper recipesHelper = new RecipesHelper(RegisterActivity.this);
                                recipesHelper.getRecipes(RegisterActivity.this);

                                // create user with UID, email and displayname
                                String displayname = mDisplaynameView.getText().toString();
                                // if user didn't give displayname use email
                                if(displayname.equals("")) {
                                    displayname = email;
                                }
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                String mUid = null;
                                if (firebaseUser != null) {
                                    mUid = firebaseUser.getUid();
                                }

                                //mDatabase = FirebaseDatabase.getInstance().getReference();

                                // add object to the database
                                UserLab userLab = UserLab.getInstance();
                                userLab.newUser(mUid, email, displayname);

                                /*User user = new User(mUid, email, displayname);
                                mDatabase.child("users").child(mUid).setValue(user);*/

                                // go back to the signIn Activity
                                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            }

                        }
                    });
        }
    }

    // callbacks from RecipesHelper
    @Override
    public void gotRecipes(ArrayList<Recipe> recipesArrayList, Context mContext) {

        // add also recipes to user class
        RecipeLab recipeLab = RecipeLab.getInstance();
        recipeLab.safeToDatabase(recipesArrayList);
        recipeLab.fillRecipeArray();
    }

    @Override
    public void gotError(String message) {
    }


}
