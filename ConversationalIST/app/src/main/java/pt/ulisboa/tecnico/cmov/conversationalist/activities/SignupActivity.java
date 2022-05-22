package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmov.conversationalist.R;

public class SignupActivity extends AppCompatActivity {

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
                    switchActivitiesWithData();
                    //get user data

                }
            }
        });
    }

    public boolean verifySign(){

        EditText emailEditTxt = findViewById(R.id.signEmailEditText);
        EditText usernameEditTxt = findViewById(R.id.signUsernameEditText);
        EditText passwordEditTxt = findViewById(R.id.signPasswordEditText);

        String email = emailEditTxt.getText().toString();
        String name = usernameEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();

        if(email.matches("")) {
            emailEditTxt.setError("Email Required");
            return false;
        }

        if(name.matches("")) {
            usernameEditTxt.setError("Username Required");
            return false;
        }
        if(password.matches("")) {
            passwordEditTxt.setError("Password required");
            return false;
        }
        //check if username unique
        return true;
    }

    private void switchActivitiesWithData() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        switchActivityIntent.putExtra("message", "From: " + SignupActivity.class.getSimpleName());
        startActivity(switchActivityIntent);
    }
}