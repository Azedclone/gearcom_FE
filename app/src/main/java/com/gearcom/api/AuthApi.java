package com.gearcom.api;

import com.gearcom.api.model.LoginBody;
import com.gearcom.api.model.LoginResponse;
import com.gearcom.api.model.RegistrationBody;
import com.gearcom.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApi {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    AuthApi authApi = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/v1/auth/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(com.gearcom.api.AuthApi.class);

    @POST("register")
    Call<Response<HTTP>> register(@Body RegistrationBody body);

    @POST("login")
    Call<LoginResponse> login(@Body LoginBody body);

    @GET("profile")
    Call<User> getUserProfile(@Header("Authorization") String authHeader);
}
