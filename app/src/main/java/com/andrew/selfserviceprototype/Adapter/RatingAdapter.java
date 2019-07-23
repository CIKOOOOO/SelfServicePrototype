package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrew.selfserviceprototype.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.Holder> {
    private Context context;
    private List<String> ratingList;
    private int lastPosition;

    public interface starOnClick {
        void clickRate(int pos);
    }

    private starOnClick starOnClick;

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public RatingAdapter(Context context, List<String> ratingList, RatingAdapter.starOnClick starOnClick) {
        this.context = context;
        this.ratingList = ratingList;
        this.starOnClick = starOnClick;
        lastPosition = -1;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_rating, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        if (position <= lastPosition) {
            Picasso.get().load(R.drawable.ic_star_fill).into(holder.imageView);
        } else {
            Picasso.get().load(R.drawable.ic_star_no_fill).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starOnClick.clickRate(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_image_rating);
        }
    }
}
