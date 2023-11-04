package com.gearcom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gearcom.R;
import com.gearcom.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAllProductsAdapter extends RecyclerView.Adapter<ListAllProductsAdapter.MyViewHolder>{
    private final ListAllProductsInterface listAllProductsInterface;
    Context context;
    List<Product> productList;

    public ListAllProductsAdapter( Context context, List<Product> productList, ListAllProductsInterface listAllProductsInterface) {
        this.listAllProductsInterface = listAllProductsInterface;
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ListAllProductsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_product, parent, false);
        return new ListAllProductsAdapter.MyViewHolder(view,listAllProductsInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAllProductsAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(productList.get(position).getName());
        holder.tvPrice.setText(productList.get(position).getPrice().toString());
        String imageUrl = productList.get(position).getImageUrl();
        Picasso.get().load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvName, tvPrice;
        public MyViewHolder(@NonNull View itemView, ListAllProductsInterface listAllProductsInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.allproimageView);
            tvName = itemView.findViewById((R.id.allprotvName));
            tvPrice = itemView.findViewById((R.id.allprotvPrice));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listAllProductsInterface != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listAllProductsInterface.onClickItem(position);
                        }
                    }
                }
            });
        }
    }
}
