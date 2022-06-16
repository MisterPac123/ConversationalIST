package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.conversationalist.PermissionUtils;
import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateChatRoomActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    private Spinner newChatTypesSpinner;
    private String chatroom_type;

    private UserAccount user;
    private boolean permissionDenied = false;
    private int PLACE_PICKER_REQUEST = 1;
    private final static int MY_REQUEST_CODE = 1;

    Geocoder geocoder;

    private String[] address;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);

        Bundle intent = getIntent().getExtras();
        if(intent != null) {
            //error
        }
        user = (UserAccount) intent.getSerializable("user");

        initBackendConnection();
        configureSpinner();
        configureCreateChatRoomButton();
        configurePickLocationButton();
    }

    private void configurePickLocationButton() {
        Button pickerBtn = findViewById(R.id.pickLocation);

        pickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapsActivity();
            }
        });
    }

    private void openMapsActivity() {
        Intent switchActivityIntent = new Intent(this, MapsActivity.class);
        someActivityResultLauncher.launch(switchActivityIntent);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        //data.getExtras();
                        address =  data.getStringArrayExtra("address");
                        Log.i("address", address[0] + " " + address[1]);
                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        TextView coordinatesTV = findViewById(R.id.coordinatesTextView);
                        coordinatesTV.setText(address[0] + " " + address[1]);
                        try {
                            geocoder.getFromLocation(Double.parseDouble(address[0]), Double.parseDouble(address[1]), 1).get(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


    private void configureSpinner() {
        newChatTypesSpinner = findViewById(R.id.chatTypesSpinner);
        Button button = findViewById(R.id.pickLocation);
        EditText edittxt = findViewById(R.id.radiusEditText);
        TextView txt = findViewById(R.id.coordinatesTextView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chatroom_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newChatTypesSpinner.setAdapter(adapter);

        newChatTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chatroom_type = adapterView.getItemAtPosition(i).toString();
                if (chatroom_type.matches("Geo-fenced")){
                    button.setVisibility(View.VISIBLE);
                    edittxt.setVisibility(View.VISIBLE);
                    txt.setVisibility(View.VISIBLE);
                }
                else{
                    button.setVisibility(View.INVISIBLE);
                    edittxt.setVisibility(View.INVISIBLE);
                    txt.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void configureCreateChatRoomButton() {
        Button newChatRoom = findViewById(R.id.createNewChatroom);
        EditText input_name = findViewById(R.id.editTxtNewChatroomTitle);
        EditText input_description = findViewById(R.id.editTxtNewChatroomTitle);
        newChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = input_name.getText().toString();
                String description = input_description.getText().toString();
                if(name.matches("")) {
                    input_name.setError("Chat name is required");
                    return;
                }
                else if(newChatTypesSpinner.getSelectedItem().toString().equalsIgnoreCase("Chat type…")){
                    //Show error msg
                    return;
                }

                else createNewChat(name, description, chatroom_type);
            }
        });
    }

    public void initBackendConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
    

    private void createNewChat(String name, String description , String chatroom_type) {
        HashMap<String, String> map = new HashMap<>();

        map.put("name", name);
        map.put("type", chatroom_type);
        map.put("description", description);
        map.put("admin", user.getUsername());

        if(chatroom_type.matches("Public"))
            createNewPublicChatRoom(map);
        else if(chatroom_type.matches("Geo-fenced"))
            createNewGeoChatRoom(map);
        else
            Log.i("createchat","no type found:" + chatroom_type);
    }

    public void createNewPublicChatRoom(HashMap<String, String> map) {
        Call<Void> call = retrofitInterface.executeCreateNewChat(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "ChatRoom added successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 400) {
                    Toast.makeText(getApplicationContext(), "Name already exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void createNewGeoChatRoom(HashMap<String, String> map) {

        map.put("coordinates", address[0] + " " + address[1]);
        Call<Void> call = retrofitInterface.executeCreateNewGeoChat(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "ChatRoom added successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 400) {
                    Toast.makeText(getApplicationContext(), "Name already exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}