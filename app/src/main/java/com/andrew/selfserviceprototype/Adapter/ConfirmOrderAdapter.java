package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConfirmOrderAdapter extends RecyclerView.Adapter<ConfirmOrderAdapter.Holder> {

    private Context mContext;
    private List<Product> productList;
    private List<Integer> quantityList;
    private Typeface typeface, titleTypeface;

    public interface addDeleteOrder {
        void onAddDeleteOrder(int pos, int quantity, boolean isDelete);

        void menuOnDeletes(int pos);

        void onMenuBack(String MID);
    }

    private addDeleteOrder addDeleteOrder;

    public void setList(List<Product> productList, List<Integer> quantityList) {
        this.productList = productList;
        this.quantityList = quantityList;
    }

    public ConfirmOrderAdapter(Context mContext, List<Product> productList, List<Integer> quantityList
            , addDeleteOrder addDeleteOrder) {
        this.mContext = mContext;
        this.productList = productList;
        this.quantityList = quantityList;
        this.addDeleteOrder = addDeleteOrder;
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
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final int pos = position;
        final Product product = productList.get(pos);

        if (productList.size() == 1) {
            addDeleteOrder.menuOnDeletes(pos);
        } else if (getItemViewType(pos) == 1) {
            if (pos == productList.size() - 1) {
                addDeleteOrder.menuOnDeletes(pos);
            }
//            else if (getItemViewType(position + 1) == 1) {
//                menuOnDelete.menuOnDeletes(position);
//            }
            else {
                for (MerchantData merchantData : StaticData.MERCHANT_LIST) {
                    if (merchantData.getMerchantId().equals(product.getMerchantId())) {
//                        holder.merchant_name.setText(merchantData.getMerchantName());
                        Picasso.get()
                                .load(Constant.URL + merchantData.getMerchantIcon())
                                .into(holder.img);
//                        holder.merchant_name.setTypeface(titleTypeface);
                        holder.img_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addDeleteOrder.onMenuBack(product.getMerchantId());
                            }
                        });
                        break;
                    }
                }
            }
        } else {
//            holder.quantity.setText(quantityList.get(pos) + "x");
            holder.name.setText(product.getProductName());
            holder.price.setText("IDR " + Utils.priceFormat(product.getProductPrice() * quantityList.get(pos)) + " ,-");
            holder.quantity.setText("" + quantityList.get(pos));

            holder.price.setTypeface(typeface);
            holder.name.setTypeface(typeface);

            Picasso.get()
                    .load(Constant.PRODUCT_URL + product.getProductImage())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.imageView);

            if (product.getDiscount() > 0) {
//                Utils.priceFormat(product.getProductPrice() - ((product.getProductPrice()) * product.getDiscount()));
                holder.discount.setText(" - IDR " + Utils.priceFormat((product.getProductPrice()) * product.getDiscount()) + " ,-");
                holder.discount.setVisibility(View.VISIBLE);
            } else {
                holder.discount.setVisibility(View.GONE);
            }

//            if (product.isAdvertisement()) {
//                holder.img_btn_minus.setEnabled(false);
//                holder.img_btn_plus.setEnabled(false);
//                holder.img_btn_minus.setBackground(mContext.getDrawable(R.drawable.ic_minus_bombay));
//                holder.img_btn_plus.setBackground(mContext.getDrawable(R.drawable.ic_plus_bombay));
//            } else {
//                holder.img_btn_minus.setEnabled(true);
//                holder.img_btn_plus.setEnabled(true);
//                holder.img_btn_minus.setBackground(mContext.getDrawable(R.drawable.selection_ic_box_minus));
//                holder.img_btn_plus.setBackground(mContext.getDrawable(R.drawable.selection_ic_box_plus));
//                holder.remove.setVisibility(View.GONE);
//            }

            if (quantityList.get(pos) == 0) {
                holder.remove.setVisibility(View.VISIBLE);
            } else holder.remove.setVisibility(View.GONE);

            if (pos + 1 <= productList.size() - 1) {
                if (getItemViewType(pos + 1) == 1) {
                    holder.line.setVisibility(View.GONE);
                } else holder.line.setVisibility(View.VISIBLE);
            } else {
                holder.line.setVisibility(View.GONE);
            }

            holder.img_btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = quantityList.get(pos) + 1;
                    if (product.isAdvertisement()) {
                        if (quantity <= 1) {
                            quantityList.set(pos, quantity);
                            addDeleteOrder.onAddDeleteOrder(pos, quantity, false);
                        }
                    } else {
                        quantityList.set(pos, quantity);
                        addDeleteOrder.onAddDeleteOrder(pos, quantity, false);
                    }
                    notifyItemChanged(pos);
                }
            });


            holder.img_btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = quantityList.get(pos) - 1;
                    if (quantity >= 0) {
                        quantityList.set(pos, quantity);
                        addDeleteOrder.onAddDeleteOrder(pos, quantity, true);
                        notifyItemChanged(pos);
                    }
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDeleteOrder.menuOnDeletes(pos);
//                    if (pos + 1 <= productList.size() - 1) {
//                        if (getItemViewType(pos + 1) == 1 && getItemViewType(pos - 1) == 1) {
////                            quantityList.remove(pos - 1);
////                            productList.remove(pos - 1);
//                            menuOnDelete.menuOnDeletes(pos - 1);
//                        }
//                    }
                }
            });

//            holder.quantity.setTypeface(typeface);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView quantity, name, price, discount;
        private ImageView img;
        private View line;
        private ImageButton img_btn_minus, img_btn_plus, img_back;
        private Button remove;
        private RoundedImageView imageView;

        Holder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.recycler_text_quantity_confirm_order);
            name = itemView.findViewById(R.id.recycler_product_name_confirm_order);
            price = itemView.findViewById(R.id.recycler_price_confirm_order);
            img = itemView.findViewById(R.id.recycler_image_confirm_order);
            discount = itemView.findViewById(R.id.recycler_discount_confirm_order);
            line = itemView.findViewById(R.id.recycler_line_confirm_order);
            remove = itemView.findViewById(R.id.recycler_btn_remove_confirm_order);
            img_btn_minus = itemView.findViewById(R.id.recycler_btn_minus_confirm_order);
            img_btn_plus = itemView.findViewById(R.id.recycler_btn_plus_confirm_order);
            imageView = itemView.findViewById(R.id.recycler_image_product_confirm_order);
            img_back = itemView.findViewById(R.id.recycler_img_btn_back_confirm_order);
        }
    }
}
