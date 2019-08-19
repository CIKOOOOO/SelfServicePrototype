package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MerchantListAdapter extends RecyclerView.Adapter<MerchantListAdapter.Holder> {

    private Context mContext;
    private int lastPosition;
    private List<MerchantData> merchantData;

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
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_merchant_list, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        MerchantData md = merchantData.get(position);

        holder.textView.setText(md.getMerchantName());

        Picasso.get()
                .load(Constant.URL + md.getMerchantIcon())
                .into(holder.imageView);

//        if (position == lastPosition) {
//            holder.imageView.getLayoutParams().width = 700;
//            holder.imageView.getLayoutParams().height = 700;
//        } else {
//            Picasso.get()
//                    .load(Constant.URL + md.getMerchantIcon())
//                    .into(holder.imageView);
//            holder.imageView.getLayoutParams().width = 400;
//            holder.imageView.getLayoutParams().height = 400;
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (lastPosition == merchantData.size() - 1) {
//                    // Data is null... need to add this one because it will prevent error that recyclerview cannot show the last data
//                    // Trust me, i don't want it either
//                } else if (lastPosition != position) {
//                    imageOnClick.onClick(position);
//                    lastPosition = position;
//                } else {
                    // Here is to process when user click the center of image
                    imageOnClick.onClick(position);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return merchantData.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recycler_text_title);
            imageView = itemView.findViewById(R.id.recycler_image_merchant_list);
        }
    }
}
