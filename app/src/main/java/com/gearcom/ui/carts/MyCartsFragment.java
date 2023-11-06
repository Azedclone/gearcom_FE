package com.gearcom.ui.carts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gearcom.R;
import com.gearcom.activity.auth.LoginActivity;
import com.gearcom.adapters.CartItemCallback;
import com.gearcom.adapters.CategoriesHomeAdapter;
import com.gearcom.adapters.MyCartAdapter;
import com.gearcom.adapters.RecyclerViewInterface;
import com.gearcom.api.Api;
import com.gearcom.api.model.BillBody;
import com.gearcom.api.model.BillDetailBody;
import com.gearcom.api.model.CartBody;
import com.gearcom.model.Bill;
import com.gearcom.model.BillDetail;
import com.gearcom.model.Cart;
import com.gearcom.model.Category;
import com.gearcom.model.User;
import com.gearcom.ui.products.ProductDetailActivity;
import com.gearcom.ui.products.ProductListActivity;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

import com.vnpay.authentication.VNP_AuthenticationActivity;
import com.vnpay.authentication.VNP_SdkCompletedCallback;

public class MyCartsFragment extends Fragment {
    List<Cart> carts;
    private TextView tvPriceTotal;
    private double totalPrice = 0;
    private boolean isBill;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate((R.layout.fragment_my_carts), container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE);
        String jwt = sharedPreferences.getString("jwt", "");
        RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
            @Override
            public void onClickItem(int position) {

            }
        };
        tvPriceTotal = root.findViewById(R.id.tvPriceTotal);

        if (!jwt.isEmpty()) {
            Api.api.getCartByUserId("Bearer " + jwt).enqueue(new Callback<List<Cart>>() {
                @Override
                public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                    if (response.body() != null) {
                        carts = response.body();
                        if(carts.size()==0) {
                            root.findViewById(R.id.constraint1).setVisibility(View.VISIBLE);
                            root.findViewById(R.id.constraint2).setVisibility(View.GONE);
                            return ;
                        }
                        // Xử lý danh sách sản phẩm ở đây
                        RecyclerView recyclerView = root.findViewById(R.id.mRecyclerView);
                        MyCartAdapter adapter = new MyCartAdapter(getActivity(), carts, recyclerViewInterface);
                        adapter.setCartItemCallback(new CartItemCallback() {
                            @Override
                            public void onAddClicked(int position) {
                                CartBody cartBody = new CartBody();
                                cartBody.setProduct(carts.get(position).getProduct());
                                carts.get(position).setQuantity(carts.get(position).getQuantity() + 1);
                                adapter.notifyDataSetChanged();
                                Api.api.addToCart("Bearer " + jwt, cartBody).enqueue(new Callback<Response<HTTP>>() {
                                    @Override
                                    public void onResponse(Call<Response<HTTP>> call, Response<Response<HTTP>> response) {
                                        if (response.code() == 200) {

                                        } else if (response.code() == 409) {
                                            Toast.makeText(getActivity(), "Add fail!", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Response<HTTP>> call, Throwable t) {
                                        System.out.println(t);
                                    }
                                });
                                calculateTotalPrice();
                            }

                            @Override
                            public void onMinusClicked(int position) {
                                CartBody cartBody = new CartBody();
                                cartBody.setProduct(carts.get(position).getProduct());
                                carts.get(position).setQuantity(carts.get(position).getQuantity() - 1);
                                adapter.notifyDataSetChanged();
                                Api.api.minusQuantity("Bearer " + jwt, cartBody).enqueue(new Callback<Response<HTTP>>() {
                                    @Override
                                    public void onResponse(Call<Response<HTTP>> call, Response<Response<HTTP>> response) {
                                        if (response.code() == 200) {

                                        } else if (response.code() == 409) {
                                            Toast.makeText(getActivity(), "Fail!", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Response<HTTP>> call, Throwable t) {
                                        System.out.println(t);
                                    }
                                });
                                calculateTotalPrice();
                            }

                        });
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

                        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                            @Override
                            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                return false;
                            }

                            @Override
                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                int position = viewHolder.getAdapterPosition();
                                CartBody cartBody = new CartBody();
                                cartBody.setProduct(carts.get(position).getProduct());
                                Api.api.removeFromCart("Bearer " + jwt, cartBody).enqueue(new Callback<Response<HTTP>>() {
                                    @Override
                                    public void onResponse(Call<Response<HTTP>> call, Response<Response<HTTP>> response) {
                                        if (response.code() == 200) {
                                            Toast.makeText(getActivity(), "Add success", Toast.LENGTH_LONG).show();
                                        } else if (response.code() == 409) {
                                            Toast.makeText(getActivity(), "Add fail!", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Response<HTTP>> call, Throwable t) {
                                        System.out.println(t);
                                    }
                                });
                                calculateTotalPrice();
                            }
                        };

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyCartAdapter.SwipeToDeleteCallback(adapter));
                        itemTouchHelper.attachToRecyclerView(recyclerView);

                        calculateTotalPrice();
                    } else {
                        Toast.makeText(getActivity(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Cart>> call, Throwable t) {
                }
            });
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        Button buy =  root.findViewById(R.id.buy_now);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBill = true;
                List<BillDetailBody> list = new ArrayList<>();
                for (Cart c : carts
                ) {
                    BillDetailBody billDetailBody = new BillDetailBody();
                    billDetailBody.setProductId(c.getProduct().getId());
                    billDetailBody.setQuantity(c.getQuantity());
                    list.add(billDetailBody);
                }
                Bill bill = new Bill();
                bill.setId(1);
                bill.setTotalPrice(totalPrice);
                BillBody billBody = new BillBody();
                billBody.setBill(bill);
                billBody.setBillDetailBodies(list);
                Api.api.createBill(billBody,"Bearer " + jwt).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });
                Api.api.removeCartByUserId("Bearer " + jwt).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            carts = new ArrayList<>();
                            root.findViewById(R.id.constraint1).setVisibility(View.VISIBLE);
                            root.findViewById(R.id.constraint2).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        System.out.println(t);
                        Toast.makeText(getActivity(), "Something went wrong buy now", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
        return root;
    }

    private void calculateTotalPrice() {
        totalPrice = 0;

        for (Cart c : carts
        ) {
            totalPrice += c.getQuantity() * c.getProduct().getPrice();
        }
        tvPriceTotal.setText("Total price: " + totalPrice);
    }
//    public void openSdk() {
//        Intent intent = new Intent(getActivity(), VNP_AuthenticationActivity.class);
//        intent.putExtra("url", "https://sandbox.vnpayment.vn/testsdk/"); //bắt buộc, VNPAY cung cấp
//        intent.putExtra("tmn_code", "FAHASA03"); //bắt buộc, VNPAY cung cấp
//        intent.putExtra("scheme", "resultactivity"); //bắt buộc, scheme để mở lại app khi có kết quả thanh toán từ mobile banking
//        intent.putExtra("is_sandbox", false); //bắt buộc, true <=> môi trường test, true <=> môi trường live
//        VNP_AuthenticationActivity.setSdkCompletedCallback(new VNP_SdkCompletedCallback() {
//            @Override
//            public void sdkAction(String action) {
//                Log.wtf("SplashActivity", "action: " + action);
//                //action == AppBackAction
//                //Người dùng nhấn back từ sdk để quay lại
//
//                //action == CallMobileBankingApp
//                //Người dùng nhấn chọn thanh toán qua app thanh toán (Mobile Banking, Ví...)
//                //lúc này app tích hợp sẽ cần lưu lại cái PNR, khi nào người dùng mở lại app tích hợp thì sẽ gọi kiểm tra trạng thái thanh toán của PNR Đó xem đã thanh toán hay chưa.
//
//                //action == WebBackAction
//                //Người dùng nhấn back từ trang thanh toán thành công khi thanh toán qua thẻ khi url có chứa: cancel.sdk.merchantbackapp
//
//                //action == FaildBackAction
//                //giao dịch thanh toán bị failed
//
//                //action == SuccessBackAction
//                //thanh toán thành công trên webview
//            }
//        });
//        startActivity(intent);
//    }
}