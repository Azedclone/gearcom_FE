package com.gearcom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gearcom.model.Product;
import com.squareup.picasso.Picasso;

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

//    public void addToCart(View view) {
//        Intent intent = getIntent();
//        Product product = (Product) intent.getSerializableExtra("product_selected");
//
//        CartManager cartManager = CartManager.getInstance();
//        cartManager.addProduct(product);
//
//        CurrentUser currentUser = CurrentUser.getInstance();
//
//        LapHelper dbHelper = new LapHelper(this);
//        dbHelper.addToCart(currentUser.getUserId(),product.getProductId(), product.getProductName(), 1,10);
//
//        // Tạo intent để mở trang giỏ hàng
//        Intent cartIntent = new Intent(this, CartActivity.class);
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
//
//    }
}