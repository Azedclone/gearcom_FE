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
import com.gearcom.model.Product;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    List<Product> productList;

    public RecyclerViewAdapter(Context context, List<Product> productList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.productList = productList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new RecyclerViewAdapter.MyViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(productList.get(position).getName());
        holder.tvPrice.setText(productList.get(position).getPrice().toString()+ "$");
        String imageUrl = productList.get(position).getImageUrl();
        Picasso.get().load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }
    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(productList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvName, tvPrice;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById((R.id.tvName));
            tvPrice = itemView.findViewById((R.id.tvPrice));

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
        private RecyclerViewAdapter adapter;

        public SwipeToDeleteCallback(RecyclerViewAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            adapter.removeItem(position);
        }
    }
    public static class ItemMoveCallback extends ItemTouchHelper.Callback {
        private RecyclerViewAdapter adapter;

        public ItemMoveCallback(RecyclerViewAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // Cho phép di chuyển lên và xuống
            int swipeFlags = 0; // Không cho phép swipe
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            adapter.moveItem(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Không làm gì trong trường hợp này
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true; // Cho phép di chuyển khi giữ lâu trên item
        }
    }
}
