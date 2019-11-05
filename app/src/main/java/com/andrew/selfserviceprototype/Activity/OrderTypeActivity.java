package com.andrew.selfserviceprototype.Activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.andrew.selfserviceprototype.Adapter.OrderTypeAdapter;
import com.andrew.selfserviceprototype.Adapter.PaymentAdapter;
import com.andrew.selfserviceprototype.Api.ApiClient;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.OrderType;
import com.andrew.selfserviceprototype.Model.Payment;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.PrefConfig;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderTypeActivity extends BaseActivity implements OrderTypeAdapter.onOrderClick, PaymentAdapter.choosingPayment, View.OnClickListener {

    private static final String TAG = OrderTypeActivity.class.getSimpleName();

    private ImageView image_background;
    private Button btn_next;

    private ApiInterface apiInterface;
    private OrderTypeAdapter orderTypeAdapter;
    private PrefConfig prefConfig;
    private PaymentAdapter paymentAdapter;

    private List<OrderType> orderTypeList;
    private List<Payment> paymentList;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_type);
        initVar();
//        setAppIdleTimeout();
    }

    private void initVar() {
        prefConfig = new PrefConfig(this);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        image_background = findViewById(R.id.image_food_type);
        btn_next = findViewById(R.id.btn_next_order_type);

        RecyclerView recycler_order_type = findViewById(R.id.recycler_order_type);
        RecyclerView recycler_payment = findViewById(R.id.recycler_payment);

        paymentList = new ArrayList<>();
        orderTypeList = new ArrayList<>();

        orderTypeAdapter = new OrderTypeAdapter(this, orderTypeList, this);
        paymentAdapter = new PaymentAdapter(this, paymentList, this);

        recycler_order_type.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_order_type.setAdapter(orderTypeAdapter);

        recycler_payment.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_payment.setAdapter(paymentAdapter);

        btn_next.setEnabled(false);
        btn_next.setBackground(getDrawable(R.drawable.rectangle_rounded_bombay));
        btn_next.setTextColor(getResources().getColor(R.color.iron_palette));

        btn_next.setOnClickListener(this);

        new InitRunner().execute();
    }

    @Override
    public void onClick(final int pos) {
        if (paymentAdapter.getLastPosition() != -1 && orderTypeAdapter.getLastPosition() != -1) {
            btn_next.setEnabled(true);
            btn_next.setBackground(getDrawable(R.drawable.selection_athens_iron));
            btn_next.setTextColor(getResources().getColor(R.color.cerulean_palette));
        } else {
            btn_next.setEnabled(false);
            btn_next.setBackground(getDrawable(R.drawable.rectangle_rounded_bombay));
            btn_next.setTextColor(getResources().getColor(R.color.iron_palette));
        }
        StaticData.ORDER_TYPE = orderTypeList.get(pos);

//        Call<MerchantData> call = apiInterface.getMerchantData("ads_promo");
//
//        call.enqueue(new Callback<MerchantData>() {
//            @Override
//            public void onResponse(Call<MerchantData> call, Response<MerchantData> response) {
//                if (response.body().getMerchantDataList() != null) {
//                    prefConfig.saveOrderType(orderTypeList.get(pos).getOrderTypeId(), orderTypeList.get(pos).getOrderTypeName());
//                    Intent intent = new Intent(OrderTypeActivity.this, MerchantListActivity.class);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        intent.putParcelableArrayListExtra(MerchantListActivity.GETTING_MERCHANT_DATA, (ArrayList<? extends Parcelable>) response.body().getMerchantDataList());
//                    }
//                    startActivity(intent);
//                } else {
//                    Log.e(TAG, "Merchant Data is null!!! CALL EMERGENCY!!!");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MerchantData> call, Throwable t) {
//                Log.e(TAG, "MAYDAY MAYDAY, WE GOT SITUATION HERE \\N BACKUP IS NEEDED!!!!!!!");
//            }
//        });
    }

    @Override
    public void isChoosingPayment(int pos) {
        if (paymentAdapter.getLastPosition() != -1 && orderTypeAdapter.getLastPosition() != -1) {
            btn_next.setEnabled(true);
            btn_next.setBackground(getDrawable(R.drawable.selection_athens_iron));
            btn_next.setTextColor(getResources().getColor(R.color.cerulean_palette));
        } else {
            btn_next.setEnabled(false);
            btn_next.setBackground(getDrawable(R.drawable.rectangle_rounded_bombay));
            btn_next.setTextColor(getResources().getColor(R.color.iron_palette));
        }
        StaticData.PAYMENT = paymentList.get(pos);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_order_type:
                PrefConfig prefConfig = new PrefConfig(this);
                prefConfig.setTutorial(false);
                Intent intent = new Intent(OrderTypeActivity.this, MerchantListActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class InitRunner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Picasso.get()
                    .load(R.drawable.asset_background_food)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(image_background);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Call<OrderType> call = apiInterface.getOrderType("order_type");
            call.enqueue(new Callback<OrderType>() {
                @Override
                public void onResponse(Call<OrderType> call, Response<OrderType> response) {
                    if (response.body().getOrderTypeList() != null) {
                        orderTypeList = response.body().getOrderTypeList();
                        orderTypeAdapter.setOrderTypeList(orderTypeList);
                        orderTypeAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "DATA IS NULL, HOW??");
                    }
                }

                @Override
                public void onFailure(Call<OrderType> call, Throwable t) {
                    Log.e(TAG, "We got situation here dude, please check it out!");
                }
            });

            Call<Payment> call2 = apiInterface.getPaymentType("payment_type");
            call2.enqueue(new Callback<Payment>() {
                @Override
                public void onResponse(Call<Payment> call, Response<Payment> response) {
                    if (response.body().getResponse().equals("ok")) {
                        if (response.body().getPaymentList() != null) {
                            StaticData.PAYMENT_LIST.clear();
                            StaticData.PAYMENT_LIST.addAll(response.body().getPaymentList());
                            paymentList = response.body().getPaymentList();
                            paymentAdapter.setPaymentList(paymentList);
                            paymentAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Data is null, check database");
                    }
                }

                @Override
                public void onFailure(Call<Payment> call, Throwable t) {
                    Log.e(TAG, "Got failure in here dude, please check it out!");
                }
            });
            return null;
        }
    }

    private void setAppIdleTimeout() {

        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Navigate to main activity
                        finish();
                    }
                });
            }
        };
        handler.postDelayed(runnable, Constant.MAX_DURATION_IDLE);
    }

    //reset timer on user interaction and in onResume
    public void resetAppIdleTimeout() {
//        handler.removeCallbacks(runnable);
//        handler.postDelayed(runnable, Constant.MAX_DURATION_IDLE);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        resetAppIdleTimeout();
    }

    @Override
    public void onUserInteraction() {
//        resetAppIdleTimeout();
        super.onUserInteraction();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
//        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
