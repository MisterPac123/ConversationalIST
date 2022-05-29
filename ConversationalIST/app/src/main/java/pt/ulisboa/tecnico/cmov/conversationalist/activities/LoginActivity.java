package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;

public class LoginActivity extends AppCompatActivity {

    UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        configLoginButton();
        configSignupButton();


    }

    public void configLoginButton(){
        Button newChat_Button = (Button) findViewById(R.id.loginButton);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyLogin()){
                    startMainActivity();
                }
            }
        });
    }

    public boolean verifyLogin(){
        EditText usernameEditTxt = findViewById(R.id.usernameEditText);
        EditText passwordEditTxt = findViewById(R.id.passwordEditText);

        String username = usernameEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();

        if(username.matches("")) {
            usernameEditTxt.setError("Username Required");
            return false;
        }
        if(password.matches("")) {
            passwordEditTxt.setError("Password required");
            return false;
        }
        //check if user in database
        //get user from database
        //(for now just create user)
        user = new UserAccount(username, "random@email.com", password, username);
        return true;
    }

    private void startMainActivity() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("user", user);
        startActivity(switchActivityIntent);

    }

    public void configSignupButton() {

        TextView signClickableTxt = (TextView) this.findViewById(R.id.createAccountTxt);
        Intent switchActivityIntent = new Intent(this, SignupActivity.class);

        signClickableTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(switchActivityIntent);
            }
        });

    }
}