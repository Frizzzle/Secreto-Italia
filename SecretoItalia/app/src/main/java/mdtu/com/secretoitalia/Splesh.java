package mdtu.com.secretoitalia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import io.fabric.sdk.android.Fabric;
import mdtu.com.secretoitalia.Wrappers.BagWrapper;

/**
 * Created by koctyabondar on 9/15/15.
 */
public class Splesh extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        ParseFacebookUtils.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splesh.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 1000);
    }
}
