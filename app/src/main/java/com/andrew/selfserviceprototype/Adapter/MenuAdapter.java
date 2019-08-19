package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.Holder> {

    private Context mContext;
    private List<Product> productList;

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public interface menuOnClick {
        void onMenuClick(int pos);
    }

    private menuOnClick menuOnClick;

    public MenuAdapter(Context mContext, List<Product> productList, MenuAdapter.menuOnClick menuOnClick) {
        this.mContext = mContext;
        this.productList = productList;
        this.menuOnClick = menuOnClick;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_menu_order, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final Product product = productList.get(position);
        Picasso.get()
                .load(Constant.PRODUCT_URL + product.getProductImage())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.imageView);
        holder.price.setText("IDR " + Utils.priceFormat(product.getProductPrice()));
        holder.title.setText(product.getProductName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                menuOnClick.onMenuClick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView price, title;
        private ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.recycler_text_price_menu);
            title = itemView.findViewById(R.id.recycler_text_title_menu);
            imageView = itemView.findViewById(R.id.recycler_image_menu);
        }
    }
}
