package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Holder> {

    private Context mContext;
    private List<Product> productList;
    private List<Integer> quantityList;
    private List<Boolean> isUpdate;

    public interface iAdapterMyOrder {
        void onAddDeleteOrder(int pos, int quantity, boolean isDelete);

        void onDelete(int pos);

        void onUpdateList(int pos);
    }

    public void setIsUpdate(List<Boolean> isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void setList(List<Product> product, List<Integer> qty) {
        this.productList = product;
        this.quantityList = qty;
    }

    private iAdapterMyOrder iAdapterMyOrder;

    public MyOrderAdapter(Context mContext, List<Product> productList, List<Integer> quantityList
            , List<Boolean> isUpdate, iAdapterMyOrder iAdapterMyOrder) {
        this.mContext = mContext;
        this.iAdapterMyOrder = iAdapterMyOrder;
        this.productList = productList;
        this.quantityList = quantityList;
        this.isUpdate = isUpdate;
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

        if (isUpdate.get(pos)) {
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(5);
            holder.itemView.startAnimation(anim);
            isUpdate.set(pos, false);
            iAdapterMyOrder.onUpdateList(pos);
        }

//        if (product.isAdvertisement()) {
//            holder.btn_minus.setEnabled(false);
//            holder.btn_plus.setEnabled(false);
//            holder.btn_minus.setBackground(mContext.getDrawable(R.drawable.ic_minus_bombay));
//            holder.btn_plus.setBackground(mContext.getDrawable(R.drawable.ic_plus_bombay));
//            holder.btn_remove.setVisibility(View.VISIBLE);
//        } else {
//            holder.btn_minus.setEnabled(true);
//            holder.btn_plus.setEnabled(true);
//            holder.btn_minus.setBackground(mContext.getDrawable(R.drawable.selection_ic_box_minus));
//            holder.btn_plus.setBackground(mContext.getDrawable(R.drawable.selection_ic_box_plus));
//
//        }

        if (quantityList.get(pos) == 0) {
            holder.btn_remove.setVisibility(View.VISIBLE);
        } else {
            holder.btn_remove.setVisibility(View.GONE);
        }

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iAdapterMyOrder.onDelete(pos);
                notifyDataSetChanged();
            }
        });

        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = quantityList.get(pos) - 1;
                if (quantity >= 0) {
                    quantityList.set(pos, quantity);
                    iAdapterMyOrder.onAddDeleteOrder(pos, quantity, true);
                    notifyDataSetChanged();
                }
            }
        });

        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = quantityList.get(pos) + 1;
                if (product.isAdvertisement()) {
                    if (quantity <= 1) {
                        quantityList.set(pos, quantity);
                        iAdapterMyOrder.onAddDeleteOrder(pos, quantity, false);
                    }
                } else {
                    quantityList.set(pos, quantity);
                    iAdapterMyOrder.onAddDeleteOrder(pos, quantity, false);
                }
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
