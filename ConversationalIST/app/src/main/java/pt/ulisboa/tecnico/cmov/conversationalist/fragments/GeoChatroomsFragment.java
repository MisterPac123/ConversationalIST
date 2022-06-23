package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.ChatRoomActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatRoomListAdp;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ChatRoomResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoChatroomsFragment extends Fragment implements ChatRoomListAdp.ItemClickListener {

    private View view;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private static final int REQUEST_CODE = 101;

    private UserAccount user;
    private String[] latLong = new String[2];

    private ArrayList<ChatRoomResults> userChatRoomArrayList = new ArrayList<>();
    private ArrayList<ChatRoom> availableChats = new ArrayList<>();
    private ChatRoomListAdp chatListAdp;



    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";



    public GeoChatroomsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBackendConnection();

        if(this.getArguments() != null )
            user = (UserAccount) this.getArguments().getSerializable("user");


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_geo_chatrooms, container, false);

        fetchLocation();
        return view  ;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchLocation();

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //locationManager.getCurrentLocation(LocationManager.NETWORK_PROVIDER, null, this.mainExecutor, locationCallback);

        locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null, getActivity().getApplication().getMainExecutor(),
                new Consumer<Location>() {
                    @Override
                    public void accept(Location location) {
                        Log.i("location accept", location.getLatitude() + " " + location.getLongitude());
                        TextView textView = view.findViewById(R.id.locationTextView);
                        latLong[0] = String.valueOf(location.getLatitude());
                        latLong[1] = String.valueOf(location.getLongitude());

                        textView.setText(latLong[0] + " " + latLong[1]);
                        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                        getGeoChatrooms();
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

    private void getGeoChatrooms() {
        HashMap<String, String> map = new HashMap<>();

        map.put("username", user.getUsername());
        map.put("latitude", latLong[0]);
        map.put("longitude", latLong[1]);

        getUserGeoChatRooms(map);
    }

    public void getUserGeoChatRooms(HashMap<String, String> map) {
        Call<ArrayList<ChatRoomResults>> call = retrofitInterface.executeGetUserGeoChatRoom(map);

        call.enqueue(new Callback<ArrayList<ChatRoomResults>>() {
            @Override
            public void onResponse(Call<ArrayList<ChatRoomResults>> call, Response<ArrayList<ChatRoomResults>> response) {

                if (response.code() == 200){

                    userChatRoomArrayList = response.body();
                    for ( int i = 0; i < userChatRoomArrayList.size(); i++) {
                        ChatRoomResults data = userChatRoomArrayList.get(i);
                        ChatRoom chatroom = new ChatRoom(data.getName(), data.getType(), data.getDescription());
                        addChatToArray(chatroom);
                    }

                    chatListAdp = new ChatRoomListAdp(availableChats);

                    displayChatList(view, chatListAdp);

                } else if(response.code() == 404){
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ChatRoomResults>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addChatToArray(ChatRoom chatRoom) {
        if(!availableChats.contains(chatRoom))
            availableChats.add(chatRoom);
    }

    public void displayChatList(View view, ChatRoomListAdp chatListAdp ) {

        verifyChatListEmpty(view);

        RecyclerView chatsListView = view.findViewById(R.id.searchResultView);

        Log.i("chats:", String.valueOf(availableChats.size()));
        chatsListView.setAdapter(chatListAdp);
        chatListAdp.setClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        chatsListView.setLayoutManager(linearLayoutManager);
    }

    public void verifyChatListEmpty(View view) {
        if(availableChats.size()>0){
            TextView emptyListmsg = view.findViewById(R.id.emptyChatListMsg2);
            emptyListmsg.setVisibility(View.INVISIBLE);
        }
    }

    public void openChatRoom(ChatRoom chat) {
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("chat", chat);
        startActivity(intent);
    }

    @Override
    public void onClick(View view, int position) {
        ChatRoom chat = availableChats.get(position);
        openChatRoom(chat);
    }
}