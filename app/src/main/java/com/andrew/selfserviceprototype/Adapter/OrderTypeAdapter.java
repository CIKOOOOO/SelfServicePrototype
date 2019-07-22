package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.andrew.selfserviceprototype.Model.OrderType;
import com.andrew.selfserviceprototype.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderTypeAdapter extends RecyclerView.Adapter<OrderTypeAdapter.Holder> {

    private Context context;
    private List<OrderType> orderTypeList;
    private Typeface typeface;

    public interface onOrderClick {
        void onClick(int pos);
    }

    private onOrderClick onOrderClick;

    public void setOrderTypeList(List<OrderType> orderTypeList) {
        this.orderTypeList = orderTypeList;
    }

    public OrderTypeAdapter(Context context, List<OrderType> orderTypeList, OrderTypeAdapter.onOrderClick onOrderClick) {
        this.context = context;
        this.orderTypeList = orderTypeList;
        this.onOrderClick = onOrderClick;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/clarendon_bt.ttf");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_payment, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.button.setText(orderTypeList.get(position).getOrderTypeName());
        holder.button.setTypeface(typeface);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOrderClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderTypeList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private Button button;

        Holder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.recycler_type_payment);
        }
    }
}
