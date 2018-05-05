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

public class ChangePasswordActivity extends AppCompatActivity {

    // variables
    private String TAG = "CHANGEPASSWORDACTIVITY";

    // UI references
    EditText mNewPassword;
    EditText mRepeatPassword;
    Button mChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // initiate UI references
        mNewPassword = findViewById(R.id.ET_password);
        mRepeatPassword = findViewById(R.id.ET_repeat_password);
        mChangePassword = findViewById(R.id.BUTTON_change_password);

        // set listeners
        mChangePassword.setOnClickListener(new changePasswordOnClick());
    }

    private class changePasswordOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String newPassword = mNewPassword.getText().toString();
            String newRepeat = mRepeatPassword.getText().toString();

            View focusView = null;


            // check if passwords are given and the same
            if (TextUtils.isEmpty(newPassword)) {
                mNewPassword.setError(getString(R.string.error_field_required));
                focusView = mNewPassword;
                focusView.requestFocus();
            } else if(!RegisterActivity.isPasswordValid(newPassword)) {
                mNewPassword.setError(getString(R.string.error_incorrect_password));
                focusView = mNewPassword;
                focusView.requestFocus();
            } else if (TextUtils.isEmpty(newRepeat) || !RegisterActivity.isPasswordSame(newPassword, newRepeat)){
                mRepeatPassword.setError(getString(R.string.error_invalid_repeat));
                focusView = mRepeatPassword;
                focusView.requestFocus();
            } else {
                // block of code from firebase guide on user management
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, ": User password updated.");

                                    // let the user know that he has to login with the new email adress
                                    Toast.makeText(ChangePasswordActivity.this, R.string.use_new_password,Toast.LENGTH_SHORT).show();

                                    // return to settings
                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                    ChangePasswordActivity.this.startActivity(intent);
                                }
                            }
                        });
            }

        }
    }
}
