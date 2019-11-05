package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MerchantListAdapter extends RecyclerView.Adapter<MerchantListAdapter.Holder> {

    private Context mContext;
    private int lastPosition;
    private List<MerchantData> merchantData;
    private Typeface typeface;

    public interface imageOnClick {
        void onClick(int position);
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    private imageOnClick imageOnClick;

    public MerchantListAdapter(Context mContext, List<MerchantData> merchantData, imageOnClick imageOnClick) {
        this.mContext = mContext;
        this.merchantData = merchantData;
        this.imageOnClick = imageOnClick;
        lastPosition = 0;
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/clarendon_bt.ttf");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0)
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_merchant_list, parent, false);
        else
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_merchant_list_parent, parent, false);
        return new Holder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return merchantData.get(position).getMerchantId().equals("null") ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        MerchantData md = merchantData.get(position);
        if (getItemViewType(position) == 0) {
            holder.textView.setTypeface(typeface);

            holder.textView.setText(md.getMerchantName());

            Picasso.get()
                    .load(Constant.URL + md.getMerchantIcon())
                    .into(holder.imageView);

            if (!md.getMerchantImagePromo().isEmpty())
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.athens_gray_palette));
            else
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white_color));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!md.getMerchantImagePromo().isEmpty())
                        imageOnClick.onClick(position);
                }
            });
        } else {
            holder.text_parent.setText(md.getMerchantName());
        }
    }

    @Override
    public int getItemCount() {
        return merchantData.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView, text_parent;
        CardView cardView;

        Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recycler_text_title);
            imageView = itemView.findViewById(R.id.recycler_image_merchant_list);
            text_parent = itemView.findViewById(R.id.recycler_text_parent_merchant_list);
            cardView = itemView.findViewById(R.id.recycler_card_merchant_list);
        }
    }
}
