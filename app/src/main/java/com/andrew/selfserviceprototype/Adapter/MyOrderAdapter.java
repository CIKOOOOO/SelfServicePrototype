package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
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

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Holder> {

    private Context mContext;
    private List<Product> productList;
    private List<Integer> quantityList;

    public interface addDeleteOrder {
        void onAddDeleteOrder(int pos, int quantity, boolean isDelete);
    }

    public interface deleteOrder {
        void onDelete(int pos);
    }

    public void setList(List<Product> product, List<Integer> qty) {
        this.productList = product;
        this.quantityList = qty;
    }

    private deleteOrder deleteOrder;
    private addDeleteOrder addDeleteOrder;

    public MyOrderAdapter(Context mContext, List<Product> productList, List<Integer> quantityList, MyOrderAdapter.deleteOrder deleteOrder, MyOrderAdapter.addDeleteOrder addDeleteOrder) {
        this.mContext = mContext;
        this.productList = productList;
        this.quantityList = quantityList;
        this.deleteOrder = deleteOrder;
        this.addDeleteOrder = addDeleteOrder;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_my_order, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final int pos = holder.getAdapterPosition();
        final Product product = productList.get(position);

        Picasso.get()
                .load(Constant.PRODUCT_URL + product.getProductImage())
                .into(holder.imageView);

        holder.text_quantity.setText(quantityList.get(pos) + "");
        holder.text_title.setText(product.getProductName());

        holder.text_price.setText("Price : IDR " + Utils.priceFormat(quantityList.get(pos) * product.getProductPrice()));

        holder.text_price.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.text_price.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int lineCount = holder.text_price.getLineCount();
                if (lineCount > 1) {
                    holder.text_price.setText("Price :\nIDR " + Utils.priceFormat(quantityList.get(pos) * product.getProductPrice()));
                }
            }
        });

        if (pos == productList.size() - 1) {
            holder.view_divider.setVisibility(View.GONE);
        } else holder.view_divider.setVisibility(View.VISIBLE);

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOrder.onDelete(pos);
                notifyDataSetChanged();
            }
        });

        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = quantityList.get(pos) - 1;
                if (quantity > 0) {
                    quantityList.set(pos, quantity);
                    addDeleteOrder.onAddDeleteOrder(pos, quantity, true);
                    notifyDataSetChanged();
                }
            }
        });

        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = quantityList.get(pos) + 1;
                quantityList.set(pos, quantity);
                addDeleteOrder.onAddDeleteOrder(pos, quantity, false);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private Button btn_remove;
        private TextView text_quantity, text_price, text_title;
        private ImageButton btn_plus, btn_minus;
        private View view_divider;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_image_my_order);
            text_title = itemView.findViewById(R.id.recycler_text_title_my_order);
            text_quantity = itemView.findViewById(R.id.recycler_text_quantity_my_order);
            text_price = itemView.findViewById(R.id.recycler_price_my_order);
            btn_plus = itemView.findViewById(R.id.recycler_btn_plus_my_order);
            btn_minus = itemView.findViewById(R.id.recycler_btn_minus_my_order);
            view_divider = itemView.findViewById(R.id.recycler_view_my_order);
            btn_remove = itemView.findViewById(R.id.recycler_btn_remove_my_order);
        }
    }
}
