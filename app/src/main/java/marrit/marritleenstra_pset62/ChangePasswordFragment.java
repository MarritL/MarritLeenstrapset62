package marrit.marritleenstra_pset62;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by Marrit 5-52018
 * Fragment where user can change his passoword.
 */
public class ChangePasswordFragment extends Fragment {

    // variables
    private String TAG = "CHANGEPASSWORDFRAGMENT";

    // UI references
    EditText mNewPassword;
    EditText mRepeatPassword;
    Button mChangePassword;

    public ChangePasswordFragment() {
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
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // initiate UI references
        mNewPassword = view.findViewById(R.id.ET_password);
        mRepeatPassword = view.findViewById(R.id.ET_repeat_password);
        mChangePassword = view.findViewById(R.id.BUTTON_change_password);

        // set listeners
        mChangePassword.setOnClickListener(new changePasswordOnClick());

        return view;
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
                                    Toast.makeText(getContext(), R.string.use_new_password,Toast.LENGTH_SHORT).show();

                                    // return to settings
                                    MainActivity.navigation.setSelectedItemId(R.id.navigation_settings);
                                }
                            }
                        });
            }

        }
    }
}
