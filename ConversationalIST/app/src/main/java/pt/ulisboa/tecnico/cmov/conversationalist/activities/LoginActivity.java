package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.results.LoginResult;
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



    }
    private void configLoginButton(){
        Button newChat_Button = (Button) findViewById(R.id.loginButton);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
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

        View view = getLayoutInflater().inflate(R.layout.activity_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view).show();

        Button loginBtn = view.findViewById(R.id.loginButton);
        final EditText usernameEdit = view.findViewById(R.id.usernameEditText);
        final EditText passwordEdit = view.findViewById(R.id.passwordEditText);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> map = new HashMap<>();

                map.put("username", usernameEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<LoginResult> call = retrofitInterface2.executeLogin(map);

                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

                        if (response.code() == 200){

                            LoginResult result = response.body();

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            builder1.setTitle(result.getUsername());
                            builder1.setMessage(result.getEmail());

                            builder1.show();

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
        });

    }

}