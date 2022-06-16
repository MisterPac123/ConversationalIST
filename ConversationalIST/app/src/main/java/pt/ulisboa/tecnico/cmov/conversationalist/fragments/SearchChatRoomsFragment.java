package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatRoomListAdp;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SearchChatRoomResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchChatRoomsFragment extends Fragment implements ChatRoomListAdp.ItemClickListener{

    private View view;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    private ChatRoomListAdp chatListAdp;
    private ArrayList<ChatRoom> searchChatsResult = new ArrayList<>();

    private UserAccount user;

    private ArrayList<SearchChatRoomResults> searchChatRoomArrayList = new ArrayList<>();


    public SearchChatRoomsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_chat_rooms, container, false);
        // Inflate the layout for this fragment

        if(this.getArguments() != null )
            user = (UserAccount) this.getArguments().getSerializable("user");

        initBackendConnection();
        configSearchBarEditText();

        return view;
    }

    private void configSearchBarEditText() {
        EditText searchChatRoomET = view.findViewById(R.id.searchChatRoomET);
        TextView testTV = view.findViewById(R.id.testTextView);
        searchChatRoomET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                testTV.setText("typing");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                testTV.setText(editable.toString());
                getSearchChatRoomBackend(editable.toString());
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

    public void getSearchChatRoomBackend(String name) {
        HashMap<String, String> map = new HashMap<>();

        map.put("chatName", name);

        Call<ArrayList<SearchChatRoomResults>> call = retrofitInterface.executeSearchChatRoom(map);

        call.enqueue(new Callback<ArrayList<SearchChatRoomResults>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchChatRoomResults>> call, Response<ArrayList<SearchChatRoomResults>> response) {

                if (response.code() == 200){

                    searchChatsResult = new ArrayList<>();
                    searchChatRoomArrayList = response.body();
                    for ( int i = 0; i < searchChatRoomArrayList.size(); i++) {
                        SearchChatRoomResults data = searchChatRoomArrayList.get(i);
                        ChatRoom chatroom = new ChatRoom(data.getName(), data.getType(), data.getDescription());
                        Log.i("chat found", data.getName());
                        searchChatsResult.add(chatroom);

                    }
                    chatListAdp = new ChatRoomListAdp(searchChatsResult);


                    handleSearchChatRoom(searchChatsResult, chatListAdp);


                } else if(response.code() == 404){
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchChatRoomResults>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSearchChatRoom(ArrayList<ChatRoom> searchChatsResult, ChatRoomListAdp chatListAdp) {
        RecyclerView chatsListView = view.findViewById(R.id.searchResultView);


        chatsListView.setAdapter(chatListAdp);
        chatListAdp.setClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        chatsListView.setLayoutManager(linearLayoutManager);


    }

    public void addUserToChatRoom(String chatroom) {
        HashMap<String, String> map = new HashMap<>();

        map.put("chatName", chatroom);
        map.put("username", user.getUsername());

        Call<SearchChatRoomResults> call = retrofitInterface.executeAddUserToRoom(map);

        call.enqueue(new Callback<SearchChatRoomResults>() {
            @Override
            public void onResponse(Call<SearchChatRoomResults> call, Response<SearchChatRoomResults> response) {

                if (response.code() == 200) {
                    String name = response.body().getName();
                    String type = response.body().getType();
                    String description = response.body().getDescription();
                    ChatRoom chatRoom = new ChatRoom(name, type, description);

                    Log.i("add user to chatroom", chatRoom.getName());


                } else if (response.code() == 404) {
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<SearchChatRoomResults> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        ChatRoom chat = searchChatsResult.get(position);
        addUserToChatRoom(chat.getName());
    }
}