package com.gearcom.ui.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gearcom.R;
import com.gearcom.activity.auth.LoginActivity;
import com.gearcom.activity.auth.RegisterActivity;
import com.gearcom.adapters.MyCartAdapter;
import com.gearcom.api.Api;
import com.gearcom.api.model.CartBody;
import com.gearcom.model.Cart;
import com.gearcom.model.Product;
import com.gearcom.ui.carts.MyCartsFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

public class ProductDetailActivity extends AppCompatActivity {

    String CHANNEL_ID = "my_channel_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();
        Product product = (Product) intent.getSerializableExtra("product_selected");
        TextView tvName = findViewById(R.id.tvProductName);
        TextView tvScreen = findViewById(R.id.tvPrice);
        ImageView imageView = findViewById(R.id.imageView);
        tvName.setText(product.getName());
        tvScreen.setText(product.getPrice().toString());
        Picasso.get().load(product.getImageUrl()).into(imageView);
        // Định nghĩa CHANNEL_ID


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String CHANNEL_ID = "my_channel_id";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription("Channel description");
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//            // Kiểm tra quyền NOTIFICATION và yêu cầu nếu cần thiết
//            if (notificationManager.getNotificationChannel(CHANNEL_ID).getImportance() == NotificationManager.IMPORTANCE_NONE) {
//                Intent intent2 = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
//                intent2.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                intent2.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID);
//                startActivity(intent2);
//            }
//        }
    }

    public void goBack(View view) {
        finish();
    }

    public void addToCart(View view) {
        Intent intent = getIntent();
        Product product = (Product) intent.getSerializableExtra("product_selected");

        SharedPreferences sharedPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        String jwt = sharedPreferences.getString("jwt", "");
        if (!jwt.isEmpty()) {
            CartBody cartBody= new CartBody();
            cartBody.setProduct(product);
            cartBody.setQuantity(1);
            Api.api.addToCart("Bearer " + jwt,cartBody).enqueue(new Callback<Response<HTTP>>() {
                @Override
                public void onResponse(Call<Response<HTTP>> call, Response<Response<HTTP>> response) {
                    if (response.code() == 200) {
                        Toast.makeText(ProductDetailActivity.this, "Add success", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 409) {
                        Toast.makeText(ProductDetailActivity.this, "Add fail!", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<Response<HTTP>> call, Throwable t) {
                    System.out.println(t);
                    Toast.makeText(ProductDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
        }


//
//        // Tạo intent để mở trang giỏ hàng
//        Intent cartIntent = new Intent(this, MyCartsFragment.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, cartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Tạo thông báo
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_android)
//                .setContentTitle("Bạn đã được thông báo!")
//                .setContentText("Đã thêm sản phẩm vào giỏ hàng.")
//                .setContentIntent(pendingIntent) // Thiết lập PendingIntent
//                .setAutoCancel(true); // Tự động huỷ thông báo khi người dùng ấn vào
//        // Gửi thông báo
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.notify(1, builder.build());

    }
}