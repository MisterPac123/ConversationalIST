package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.conversationalist.R;

public class LoginActivity extends AppCompatActivity {

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
                    switchActivitiesWithData();
                    //get user data

                }
            }
        });
    }

    public boolean verifyLogin(){
        EditText usernameEditTxt = findViewById(R.id.usernameEditText);
        EditText passwordEditTxt = findViewById(R.id.passwordEditText);

        String name = usernameEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();

        if(name.matches("")) {
            usernameEditTxt.setError("Username Required");
            return false;
        }
        if(password.matches("")) {
            passwordEditTxt.setError("Password required");
            return false;
        }
        //check if user in database
        return true;
    }

    private void switchActivitiesWithData() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("message", "From: " + LoginActivity.class.getSimpleName());
        startActivity(switchActivityIntent);
    }

    public void configSignupButton() {

        TextView signClickableTxt = (TextView) this.findViewById(R.id.createAccountTxt);
        Intent switchActivityIntent = new Intent(this, SignupActivity.class);


        signClickableTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivityIntent.putExtra("message", "From: " + LoginActivity.class.getSimpleName());
                startActivity(switchActivityIntent);
            }

        });

    }
}