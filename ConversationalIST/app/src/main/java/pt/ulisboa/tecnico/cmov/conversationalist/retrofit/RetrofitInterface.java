package pt.ulisboa.tecnico.cmov.conversationalist.retrofit;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ArrayMsgsFromChatResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.LoginResult;

import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ReceiveMsgFromChatResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SearchChatRoomResults;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SendMsgResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.UserChatRoomsResults;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @POST("/users/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/users/signup")
    Call<Void> executeSignup(@Body HashMap<String, String> map);

    @POST("/chatRooms/NewChatRoom")
    Call<Void> executeCreateNewChat(@Body HashMap<String, String> map);

    @POST("/chatRooms/NewGeoChatRoom")
    Call<Void> executeCreateNewGeoChat(@Body HashMap<String, String> map);

    @POST("/chatRooms/searchChatRoom")
    Call<ArrayList<SearchChatRoomResults>> executeSearchChatRoom(@Body HashMap<String, String> map);

    @POST("/chatRooms/getUserChatRooms")
    Call<ArrayList<SearchChatRoomResults>> executeGetUserChatRoom(@Body HashMap<String, String> map);

    @POST("/chatRooms/addUserToChatRoom")
    Call<SearchChatRoomResults> executeAddUserToRoom(@Body HashMap<String, String> map);

    @POST("/chatRooms/sendMsgToChatRoom")
    Call<SendMsgResult> executeSendMsgToChatRoom(@Body HashMap<String, String> map);

    @POST("/chatRooms/getMsgFromChatRoom")
    Call<ArrayMsgsFromChatResult> executeReceiveMsgFromChatRoom(@Body HashMap<String, String> map);

    @GET("/chatRooms/{chatName}")
    Call<ArrayMsgsFromChatResult> executeGetnewMsgs(@Path("chatName") String chatName);

}