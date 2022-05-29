package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.results.LoginResult;

import pt.ulisboa.tecnico.cmov.conversationalist.results.SignupResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/signup")
    Call<SignupResult> executeSignup(@Body HashMap<String, String> map);
}