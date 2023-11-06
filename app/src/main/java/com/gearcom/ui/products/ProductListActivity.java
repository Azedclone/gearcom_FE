package com.gearcom.ui.products;
import android.view.MenuItem;
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
import com.gearcom.model.Category;
import com.gearcom.model.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity implements ListAllProductsInterface {

    private List<Product> productList, productListSearch;
    private String keySearch; // Thêm biến keySearch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setTitle("List Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Xử lý khi người dùng nhấn nút "Back" trên ActionBar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }

        Intent intent = getIntent();
        Category cateData = (Category) intent.getSerializableExtra("category_selected");
        keySearch = intent.getStringExtra("keySearch"); // Lấy giá trị từ Intent

        if (keySearch != null && !keySearch.isEmpty()) { // Kiểm tra keySearch
            Api.api.getProducts().enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.body() != null) {
                        productList = response.body();

                        // Tạo danh sách mới để lưu trữ sản phẩm chứa keySearch
                        List<Product> filteredProducts = new ArrayList<>();

                        // Lặp qua danh sách sản phẩm ban đầu
                        for (Product product : productList) {
                            // Kiểm tra xem tên sản phẩm có chứa keySearch không
                            if (product.getName().toLowerCase().contains(keySearch.toLowerCase())) {
                                // Nếu chứa, thêm sản phẩm này vào danh sách filteredProducts
                                filteredProducts.add(product);
                            }
                        }

                        if (filteredProducts.isEmpty()) {
                            Toast.makeText(ProductListActivity.this, "No matching products found.", Toast.LENGTH_SHORT).show();
                        }

                        // Sử dụng danh sách filteredProducts cho RecyclerView
                        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
                        ListAllProductsAdapter adapter = new ListAllProductsAdapter(ProductListActivity.this, filteredProducts, ProductListActivity.this);
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
        } else if (cateData != null) {
            Api.api.getProductsByCategoryId(cateData.getId()).enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.body() != null) {
                        productList = response.body();
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
        } else {
            Api.api.getProducts().enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.body() != null) {
                        productList = response.body();

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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Xử lý khi người dùng nhấn nút "Up" trên ActionBar
            onBackPressed(); // Sử dụng để kết thúc Activity và quay lại trang trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClickItem(int position) {
        Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
        Product product = productList.get(position);
        intent.putExtra("product_selected", (Serializable) product);
        startActivity(intent);
    }
}