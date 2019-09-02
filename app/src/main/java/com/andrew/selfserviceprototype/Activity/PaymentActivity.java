package com.andrew.selfserviceprototype.Activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Adapter.BadRateAdapter;
import com.andrew.selfserviceprototype.Adapter.PaymentAdapter;
import com.andrew.selfserviceprototype.Adapter.RatingAdapter;
import com.andrew.selfserviceprototype.Api.ApiClient;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.Payment;
import com.andrew.selfserviceprototype.Model.Transaction;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
import com.andrew.selfserviceprototype.Utils.PrefConfig;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentActivity extends BaseActivity implements View.OnClickListener
        , PaymentAdapter.choosingPayment, RatingDialogListener, RatingAdapter.starOnClick {
    public static final String PAYMENT_GETTING_DATA = "payment_data";
    public static final String PAYMENT_GETTING_DATA_LIST = "payment_data_list";

    private static final String TAG = PaymentActivity.class.getSimpleName();

    private static boolean isDataSend;

    private RelativeLayout relative_payment, relative_payment_success, relative_repeat_order, relative_rate;
    private ImageView image_qr;
    private TextView text_payment_type, text_rate;
    private LinearLayout linear_improve;

    private Transaction transaction;
    private PaymentAdapter paymentAdapter;
    private ApiInterface apiInterface;
    private PrefConfig prefConfig;
    private RatingAdapter ratingAdapter;
    private BadRateAdapter badRateAdapter;

    private List<String> rateList, badRateList;
    private List<Transaction.TransactionDetail> transactionDetailList;
    private List<Payment> paymentList;
    private String TID;
    private int rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initVar();
    }

    private void initVar() {
        isDataSend = false;
        rate = -1;
        prefConfig = new PrefConfig(this);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        relative_payment = findViewById(R.id.relative_payment);
        image_qr = findViewById(R.id.image_qr_payment);
        text_payment_type = findViewById(R.id.text_payment);
        relative_payment_success = findViewById(R.id.relative_payment_success);
        relative_repeat_order = findViewById(R.id.relative_repeat_order);
        text_rate = findViewById(R.id.text_rate_payment);
        relative_rate = findViewById(R.id.relative_rate_payment);
        linear_improve = findViewById(R.id.ll_box_bad_rate_payment);

        RelativeLayout relative_content_payment = findViewById(R.id.relative_payment_01);
        ImageButton arrow_back = findViewById(R.id.image_button_back_payment);
        RecyclerView recyclerView = findViewById(R.id.recycler_payment);
        Button btn_yes = findViewById(R.id.btn_yes_order_again_payment);
        Button btn_no = findViewById(R.id.btn_no_order_again_payment);
        Button submit_rate = findViewById(R.id.btn_submit_rate_payment);

        rateList = new ArrayList<>(Constant.getRatingList());
        paymentList = new ArrayList<>();
        transactionDetailList = new ArrayList<>();
        badRateList = new ArrayList<>(Constant.getBadRateList());

        paymentAdapter = new PaymentAdapter(this, paymentList, this);
        ratingAdapter = new RatingAdapter(this, rateList, this);
        badRateAdapter = new BadRateAdapter(badRateList, this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(paymentAdapter);

        arrow_back.setOnClickListener(this);
        relative_payment.setOnClickListener(this);
        relative_content_payment.setOnClickListener(this);
        btn_no.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
        submit_rate.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.getParcelableArrayListExtra(PAYMENT_GETTING_DATA_LIST) != null && intent.getParcelableExtra(PAYMENT_GETTING_DATA) != null) {
            transactionDetailList.addAll(intent.<Transaction.TransactionDetail>getParcelableArrayListExtra(PAYMENT_GETTING_DATA_LIST));
            transaction = intent.getParcelableExtra(PAYMENT_GETTING_DATA);
            TID = transaction.getTransactionId();
        }

        new InitRunner().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_back_payment:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.relative_payment:
                isDataSend = false;
                relative_payment.setVisibility(View.GONE);
                break;
            case R.id.btn_yes_order_again_payment:
                setResult(1);
                finish();
                break;
            case R.id.btn_no_order_again_payment:
                finishAffinity();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btn_submit_rate_payment:
                if (rate > 0) {
                    Call<Transaction> call = apiInterface.updateFeedback("update_transaction", TID, rate);
                    call.enqueue(new Callback<Transaction>() {
                        @Override
                        public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                            if (response.body().getResponse().equals("ok")) {
                                relative_repeat_order.setVisibility(View.VISIBLE);
                            } else {
                                Log.e(TAG, "HOW COME DUDE?");
                            }
                        }

                        @Override
                        public void onFailure(Call<Transaction> call, Throwable t) {
                            Log.e(TAG, "NEED TO CHECK YOUR INTERNET CONNECTION OR MAYBE MY CODE IS SUCKS");
                        }
                    });
                }
                break;
        }
    }

    private long getTotalPrice() {
        long price = 0;
        for (Transaction.TransactionDetail transactionDetail : transactionDetailList) {
            price += transactionDetail.getUnitPrice() * transactionDetail.getQuantity();
        }
        return price;
    }

    @Override
    public void isChoosingPayment(final int pos) {
        relative_payment.setVisibility(View.VISIBLE);
        relative_payment.getBackground().setAlpha(Constant.MAX_ALPHA);

        switch (paymentList.get(pos).getPaymentId()) {
            case "1":
                isDataSend = true;
                image_qr.setVisibility(View.VISIBLE);
                DecodeBitmap.setScaledImageView(image_qr, R.drawable.asset_qr, this);
                text_payment_type.setText("Please scan the barcode");
                break;
            case "2":
                isDataSend = true;
                image_qr.setVisibility(View.GONE);
                text_payment_type.setText(getResources().getString(R.string.order_qr_reader));
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDataSend) {
                    sendData(pos);
                }
            }
        }, Constant.MAX_DURATION_PAYMENT);
    }

    private void sendData(final int pos) {
        Call<Transaction> call = apiInterface.sendTransaction("transaction", transaction.getTransactionId()
                , transaction.getTaxAmount(), Utils.getTime("dd/MM/yyyy")
                , Utils.getTime("HH:mm"), paymentList.get(pos).getPaymentId(), StaticData.ORDER_TYPE.getOrderTypeId(), transaction.getOrderStatus());

        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.body().getResponse().equals("ok")) {
                    relative_payment.setVisibility(View.GONE);
                    relative_payment_success.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            relative_payment_success.setVisibility(View.GONE);

                            RecyclerView recyclerView = findViewById(R.id.recycler_rating_payment);
                            recyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this, RecyclerView.HORIZONTAL, false));
                            recyclerView.setAdapter(ratingAdapter);

//                            relative_rate.setBackground(getResources().getDrawable(R.drawable.asset_background_food));
                            relative_rate.setVisibility(View.VISIBLE);
                        }
                    }, 3000);
                } else {
                    Log.e(TAG, "Send Transaction is failed, please check connection or check your php code!!");
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Log.e(TAG, "We got situation here, SEND DATA IS GOING CRAZY!!");
            }
        });

        for (int i = 0; i < transactionDetailList.size(); i++) {
            Transaction.TransactionDetail tran = transactionDetailList.get(i);
            Call<Transaction.TransactionDetail> transactionDetailCall
                    = apiInterface.sendTransactionDetail("transaction_detail", transaction.getTransactionId()
                    , tran.getMerchantId(), tran.getProductId(), tran.getUnitPrice(), tran.getQuantity());

            transactionDetailCall.enqueue(new Callback<Transaction.TransactionDetail>() {
                @Override
                public void onResponse(Call<Transaction.TransactionDetail> call, Response<Transaction.TransactionDetail> response) {
                    if (response.body().getResponse().equals("ok")) {
                        Log.i(TAG, "Detail of transaction is already SEND. GOOD NEWS FOR EVERYONE!");
                        StaticData.PRODUCT_ORDER_MAP.clear();
                        StaticData.QUANTITY_ORDER_MAP.clear();

                        Log.e("asd", StaticData.PRODUCT_ORDER_MAP.toString());
                    } else {
                        Log.e(TAG, "THIS IS NOT GOOD EVERYONE, CHECK EVERYTHING BECAUSE IT'S MONEY WE TALKING ABOUT!!");
                    }
                }

                @Override
                public void onFailure(Call<Transaction.TransactionDetail> call, Throwable t) {
                    Log.e(TAG, "Send transaction detail FAILED");
                }
            });
        }
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {

    }

    @Override
    public void clickRate(int pos) {
        linear_improve.clearAnimation();
        ratingAdapter.setLastPosition(pos);
        ratingAdapter.notifyDataSetChanged();
        text_rate.setText(rateList.get(pos));
        if (pos <= 3) {
            if (linear_improve.getVisibility() == View.GONE) {
                linear_improve.setVisibility(View.VISIBLE);
                linear_improve.setAlpha(0.0f);
                linear_improve.animate()
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                linear_improve.setVisibility(View.VISIBLE);
                            }
                        });

                RecyclerView recycler_bad_rate = findViewById(R.id.recycler_bad_rate_payment);
                recycler_bad_rate.setLayoutManager(new GridLayoutManager(this, 3));
                recycler_bad_rate.setAdapter(badRateAdapter);
            }
        } else {
            linear_improve.animate()
                    .alpha(0.0f)
                    .setDuration(1000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            linear_improve.setVisibility(View.GONE);
                        }
                    });
        }
        rate = ++pos;
    }

    private class InitRunner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Picasso.get()
                    .load(R.drawable.asset_background_food)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into((ImageView) findViewById(R.id.image_payment_background));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Call<Payment> call = apiInterface.getPaymentType("payment_type");
            call.enqueue(new Callback<Payment>() {
                @Override
                public void onResponse(Call<Payment> call, Response<Payment> response) {
                    if (response.body().getResponse().equals("ok")) {
                        if (response.body().getPaymentList() != null) {
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
}
