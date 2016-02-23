package mdtu.com.secretoitalia;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by koctyabondar on 9/14/15.
 */
public class SignIn extends Activity {
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_in);

        // Check if there is a currently logged in user
        // and it's linked to a Facebook account.

        Button signIn = (Button) findViewById(R.id.joinWithEmail);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView email = (TextView) findViewById(R.id.email);
                if (!isValidEmail(email.getText().toString())) {
                    Toast.makeText(SignIn.this, "Input correct email", Toast.LENGTH_SHORT).show();
                    return;
                }

                final TextView password = (TextView) findViewById(R.id.pasword);
                if (password.getText().length() < 6) {
                    Toast.makeText(SignIn.this, "Input password (minimum 6 characters)", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseUser newUser = new ParseUser();
                newUser.setEmail(email.getText().toString());
                newUser.setPassword(password.getText().toString());
                newUser.setUsername(email.getText().toString());


                newUser.logInInBackground(newUser.getUsername(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            finish();
                        } else if (e.getCode() == 101) {
                            Toast.makeText(SignIn.this, "Invalid login/password, please correct it", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });

    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(SignIn.this, "", "Logging in...", true);

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
