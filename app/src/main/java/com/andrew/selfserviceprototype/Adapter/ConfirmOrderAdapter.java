package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.andrew.selfserviceprototype.Utils.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConfirmOrderAdapter extends RecyclerView.Adapter<ConfirmOrderAdapter.Holder> {

    private Context mContext;
    private List<Product> productList;
    private List<Integer> quantityList;
    private Typeface typeface, titleTypeface;

    public void setList(List<Product> productList, List<Integer> quantityList) {
        this.productList = productList;
        this.quantityList = quantityList;
    }

    public ConfirmOrderAdapter(Context mContext, List<Product> productList, List<Integer> quantityList) {
        this.mContext = mContext;
        this.productList = productList;
        this.quantityList = quantityList;
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/arial.ttf");
        titleTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/axure_handwriting.ttf");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_confirm_order, parent, false);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_merchant_name_confirm_order, parent, false);
        }
        return new Holder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (!productList.get(position).getProductId().isEmpty()) {
            return 0;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int pos = holder.getAdapterPosition();
        Product product = productList.get(pos);
        if (getItemViewType(pos) == 0) {
//            holder.quantity.setText(quantityList.get(pos) + "x");
            holder.name.setText("(" + quantityList.get(pos) + "x) " + product.getProductName());
            holder.price.setText("IDR " + Utils.priceFormat(product.getProductPrice() * quantityList.get(pos)) + " ,-");
            holder.name.setTypeface(typeface);
            holder.price.setTypeface(typeface);
//            holder.quantity.setTypeface(typeface);
        } else {
            for (MerchantData merchantData : StaticData.MERCHANT_LIST) {
                if (merchantData.getMerchantId().equals(product.getMerchantId())) {
                    holder.merchant_name.setText(merchantData.getMerchantName());
                    holder.merchant_name.setTypeface(titleTypeface);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView quantity, name, price, merchant_name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.recycler_quantity_confirm_order);
            name = itemView.findViewById(R.id.recycler_product_name_confirm_order);
            price = itemView.findViewById(R.id.recycler_price_confirm_order);
            merchant_name = itemView.findViewById(R.id.recycler_text_merchant_name_confirm_order);
        }
    }
}
