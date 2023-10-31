package com.gearcom.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.gearcom.R;
import com.gearcom.api.AuthApi;
import com.gearcom.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView username, name, phone, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);

        SharedPreferences sharedPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        String jwt = sharedPreferences.getString("jwt", "");

        if (!jwt.isEmpty()){
        AuthApi.authApi.getUserProfile("Bearer " + jwt).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                username.setText(response.body().getUsername());
                name.setText(response.body().getName());
                phone.setText(response.body().getPhone());
                address.setText(response.body().getAddress());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        }
    }
}