package com.gearcom.ui.products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.gearcom.R;
import com.gearcom.adapters.ListAllProductsAdapter;
import com.gearcom.adapters.ListAllProductsInterface;
import com.gearcom.api.Api;
import com.gearcom.model.Product;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity implements ListAllProductsInterface {

    private List<Product> productList,productListSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
//        products = productStore.getProducts();
//        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(ProductListActivity.this, products, ProductListActivity.this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 2));
        Intent intent = getIntent();
        if (intent != null) {
            String data = intent.getStringExtra("category_selected"); // key là tên của dữ liệu được gửi đi
            if (data == null) {
                Api.api.getProducts().enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.body() != null) {
                            productList = response.body();
                            // Xử lý danh sách sản phẩm ở đây
                            RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
                            ListAllProductsAdapter adapter = new ListAllProductsAdapter(ProductListActivity.this, productList, ProductListActivity.this);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 1));
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
//            else if(data != null){
//                Api.api.getProducts().enqueue(new Callback<List<Product>>() {
//                    @Override
//                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
//                        if (response.body() != null) {
//                            productList = response.body();
//                            // Xử lý danh sách sản phẩm ở đây
//                            if (productList != null){
//
//                                for (Product product : productList) {
//                                    if (product.getCategory().getId() == Integer.parseInt(data)) {
//                                        productListSearch.add(product);
//                                    }
//                                }
//                            }
//                            RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
//                            ListAllProductsAdapter adapter = new ListAllProductsAdapter(ProductListActivity.this, productList, ProductListActivity.this);
//                            recyclerView.setAdapter(adapter);
//                            recyclerView.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 1));
//                        } else {
//                            Toast.makeText(ProductListActivity.this, "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Product>> call, Throwable t) {
//                        Toast.makeText(ProductListActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
        }


    }

    @Override
    public void onClickItem(int position) {
        Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
        Product product = productList.get(position);
        intent.putExtra("product_selected", (Serializable) product);
        startActivity(intent);
    }
}