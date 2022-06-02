package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.results.LoginResult;
import pt.ulisboa.tecnico.cmov.conversationalist.results.SignupResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        configSignButton();


    }

    private void configSignButton(){
        Button newChat_Button = findViewById(R.id.signButton);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifySign()){
                    handleSignupDialog();
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
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        switchActivityIntent.putExtra("user", user);
        startActivity(switchActivityIntent);
    }

    private void handleSignupDialog() {

        View view = getLayoutInflater().inflate(R.layout.activity_signup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view).show();

        Button signupBtn = view.findViewById(R.id.signButton);
        final EditText usernameEdit = view.findViewById(R.id.signUsernameEditText);
        final EditText passwordEdit = view.findViewById(R.id.signPasswordEditText);
        final EditText nameEdit = view.findViewById(R.id.signNameEditText);
        final EditText emailEdit = view.findViewById(R.id.signEmailEditText);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> map = new HashMap<>();

                map.put("email", emailEdit.getText().toString());
                map.put("name", nameEdit.getText().toString());
                map.put("username", usernameEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<Void> call = retrofitInterface.executeSignup(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.code() == 200) {

                            Toast.makeText(SignupActivity.this, "Signup Successfully", Toast.LENGTH_LONG).show();

                            startSignupActivity();

                        } else if (response.code() == 400) {

                            Toast.makeText(SignupActivity.this, "Already Registered", Toast.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SignupActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}