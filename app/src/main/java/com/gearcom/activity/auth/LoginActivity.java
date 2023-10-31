package com.gearcom.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gearcom.R;
import com.gearcom.api.AuthApi;
import com.gearcom.api.model.LoginBody;
import com.gearcom.api.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;

    private Button login, register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(view -> {
            String username = String.valueOf(usernameInput.getText());
            String password = String.valueOf(passwordInput.getText());

            LoginBody body = new LoginBody(username, password);

            AuthApi.authApi.login(body).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.code() == 200) {
                        String jwt = response.body().getJwt();
                        SharedPreferences sharedPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
                        sharedPreferences.edit().putString("jwt", jwt).apply();
                        Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "INTERNAL SERVER ERROR!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}