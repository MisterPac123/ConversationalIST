package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.UserAccount;

public class SignupActivity extends AppCompatActivity {
    UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        configSignButton();


    }

    public void configSignButton(){
        Button newChat_Button = (Button) findViewById(R.id.signButton);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifySign()){
                    startSignupActivity();
                }
            }
        });
    }

    public boolean verifySign(){

        EditText emailEditTxt = findViewById(R.id.signEmailEditText);
        EditText usernameEditTxt = findViewById(R.id.signUsernameEditText);
        EditText passwordEditTxt = findViewById(R.id.signPasswordEditText);
        EditText nameEditTxt = findViewById(R.id.signNameEditText);

        String email = emailEditTxt.getText().toString();
        String username = usernameEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();
        String name = nameEditTxt.getText().toString();

        if(email.matches("")) {
            emailEditTxt.setError("Email Required");
            return false;
        }

        if(name.matches("")) {
            nameEditTxt.setError("Name Required");
            return false;
        }
        if(password.matches("")) {
            passwordEditTxt.setError("Password required");
            return false;
        }
        if(username.matches("")) {
            usernameEditTxt.setError("Username Required");
            return false;
        }
        //check if username unique
        //check if email already exists in db
        user = new UserAccount(username, email, password, name);
        return true;
    }

    private void startSignupActivity() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("user", user);
        startActivity(switchActivityIntent);
    }
}