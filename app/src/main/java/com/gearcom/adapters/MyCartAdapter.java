package com.gearcom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.gearcom.R;
import com.gearcom.model.Cart;
import com.gearcom.model.Product;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    List<Cart> carts;
    int total_quantity;
    private CartItemCallback cartItemCallback;

    public void setCartItemCallback(CartItemCallback callback) {
        this.cartItemCallback = callback;
    }

    public MyCartAdapter(Context context, List<Cart> carts, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.carts = carts;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyCartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_item, parent, false);
        return new MyCartAdapter.MyViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(carts.get(position).getProduct().getName());
        holder.tvPrice.setText(carts.get(position).getProduct().getPrice().toString()+ "$");
        holder.tvQuantity.setText(String.valueOf(carts.get(position).getQuantity()));
        String imageUrl = carts.get(position).getProduct().getImageUrl();
        Picasso.get().load(imageUrl).into(holder.imageView);
        total_quantity = carts.get(position).getQuantity();
        holder.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItemCallback != null) {
                    cartItemCallback.onAddClicked(position);
                }
                notifyDataSetChanged();
            }
        });
        holder.RemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItemCallback != null) {
                    cartItemCallback.onMinusClicked(position);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public void updateProductList(List<Cart> carts) {
        this.carts = carts;
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        carts.remove(position);
        notifyItemRemoved(position);
    }
    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(carts, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, addItem,RemoveItem;
        TextView tvName, tvPrice,tvQuantity;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById((R.id.tvName));
            tvPrice = itemView.findViewById((R.id.tvPrice));
            tvQuantity = itemView.findViewById((R.id.tvQuantity));
            addItem = itemView.findViewById((R.id.addItem));
            RemoveItem = itemView.findViewById((R.id.removeItem));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onClickItem(position);
                        }
                    }
                }
            });
        }

    }
    public static class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private MyCartAdapter adapter;

        public SwipeToDeleteCallback(MyCartAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }
}
