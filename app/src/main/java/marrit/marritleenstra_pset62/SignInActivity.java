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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    // fireBase references
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseAnalytics mFirebaseAnalytics;
    FirebaseUser mUser;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    public TextView mGoToRegisterView;
    public Button mEmailSignInButton;
    public TextView mForgotPassword;

    public static final String TAG = "SIGNIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mGoToRegisterView = findViewById(R.id.go_to_register);
        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mForgotPassword = findViewById(R.id.TV_forgot_password);

        // set up fireBase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // set listeners
        mEmailSignInButton.setOnClickListener(new attemptLoginOnClick());
        mGoToRegisterView.setOnClickListener(new goToRegisterOnClick());
        mForgotPassword.setOnClickListener(new goToForgotPasswordOnClick());
    }

    // login the user using firebase authentication
    public class attemptLoginOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            attemptLogin();
            mPasswordView.getText().clear();
        }
    }

    // go to the registration activity
    public class goToRegisterOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            SignInActivity.this.startActivity(intent);
        }
    }

    // go to the forget password activity
    public class goToForgotPasswordOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            SignInActivity.this.startActivity(intent);
        }
    }

    // the actual login using firebase authentication
    public void attemptLogin() {

        // reset errors
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // store values at the time of the login attempt
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // check if password and email are filled in
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
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
            // try to sign in the user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();
                                Log.w(TAG, "signInWithEmail:failed", task.getException());

                            } else {
                                Toast.makeText(SignInActivity.this, "signed in",
                                        Toast.LENGTH_SHORT).show();

                                // initialise userdata
                                if (mUser != null){
                                    System.out.println(TAG + ": firebaseUser != null");

                                    // start RecipeLab
                                    RecipeLab recipeLab = RecipeLab.getInstance();
                                    recipeLab.fillRecipeArray();

                                    // start UserLab
                                    UserLab userLab = UserLab.getInstance();
                                    userLab.fillUserData();

                                    // start CommunityLab
                                    CommunityLab communityLab = CommunityLab.getInstance();
                                    communityLab.fillCommunityData();
                                }

                                // go to mainActivity
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                SignInActivity.this.startActivity(intent);
                            }

                            // ...
                        }
                    });

        }
    }

    // disable going back
    @Override
    public void onBackPressed(){
        // do nothing
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
