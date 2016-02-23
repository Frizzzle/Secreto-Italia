package mdtu.com.secretoitalia;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

/**
 * Created by koctyabondar on 9/21/15.
 */
public class SignUp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        MultiStateToggleButton button = (MultiStateToggleButton) this.findViewById(R.id.mstb);
        button.setValue(0);

        LinearLayout lar = (LinearLayout)findViewById(R.id.signUpInclude);
        LinearLayout toggle = (LinearLayout) lar.findViewById(R.id.show_menu);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toggle.getLayoutParams();
        params.setMargins(-10, -10, -10, -10);
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
        Button register = (Button) findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView email = (TextView) findViewById(R.id.emailReg);
                TextView firstName = (TextView) findViewById(R.id.firstReg);
                TextView lastName = (TextView) findViewById(R.id.lastReg);
                TextView password = (TextView) findViewById(R.id.passReg);
                MultiStateToggleButton gender = (MultiStateToggleButton) findViewById(R.id.mstb);

                if (email.getText().toString().equals("") || email.getText().length() < 3) {
                    Toast.makeText(SignUp.this, "Please input email  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidEmail(email.getText().toString())) {
                    Toast.makeText(SignUp.this, "Please correct email   ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (firstName.getText().toString().equals("") || firstName.getText().length() < 3) {
                    Toast.makeText(SignUp.this, "Please input first name  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lastName.getText().toString().equals("") || lastName.getText().length() < 3) {
                    Toast.makeText(SignUp.this, "Please input last name  ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().equals("") || password.getText().length() < 6) {
                    Toast.makeText(SignUp.this, "Please input password   ", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser newUser = new ParseUser();
                newUser.setUsername(email.getText().toString());
                newUser.setPassword(password.getText().toString());
                newUser.setEmail(email.getText().toString());
                newUser.put("first_name", firstName.getText().toString());
                newUser.put("last_name", lastName.getText().toString());
                 boolean[] genderValue =   gender.getStates();

                String genderStr = genderValue[0] == true ? "female" : "male";
                newUser.put("gender", genderStr);
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(SignUp.this);
                            alert.setMessage("Registration successfull");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                            alert.show();
                        } else {
                            android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(SignUp.this);
                            alert.setMessage("Registration unsuccessfull");
                            alert.setPositiveButton("OK", null);
                        }
                    }
                });
            }
        });
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
