package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConfirmOrderAdapter extends RecyclerView.Adapter<ConfirmOrderAdapter.Holder> {

    private Context mContext;
    private List<Product> productList;
    private List<Integer> quantityList;
    private Typeface typeface;

    public ConfirmOrderAdapter(Context mContext, List<Product> productList, List<Integer> quantityList) {
        this.mContext = mContext;
        this.productList = productList;
        this.quantityList = quantityList;
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/arial.ttf");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_confirm_order, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int pos = holder.getAdapterPosition();
        Product product = productList.get(pos);
        holder.quantity.setText(quantityList.get(pos) + "x");
        holder.name.setText(product.getProductName());
        holder.price.setText("IDR " + Utils.priceFormat(product.getProductPrice() * quantityList.get(pos)) + " ,-");
        holder.quantity.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView quantity, name, price;

        public Holder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.recycler_quantity_confirm_order);
            name = itemView.findViewById(R.id.recycler_product_name_confirm_order);
            price = itemView.findViewById(R.id.recycler_price_confirm_order);
        }
    }
}
