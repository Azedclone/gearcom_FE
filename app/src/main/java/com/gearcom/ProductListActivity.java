package com.gearcom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gearcom.activity.auth.ProfileActivity;
import com.gearcom.adapters.RecyclerViewAdapter;
import com.gearcom.adapters.RecyclerViewInterface;
import com.gearcom.api.Api;
import com.gearcom.api.AuthApi;
import com.gearcom.model.Product;
import com.gearcom.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity implements RecyclerViewInterface {

    private List<Product> productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
//        products = productStore.getProducts();
//        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(ProductListActivity.this, products, ProductListActivity.this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 2));
        Api.api.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.body() != null) {
                    productList = response.body();
                    // Xử lý danh sách sản phẩm ở đây
                    RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(ProductListActivity.this, productList, ProductListActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 2));
                } else {
                    Toast.makeText(ProductListActivity.this, "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClickItem(int position) {
        Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
        Product product = productList.get(position);
        intent.putExtra("product_selected", (Serializable) product);
        startActivity(intent);
    }
}