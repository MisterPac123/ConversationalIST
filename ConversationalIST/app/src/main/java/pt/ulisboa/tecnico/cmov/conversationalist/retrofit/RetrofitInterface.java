package pt.ulisboa.tecnico.cmov.conversationalist.retrofit;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.LoginResult;

import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SearchChatRoomResults;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.UserChatRoomsResults;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<Void> executeSignup(@Body HashMap<String, String> map);

    @POST("/NewChatRoom")
    Call<Void> executeCreateNewChat(@Body HashMap<String, String> map);

    @POST("/searchChatRoom")
    Call<SearchChatRoomResults> executeSearchChatRoom(@Body HashMap<String, String> map);

    @POST("/getUserChatRooms")
    Call<ArrayList<SearchChatRoomResults>> executeGetUserChatRoom(@Body HashMap<String, String> map);
}