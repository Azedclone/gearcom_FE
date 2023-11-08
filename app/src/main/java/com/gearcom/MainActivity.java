package com.gearcom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gearcom.activity.auth.LoginActivity;
import com.gearcom.activity.auth.RegisterActivity;
import com.gearcom.api.Api;
import com.gearcom.api.AuthApi;
import com.gearcom.ui.chat.ChatActivity;
import com.gearcom.databinding.ActivityMainBinding;
import com.gearcom.model.Cart;
import com.gearcom.model.User;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    String CHANNEL_ID = "my_channel_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_id";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            // Kiểm tra quyền NOTIFICATION và yêu cầu nếu cần thiết
            if (notificationManager.getNotificationChannel(CHANNEL_ID).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent2 = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent2.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent2.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID);
                startActivity(intent2);
            }
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_cart)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SharedPreferences sharedPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        String jwt = sharedPreferences.getString("jwt", "");

        if (!jwt.isEmpty()) {
            AuthApi.authApi.getUserProfile("Bearer " + jwt).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.body() != null) {
                        View headerView = navigationView.getHeaderView(0);
                        TextView nameTextView = headerView.findViewById(R.id.name);
                        nameTextView.setText(response.body().getName());
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
            Api.api.getCartByUserId("Bearer " + jwt).enqueue(new Callback<List<Cart>>() {
                @Override
                public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                    if (response.body() != null) {
                        List<Cart> carts = response.body();
                        if(carts.size()!=0) {
//                            Intent cartIntent = new Intent(this, MyCartsFragment.class);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, cartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            // Tạo thông báo
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_android)
                                    .setContentTitle("Giỏ hàng!")
                                    .setContentText("Giỏ hàng của bạn có sản phẩm");
                            // Gửi thông báo
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.notify(1, builder.build());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Cart>> call, Throwable t) {
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int choice = item.getItemId();

        if (choice == R.id.login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        if (choice == R.id.register) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
        if (choice == R.id.location) {
//            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
//            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}