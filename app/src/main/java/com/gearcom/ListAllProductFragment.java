package com.gearcom;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gearcom.adapters.ListAllProductsAdapter;
import com.gearcom.adapters.ListAllProductsInterface;
import com.gearcom.adapters.RecyclerViewAdapter;
import com.gearcom.api.Api;
import com.gearcom.model.Product;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListAllProductFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListAllProductFragment extends Fragment {
    private List<Product> productList;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public ListAllProductFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ListAllProductFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ListAllProductFragment newInstance(String param1, String param2) {
//        ListAllProductFragment fragment = new ListAllProductFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate((R.layout.fragment_list_all_product),container,false);
        ListAllProductsInterface listAllProductsInterface = new ListAllProductsInterface() {
            @Override
            public void onClickItem(int position) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                Product product = productList.get(position);
                intent.putExtra("product_selected", (Serializable) product);
                startActivity(intent);
            }
        };
        Api.api.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.body() != null) {
                    productList = response.body();

                    // Xử lý danh sách sản phẩm ở đây
                    RecyclerView recyclerView = root.findViewById(R.id.listAllProduct);
                    ListAllProductsAdapter adapter = new ListAllProductsAdapter(getActivity(), productList, listAllProductsInterface);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                } else {
                    Toast.makeText(getActivity(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}