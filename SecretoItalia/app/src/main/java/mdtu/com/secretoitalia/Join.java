package mdtu.com.secretoitalia;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by koctyabondar on 9/21/15.
 */
public class Join extends Activity {
    private Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);
        Button signUp = (Button) findViewById(R.id.joinWithEmail);
        LinearLayout lar = (LinearLayout)findViewById(R.id.joingInclude);
        LinearLayout toggle = (LinearLayout) lar.findViewById(R.id.show_menu);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toggle.getLayoutParams();
        params.setMargins(-10,-10,-10,-10);
        toggle.setLayoutParams(params);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView img = (ImageView) lar.findViewById(R.id.menuIcon);
        img.setImageResource(R.mipmap.side_arrow_icon);
        img.setAlpha(100);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Join.this,SignUp.class );
                startActivity(intent);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(Join.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {

                } else if (user.isNew()) {
                    String a = user.getEmail();
                    a += " '";
                    getInfo();

                } else {
                    String a = user.getEmail();
                    a += " '";
                    getInfo();

                }
            }
        });


    }
    private void getInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        String email = "";
                        String first_name = "";
                        String last_name = "";
                        String gender = "";
                        String id = "";
                        String picture = "";
                        try {
                            JSONObject data = object.getJSONObject("picture");
                            JSONObject array = data.getJSONObject("data");
                            picture = array.getString("url");
                            email = object.getString("email");
                            last_name = object.getString("last_name");
                            gender = object.getString("gender");
                            id = object.getString("id");
                            first_name = object.getString("first_name");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        currentUser.put("email",email);
                        currentUser.put("last_name",last_name);
                        currentUser.put("gender",gender);
                        currentUser.put("fbid",id);
                        currentUser.put("first_name",first_name);
                        currentUser.put("picture",picture);
                        currentUser.saveInBackground();
                        finish();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,first_name,last_name,gender,id,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
