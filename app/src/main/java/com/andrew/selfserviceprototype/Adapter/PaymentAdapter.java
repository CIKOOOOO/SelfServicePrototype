package com.andrew.selfserviceprototype.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.andrew.selfserviceprototype.Model.Payment;
import com.andrew.selfserviceprototype.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.Holder> {

    private Context context;
    private List<Payment> paymentList;
    private Typeface typeface;

    public interface choosingPayment {
        void isChoosingPayment(int pos);
    }

    private choosingPayment choosingPayment;

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    public PaymentAdapter(Context context, List<Payment> paymentList, PaymentAdapter.choosingPayment choosingPayment) {
        this.context = context;
        this.paymentList = paymentList;
        this.choosingPayment = choosingPayment;
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
        Payment payment = paymentList.get(position);
        holder.button.setText(payment.getPaymentType());
        holder.button.setTypeface(typeface);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosingPayment.isChoosingPayment(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private Button button;

        Holder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.recycler_type_payment);
        }
    }
}
