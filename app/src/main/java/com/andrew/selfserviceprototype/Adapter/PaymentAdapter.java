package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Model.Payment;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.Holder> {

    private Context context;
    private List<Payment> paymentList;
    private Typeface typeface;
    private int lastPosition;

    public interface choosingPayment {
        void isChoosingPayment(int pos);
    }

    private choosingPayment choosingPayment;

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    public PaymentAdapter(Context context, List<Payment> paymentList, PaymentAdapter.choosingPayment choosingPayment) {
        this.context = context;
        this.paymentList = paymentList;
        this.choosingPayment = choosingPayment;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/clarendon_bt.ttf");
        lastPosition = -1;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_payment, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final int pos = holder.getAdapterPosition();
        Payment payment = paymentList.get(pos);

        Picasso.get()
                .load(Constant.PAYMENT_URL + payment.getPaymentIcon())
                .into(holder.image);

        if (pos == lastPosition) {
            holder.image.setBackground(context.getDrawable(R.drawable.oval_endeavour));
        } else {
            holder.image.setBackground(context.getDrawable(R.drawable.oval_stroke_endeavour));
        }

        holder.text.setText(payment.getPaymentType());
        holder.text.setTypeface(typeface);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPosition = pos;
                choosingPayment.isChoosingPayment(pos);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView text;

        Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recycler_image_payment);
            text = itemView.findViewById(R.id.recycler_text_payment);
        }
    }
}
