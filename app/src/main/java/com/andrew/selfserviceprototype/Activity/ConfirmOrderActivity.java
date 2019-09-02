package com.andrew.selfserviceprototype.Activity;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Adapter.BadRateAdapter;
import com.andrew.selfserviceprototype.Adapter.ConfirmOrderAdapter;
import com.andrew.selfserviceprototype.Adapter.PaymentAdapter;
import com.andrew.selfserviceprototype.Adapter.RatingAdapter;
import com.andrew.selfserviceprototype.Api.ApiClient;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.Model.Transaction;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrderActivity extends BaseActivity implements View.OnClickListener
        , PaymentAdapter.choosingPayment, RatingAdapter.starOnClick, ConfirmOrderAdapter.addDeleteOrder, ConfirmOrderAdapter.menuOnDelete {
    public static final String CONFIRM_GETTING_MERCHANT_DATA = "confirm_data";
    public static final String CONFIRM_GETTING_QUANTITY_LIST = "quantity_list";
    public static final String CONFIRM_GETTING_ORDER_LIST = "order_list";

    private static final String TAG = ConfirmOrderActivity.class.getSimpleName();

    private static final int RESULT = 12;

    private TextView text_price, text_total_price, text_tax, text_payment_type, text_rate, text_title_discount, text_discount;
    private RecyclerView recyclerView, recycler_payment;
    private ImageView image_icon;
    private RelativeLayout relative_loading, relative_success, relative_rate, relative_payment;
    private NestedScrollView nested_payment;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linear_improve;
    private Button btn_order;

    private ConfirmOrderAdapter confirmOrderAdapter;
    private ApiInterface apiInterface;
    private RatingAdapter ratingAdapter;
    private BadRateAdapter badRateAdapter;

    private List<String> rateList, badRateList;
    private List<Integer> quantityList;
    private List<Product> orderList;
    private double taxAmount, price, totalDiscount;
    private int rate;
    private String TID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        initVar();
    }

    private void initVar() {
        taxAmount = 0;
        price = 0;
        totalDiscount = 0;
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Button btn_cancel = findViewById(R.id.btn_cancel_confirm_order);
        RelativeLayout linearLayout = findViewById(R.id.ll_confirm_order_02);
        Button btn_change_payment = findViewById(R.id.btn_change_payment_confirm_order);
        ImageButton img_btn_close = findViewById(R.id.img_btn_close_payment_sheet);
        Button btn_submit_rate = findViewById(R.id.btn_submit_rate_confirm_order);
        TextView text_skip = findViewById(R.id.text_skip_rate_confirm_order);

        btn_order = findViewById(R.id.btn_order_confirm_order);
        image_icon = findViewById(R.id.image_icon_confirm_order);
        recyclerView = findViewById(R.id.recycler_confirm_order);
        relative_loading = findViewById(R.id.relative_loading_confirm_order);
        text_price = findViewById(R.id.text_price_confirm_order);
        text_total_price = findViewById(R.id.text_total_price_confirm_order);
        text_tax = findViewById(R.id.text_tax_confirm_order);
        text_payment_type = findViewById(R.id.text_payment_confirm_order);
        nested_payment = findViewById(R.id.nested_bottom_sheet_payment);
        recycler_payment = findViewById(R.id.recycler_payment_sheet);
        relative_success = findViewById(R.id.relative_payment_success);
        relative_rate = findViewById(R.id.relative_rate_confirm_order);
        linear_improve = findViewById(R.id.ll_box_bad_rate_confirm_order);
        text_rate = findViewById(R.id.text_rate_confirm_order);
        text_discount = findViewById(R.id.text_discount_confirm_order);
        text_title_discount = findViewById(R.id.text_discount_title_confirm_order);
        relative_payment = findViewById(R.id.relative_payment_confirm_order);

        quantityList = new ArrayList<>();
        orderList = new ArrayList<>();
        rateList = new ArrayList<>(Constant.getRatingList());
        badRateList = new ArrayList<>(Constant.getBadRateList());

        bottomSheetBehavior = BottomSheetBehavior.from(nested_payment);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        confirmOrderAdapter = new ConfirmOrderAdapter(ConfirmOrderActivity.this, orderList
                , quantityList, this, this);
        ratingAdapter = new RatingAdapter(ConfirmOrderActivity.this, rateList, this);
        badRateAdapter = new BadRateAdapter(badRateList, this);

        recyclerView.setAdapter(confirmOrderAdapter);

        linearLayout.getBackground().setAlpha(150);

        btn_cancel.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_change_payment.setOnClickListener(this);
        img_btn_close.setOnClickListener(this);
        btn_submit_rate.setOnClickListener(this);
        text_skip.setOnClickListener(this);
        relative_payment.setOnClickListener(this);
        relative_success.setOnClickListener(this);
        relative_rate.setOnClickListener(this);

        new AsyncRunner().execute();
    }

    private void show(View view) {
        view.getBackground().setAlpha(Constant.MAX_ALPHA);
        view.setVisibility(View.VISIBLE);
    }

    private double getTotalPrice(List<Product> products) {
        double totalPrice = 0;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getMerchantId().isEmpty()) {
                continue;
            }
            totalPrice += products.get(i).getProductPrice() * quantityList.get(i);
        }
        return totalPrice;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_order_confirm_order:
//                relative_payment.getBackground().setAlpha(Constant.MAX_ALPHA);
                relative_payment.setVisibility(View.VISIBLE);
                ImageView image_qr = findViewById(R.id.image_qr_payment_confirm_order);
                TextView text = findViewById(R.id.text_payment_type_confirm_order);
                switch (StaticData.PAYMENT.getPaymentId()) {
                    case "1":
                        Picasso.get()
                                .load(R.drawable.asset_qr)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(image_qr);
                        text.setText("Please scan the barcode");
                        break;
                    case "2":
                        Picasso.get()
                                .load(Constant.PAYMENT_URL + StaticData.PAYMENT.getPaymentIcon())
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(image_qr);
                        text.setText(getResources().getString(R.string.order_qr_reader));
                        break;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendData();
                    }
                }, Constant.MAX_DURATION_PAYMENT);

                break;
            case R.id.btn_cancel_confirm_order:
                setResult(RESULT_OK);
                StaticData.PRODUCT_ORDER_MAP.clear();
                StaticData.QUANTITY_ORDER_MAP.clear();

                for (int j = 0; j < StaticData.MERCHANT_LIST.size(); j++) {
                    List<Product> list = new ArrayList<>();
                    List<Integer> integerList = new ArrayList<>();

                    for (int i = 0; i < orderList.size(); i++) {
                        if (quantityList.get(i) == -1)
                            continue;

                        if (orderList.get(i).getMerchantId().equals(StaticData.MERCHANT_LIST.get(j).getMerchantId())) {
                            list.add(orderList.get(i));
                            integerList.add(quantityList.get(i));
                        }
                    }

                    if (list.size() > 0) {
                        StaticData.PRODUCT_ORDER_MAP.put(StaticData.MERCHANT_LIST.get(j).getMerchantId(), list);
                        StaticData.QUANTITY_ORDER_MAP.put(StaticData.MERCHANT_LIST.get(j).getMerchantId(), integerList);
                    }
                }
                finish();
                break;
            case R.id.btn_change_payment_confirm_order:
                PaymentAdapter paymentAdapter = new PaymentAdapter(this, StaticData.PAYMENT_LIST, this);
                for (int i = 0; i < StaticData.PAYMENT_LIST.size(); i++) {
                    if (StaticData.PAYMENT_LIST.get(i).getPaymentId().equals(StaticData.PAYMENT.getPaymentId())) {
                        paymentAdapter.setLastPosition(i);
                        break;
                    }
                }
                recycler_payment.setLayoutManager(new GridLayoutManager(this, 2));
                recycler_payment.setAdapter(paymentAdapter);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_btn_close_payment_sheet:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            case R.id.btn_submit_rate_confirm_order:
                if (rate > 0) {
                    Call<Transaction> call = apiInterface.updateFeedback("update_transaction", TID, rate);
                    call.enqueue(new Callback<Transaction>() {
                        @Override
                        public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                            if (response.body().getResponse().equals("ok")) {
//                                relative_repeat_order.setVisibility(View.VISIBLE);
                            } else {
                                Log.e(TAG, "HOW COME DUDE?");
                            }
                            relative_rate.setVisibility(View.GONE);
                            finishAffinity();
                            startActivity(new Intent(ConfirmOrderActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(Call<Transaction> call, Throwable t) {
                            Log.e(TAG, "NEED TO CHECK YOUR INTERNET CONNECTION OR MAYBE MY CODE IS SUCKS");
                        }
                    });
                }
                break;
            case R.id.text_skip_rate_confirm_order:
                relative_rate.setVisibility(View.GONE);
                finishAffinity();
                startActivity(new Intent(ConfirmOrderActivity.this, MainActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT) {
                relative_loading.setVisibility(View.GONE);
            }
        } else if (resultCode == 1) {
            setResult(1);
            finish();
        }
    }

    @Override
    public void isChoosingPayment(int pos) {
        StaticData.PAYMENT = StaticData.PAYMENT_LIST.get(pos);
        text_payment_type.setText("Payment with " + StaticData.PAYMENT.getPaymentType());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

    @Override
    public void onAddDeleteOrder(int pos, int quantity, boolean isDelete) {
        quantityList.set(pos, quantity);
        totalPriceQuantity();
    }

    @Override
    public void menuOnDeletes(final int pos) {
        quantityList.remove(pos);
        orderList.remove(pos);
        totalPriceQuantity();

        /*
         * Kondisi dibawah ini untuk melakukan cek pada header setiap merchant
         * Sehingga saat salah satu item pesanan dihapus, headernya hilang
         * */

        if (pos < quantityList.size() - 1) {
            if (quantityList.get(pos - 1) == -1 && quantityList.get(pos) == -1) {
                quantityList.remove(pos - 1);
                orderList.remove(pos - 1);
            }
        }

        if (orderList.size() == 0) {
            btn_order.setEnabled(false);
            btn_order.setTextColor(getResources().getColor(R.color.iron_palette));
            btn_order.setBackground(getResources().getDrawable(R.drawable.rectangle_rounded_bombay));
        } else {
            btn_order.setTextColor(getResources().getColor(R.color.cerulean_palette));
            btn_order.setBackground(getResources().getDrawable(R.drawable.selection_athens_iron));
            btn_order.setEnabled(true);
        }

        confirmOrderAdapter.setList(orderList, quantityList);

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                confirmOrderAdapter.notifyDataSetChanged();
            }
        });
    }

    private void totalPriceQuantity() {
        price = 0;
        totalDiscount = 0;
        taxAmount = 0;

        for (Product product : orderList) {
            if (product.getDiscount() > 0) {
                totalDiscount += product.getProductPrice() * product.getDiscount();
            }
        }

        price = getTotalPrice(orderList);
        taxAmount = price / 10;

        text_price.setText(" : IDR " + Utils.priceFormat(price) + " ,-");
        text_tax.setText(" : IDR " + Utils.priceFormat(taxAmount) + " ,-");

        text_total_price.setText(" : IDR " + Utils.priceFormat(price + taxAmount - totalDiscount) + " ,-");

        if (totalDiscount > 0) {
            text_title_discount.setVisibility(View.VISIBLE);
            text_discount.setVisibility(View.VISIBLE);
            text_discount.setText(" : - IDR " + Utils.priceFormat(totalDiscount) + " ,-");
        } else {
            text_discount.setVisibility(View.GONE);
            text_title_discount.setVisibility(View.GONE);
        }
    }

    private void sendData() {
        TID = String.valueOf(new Random().nextInt(100000));
        Call<Transaction> call = apiInterface.sendTransaction("transaction", TID
                , taxAmount, Utils.getTime("dd/MM/yyyy")
                , Utils.getTime("HH:mm"), StaticData.PAYMENT.getPaymentId()
                , StaticData.ORDER_TYPE.getOrderTypeId(), "not_finish");

        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.body().getResponse().equals("ok")) {
                    relative_payment.setVisibility(View.GONE);
                    relative_success.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            relative_success.setVisibility(View.GONE);

                            RecyclerView recyclerView = findViewById(R.id.recycler_rating_confirm_order);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ConfirmOrderActivity.this, RecyclerView.HORIZONTAL, false));
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

        for (String key : StaticData.PRODUCT_ORDER_MAP.keySet()) {
            for (int i = 0; i < StaticData.PRODUCT_ORDER_MAP.get(key).size(); i++) {
                Product product = StaticData.PRODUCT_ORDER_MAP.get(key).get(i);
                int qty = StaticData.QUANTITY_ORDER_MAP.get(key).get(i);

                Call<Transaction.TransactionDetail> transactionDetailCall
                        = apiInterface.sendTransactionDetail("transaction_detail", TID
                        , product.getMerchantId(), product.getProductId(), product.getProductPrice(), qty);

                transactionDetailCall.enqueue(new Callback<Transaction.TransactionDetail>() {
                    @Override
                    public void onResponse(Call<Transaction.TransactionDetail> call, Response<Transaction.TransactionDetail> response) {
                        if (response.body().getResponse().equals("ok")) {
                            Log.i(TAG, "Detail of transaction is already SEND. GOOD NEWS FOR EVERYONE!");
                            StaticData.PRODUCT_ORDER_MAP.clear();
                            StaticData.QUANTITY_ORDER_MAP.clear();
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
    }

    private class AsyncRunner extends AsyncTask<Void, Void, Void> {
        TextView text_order_type;
        ImageView image_icon_order_type;

        @Override
        protected Void doInBackground(Void... voids) {
            synchronized (this) {
                for (int i = 0; i < StaticData.MERCHANT_LIST.size(); i++) {
                    if (StaticData.PRODUCT_ORDER_MAP.containsKey(StaticData.MERCHANT_LIST.get(i).getMerchantId())) {
                        if (StaticData.PRODUCT_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()).size() > 0) {
                            orderList.add(new Product("", StaticData.MERCHANT_LIST.get(i).getMerchantId(), "", 0, "", "", false, "", "", "", 0));
                            quantityList.add(-1);
                            orderList.addAll(StaticData.PRODUCT_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()));
                            quantityList.addAll(StaticData.QUANTITY_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()));
                        }
                    }
                }

                for (int i = 0; i < quantityList.size(); i++) {
                    if (quantityList.get(i) == 0) {
                        orderList.remove(i);
                        quantityList.remove(i);
                    }
                }

                confirmOrderAdapter.setList(orderList, quantityList);
                confirmOrderAdapter.notifyDataSetChanged();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            show(relative_loading);
            text_order_type = findViewById(R.id.text_order_type_confirm_order);
            image_icon_order_type = findViewById(R.id.image_icon_order_type);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

//            Picasso.get()
//                    .load(Constant.URL + merchantData.getMerchantIcon())
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .into(image_icon);

            Picasso.get()
                    .load(R.drawable.asset_background_grid)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into((ImageView) findViewById(R.id.image_confirm_order_background));

            Picasso.get()
                    .load(Constant.ORDER_TYPE_URL + StaticData.ORDER_TYPE.getOrderTypeIcon())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(image_icon_order_type);

//            DecodeBitmap.setScaledImageView((ImageView) findViewById(R.id.image_confirm_order_background), R.drawable.asset_background_grid, ConfirmOrderActivity.this);

            text_order_type.setText("Order Type : " + StaticData.ORDER_TYPE.getOrderTypeName());
            text_payment_type.setText("Payment with " + StaticData.PAYMENT.getPaymentType());

            totalPriceQuantity();

            relative_loading.setVisibility(View.GONE);
        }
    }
}
