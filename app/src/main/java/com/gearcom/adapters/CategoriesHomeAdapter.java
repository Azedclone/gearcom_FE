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
import com.gearcom.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoriesHomeAdapter extends RecyclerView.Adapter<CategoriesHomeAdapter.MyViewHolder>{

    private final CategoriesHomeInterface categoriesHomeInterface;
    Context context;
    List<Category> categoryList;

    public CategoriesHomeAdapter( Context context, List<Category> categoryList, CategoriesHomeInterface categoriesHomeInterface) {
        this.categoriesHomeInterface = categoriesHomeInterface;
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoriesHomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_cate_item, parent, false);
        return new CategoriesHomeAdapter.MyViewHolder(view,categoriesHomeInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesHomeAdapter.MyViewHolder holder, int position) {
        holder.nameCate.setText(categoryList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCate;
        TextView nameCate;
        public MyViewHolder(@NonNull View itemView, CategoriesHomeInterface categoriesHomeInterface) {
            super(itemView);
            imgCate = itemView.findViewById(R.id.home_cart_img);
            nameCate = itemView.findViewById(R.id.home_cart_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (categoriesHomeInterface != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            categoriesHomeInterface.onClickItem(position);
                        }
                    }
                }
            });
        }
    }
}
