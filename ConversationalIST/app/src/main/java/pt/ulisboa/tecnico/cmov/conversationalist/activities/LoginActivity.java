package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.LoginResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit2;
    private RetrofitInterface retrofitInterface2;
    private String BASE_URL = "http://10.0.2.2:3000";

    UserAccount user;

    boolean nightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        retrofit2 = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface2 = retrofit2.create(RetrofitInterface.class);

        configLoginButton();
        configSignupButton();

        configDarkModeButton();

    }

    public void configDarkModeButton(){

        Button switch_btn = (Button) findViewById(R.id.darkModeButton);

        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switch_btn.setText("Disable Dark Mode");
                    nightMode = true;
                } else {
                    switch_btn.setText("Enable Dark Mode");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightMode = false;
                }
            }
        });

    }

    private void configLoginButton(){

        Button loginBtn = findViewById(R.id.loginButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verifyLogin()){
                    handleLoginDialog();
                }
            }
        });
    }

    private boolean verifyLogin(){
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
        //user = new UserAccount(username, "random@email.com", password, "random name");
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

    private void handleLoginDialog() {

        final EditText usernameEdit = findViewById(R.id.usernameEditText);
        final EditText passwordEdit = findViewById(R.id.passwordEditText);

        HashMap<String, String> map = new HashMap<>();

        map.put("username", usernameEdit.getText().toString());
        map.put("password", passwordEdit.getText().toString());

        Call<LoginResult> call = retrofitInterface2.executeLogin(map);

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

                if (response.code() == 200){

                    Toast.makeText(LoginActivity.this, "LoginSuccessfully", Toast.LENGTH_LONG).show();
                    String username = response.body().getUsername();
                    String name = response.body().getName();
                    String email = response.body().getEmail();

                    user = new UserAccount(username, email, name);

                    startMainActivity();

                } else if(response.code() == 404){
                    Toast.makeText(LoginActivity.this, "WrongCredentials", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}