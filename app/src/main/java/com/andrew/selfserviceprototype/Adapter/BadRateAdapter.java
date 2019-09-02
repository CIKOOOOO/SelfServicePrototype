package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class BadRateAdapter extends RecyclerView.Adapter<BadRateAdapter.Holder> {

    private List<Boolean> booleanList;
    private List<String> badRateList;
    private Context mContext;

    public BadRateAdapter(List<String> badRateList, Context mContext) {
        this.badRateList = badRateList;
        this.mContext = mContext;
        booleanList = new ArrayList<>(Constant.getFalseList());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_bad_rate, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.btn.setText(badRateList.get(position));
        if (booleanList.get(position)) {
            holder.btn.setBackground(mContext.getDrawable(R.drawable.rectangle_mariner));
            holder.btn.setTextColor(mContext.getResources().getColor(R.color.white_color));
        } else {
            holder.btn.setBackground(mContext.getDrawable(R.drawable.rectangle_stroke_mariner));
            holder.btn.setTextColor(mContext.getResources().getColor(R.color.blue_palette));
        }

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (booleanList.get(position)) {
                    booleanList.set(position, false);
                } else {
                    booleanList.set(position, true);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return badRateList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private Button btn;

        Holder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.recycler_btn_bad_rate);
        }
    }
}
