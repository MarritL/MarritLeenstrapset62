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

public class ForgotPasswordActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    public Button mSendEmailButton;

    // variables
    private FirebaseAuth mAuth;
    private static final String TAG = "FORGOTPASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // set up the form
        mEmailView = findViewById(R.id.email_resetpassword);
        mSendEmailButton = findViewById(R.id.send_password_reset_email_button);

        // set up firebase
        mAuth = FirebaseAuth.getInstance();

        // set up listeners
        mSendEmailButton.setOnClickListener(new sendEmailOnClick());
    }

    // register button click
    public class sendEmailOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            attemptSendEmail();
            mEmailView.getText().clear();
        }
    }

    // check if the form is valid
    public void attemptSendEmail() {

        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!RegisterActivity.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // proceed to send password reset email
            sendEmail(email);
        }
    }

    // send the actual password reset email
    private void sendEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(ForgotPasswordActivity.this, R.string.send_email,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // go back to sign in activity
        Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
        ForgotPasswordActivity.this.startActivity(intent);
    }

}
