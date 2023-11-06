package com.gearcom.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gearcom.R;
import com.gearcom.api.AuthApi;
import com.gearcom.api.model.RegistrationBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, nameInput, phoneInput, addressInput;
    private Button register, login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        nameInput = findViewById(R.id.name);
        phoneInput = findViewById(R.id.phone);
        addressInput = findViewById(R.id.address);

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        register.setOnClickListener(view -> {
            String username = String.valueOf(usernameInput.getText());
            String password = String.valueOf(passwordInput.getText());
            String name = String.valueOf(nameInput.getText());
            String phone = String.valueOf(phoneInput.getText());
            String address = String.valueOf(addressInput.getText());

            RegistrationBody body = new RegistrationBody(username, password, name, phone, address);

            AuthApi.authApi.register(body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) {
                        Toast.makeText(RegisterActivity.this, "Register OK", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 409) {
                        Toast.makeText(RegisterActivity.this, "Account already existed!", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    System.out.println(t);
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });

        login.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }


}