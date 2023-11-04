package com.gearcom.api;

import com.gearcom.api.model.LoginBody;
import com.gearcom.api.model.LoginResponse;
import com.gearcom.api.model.RegistrationBody;
import com.gearcom.model.Category;
import com.gearcom.model.Product;
import com.gearcom.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    Api api = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(com.gearcom.api.Api.class);

    @GET("product")
    Call<List<Product>> getProducts();
    @GET("product/{id}")
    Call<List<Product>> getProductById();

    @GET("category")
    Call<List<Category>> getCategories();
}
