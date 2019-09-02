package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Model.OrderType;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderTypeAdapter extends RecyclerView.Adapter<OrderTypeAdapter.Holder> {

    private Context context;
    private List<OrderType> orderTypeList;
    private Typeface typeface;
    private int lastPosition;

    public interface onOrderClick {
        void onClick(int pos);
    }

    private onOrderClick onOrderClick;

    public int getLastPosition() {
        return lastPosition;
    }

    public void setOrderTypeList(List<OrderType> orderTypeList) {
        this.orderTypeList = orderTypeList;
    }

    public OrderTypeAdapter(Context context, List<OrderType> orderTypeList, OrderTypeAdapter.onOrderClick onOrderClick) {
        this.context = context;
        this.orderTypeList = orderTypeList;
        this.onOrderClick = onOrderClick;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/clarendon_bt.ttf");
        lastPosition = -1;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_order_type, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        OrderType orderType = orderTypeList.get(pos);
        Picasso.get()
                .load(Constant.ORDER_TYPE_URL + orderType.getOrderTypeIcon())
                .into(holder.image);

        if (pos == lastPosition) {
            holder.image.setBackground(context.getDrawable(R.drawable.oval_endeavour));
        } else {
            holder.image.setBackground(context.getDrawable(R.drawable.oval_stroke_endeavour));
        }

        holder.text.setText(orderType.getOrderTypeName());
        holder.text.setTypeface(typeface);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPosition = pos;
                onOrderClick.onClick(pos);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderTypeList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView text;

        Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recycler_image_order_type);
            text = itemView.findViewById(R.id.recycler_text_order_type);
        }
    }
}
