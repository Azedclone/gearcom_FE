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
import com.gearcom.VNPay.VNP_Authentication;
import com.gearcom.VNPay.VNP_SdkCompletedCallback;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

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
                        if (carts.size() == 0) {
                            root.findViewById(R.id.constraint1).setVisibility(View.VISIBLE);
                            root.findViewById(R.id.constraint2).setVisibility(View.GONE);
                            return;
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
                                carts.remove(carts.get(position));
                                adapter.notifyDataSetChanged();
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

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
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
        Button buy = root.findViewById(R.id.buy_now);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openSdk(root, jwt);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
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

    public void openSdk(View root, String jwt) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String vnp_TxnRef = getRandomNumber(8);
        String vnp_IpAddr = "1.1";
        String vnp_TmnCode = "DE7NBVFA";
        int amount = (int) (totalPrice * 23000);
        Map vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        String bank_code = null;
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Payment orders:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = null;
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "en");
        }
        vnp_Params.put("vnp_ReturnUrl", "https://localhost/́8080");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512("WLNXPSPPFOPETNAWEZICJMGWIMXRGUBX", hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html" + "?" + queryUrl;
        Intent intent = new Intent(getActivity(), VNP_Authentication.class);
        intent.putExtra("url", paymentUrl); //bắt buộc, VNPAY cung cấp
        intent.putExtra("tmn_code", "DE7NBVFA"); //bắt buộc, VNPAY cung cấp
        intent.putExtra("scheme", "MainActivity"); //bắt buộc, scheme để mở lại app khi có kết quả thanh toán từ mobile banking
        intent.putExtra("is_sandbox", true); //bắt buộc, true <=> môi trường test, true <=> môi trường live
        VNP_Authentication.setSdkCompletedCallback(new VNP_SdkCompletedCallback() {
            @Override
            public void sdkAction(String action) {
                Log.wtf("SplashActivity", "action: " + action);
                //action == AppBackAction
                //Người dùng nhấn back từ sdk để quay lại

                //action == CallMobileBankingApp
                //Người dùng nhấn chọn thanh toán qua app thanh toán (Mobile Banking, Ví...)
                //lúc này app tích hợp sẽ cần lưu lại cái PNR, khi nào người dùng mở lại app tích hợp thì sẽ gọi kiểm tra trạng thái thanh toán của PNR Đó xem đã thanh toán hay chưa.

                //action == WebBackAction
                //Người dùng nhấn back từ trang thanh toán thành công khi thanh toán qua thẻ khi url có chứa: cancel.sdk.merchantbackapp

                //action == FaildBackAction
                //giao dịch thanh toán bị failed
                if (action.equals("FaildBackAction")) {
                    Toast.makeText(getActivity(), "Payment transaction failed", Toast.LENGTH_SHORT).show();
                }
                //action == SuccessBackAction
                //thanh toán thành công trên webview
                if (action.equals("SuccessBackAction")) {
                    Buy(root, jwt);
                    Toast.makeText(getActivity(), "Payment success", Toast.LENGTH_SHORT).show();
                }

            }
        });
        startActivity(intent);
    }

    private void Buy(View root, String jwt) {
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
        Api.api.createBill(billBody, "Bearer " + jwt).enqueue(new Callback<Void>() {
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

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}