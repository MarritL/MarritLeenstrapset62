package marrit.marritleenstra_pset62;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    // variables
    private static final String TAG = "SPLASHACTIVITY";
    private static final int DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(TAG + ": onCreate");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // go to mainActivity after DURATION
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, DURATION);

    }
}
