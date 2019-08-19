package com.andrew.selfserviceprototype.Activity;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Adapter.ConfirmOrderAdapter;
import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.Model.Transaction;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfirmOrderActivity extends BaseActivity implements View.OnClickListener {
    public static final String CONFIRM_GETTING_MERCHANT_DATA = "confirm_data";
    public static final String CONFIRM_GETTING_QUANTITY_LIST = "quantity_list";
    public static final String CONFIRM_GETTING_ORDER_LIST = "order_list";

    private static final int RESULT = 12;

    private TextView text_price, text_total_price, text_tax;
    private RecyclerView recyclerView;
    private ImageView image_icon;
    private RelativeLayout relative_loading;

    private ConfirmOrderAdapter confirmOrderAdapter;

    private List<Integer> quantityList;
    private List<Product> orderList;
    private long taxAmount, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        initVar();
    }

    private void initVar() {
        taxAmount = 0;
        price = 0;

        Button btn_cancel = findViewById(R.id.btn_cancel_confirm_order);
        Button btn_order = findViewById(R.id.btn_order_confirm_order);
        RelativeLayout linearLayout = findViewById(R.id.ll_confirm_order_02);

        image_icon = findViewById(R.id.image_icon_confirm_order);
        recyclerView = findViewById(R.id.recycler_confirm_order);
        relative_loading = findViewById(R.id.relative_loading_confirm_order);
        text_price = findViewById(R.id.text_price_confirm_order);
        text_total_price = findViewById(R.id.text_total_price_confirm_order);
        text_tax = findViewById(R.id.text_tax_confirm_order);

        quantityList = new ArrayList<>();
        orderList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        confirmOrderAdapter = new ConfirmOrderAdapter(ConfirmOrderActivity.this, orderList, quantityList);

        recyclerView.setAdapter(confirmOrderAdapter);

        linearLayout.getBackground().setAlpha(150);

        btn_cancel.setOnClickListener(this);
        btn_order.setOnClickListener(this);

        new AsyncRunner().execute();
    }

    private void show(View view) {
        view.getBackground().setAlpha(Constant.MAX_ALPHA);
        view.setVisibility(View.VISIBLE);
    }

    private long getTotalPrice(List<Product> products) {
        long totalPrice = 0;
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
                new ContinueRunner().execute();
                break;
            case R.id.btn_cancel_confirm_order:
                setResult(RESULT_OK);
                finish();
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

    private class ContinueRunner extends AsyncTask<Void, Void, Void> {
        String TID = String.valueOf(new Random().nextInt(1000));
        Transaction transaction = new Transaction(TID
                , taxAmount, Utils.getTime("yyyy/MM/dd"), "not_finish");

        List<Transaction.TransactionDetail> details = new ArrayList<>();

        Intent intent;

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < orderList.size(); i++) {
                Product product = orderList.get(i);
                if (product.getProductPrice() == 0) continue;
                Transaction.TransactionDetail transactionDetail = new Transaction.TransactionDetail(TID, product.getMerchantId(), product.getProductId(), product.getProductPrice(), quantityList.get(i));
                details.add(transactionDetail);
            }

            intent = new Intent(ConfirmOrderActivity.this, PaymentActivity.class);
            intent.putParcelableArrayListExtra(PaymentActivity.PAYMENT_GETTING_DATA_LIST, (ArrayList<? extends Parcelable>) details);
            intent.putExtra(PaymentActivity.PAYMENT_GETTING_DATA, transaction);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            show(relative_loading);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivityForResult(intent, RESULT);
        }
    }

    private class AsyncRunner extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            synchronized (this) {
                for (int i = 0; i < StaticData.MERCHANT_LIST.size(); i++) {
                    if (StaticData.PRODUCT_ORDER_MAP.containsKey(StaticData.MERCHANT_LIST.get(i).getMerchantId())) {
                        if (StaticData.PRODUCT_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()).size() > 0) {
                            orderList.add(new Product("", StaticData.MERCHANT_LIST.get(i).getMerchantId(), "", 0, "", ""));
                            quantityList.add(-1);
                            orderList.addAll(StaticData.PRODUCT_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()));
                            quantityList.addAll(StaticData.QUANTITY_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()));
                        }
                    }
                }
                price = getTotalPrice(orderList);
                taxAmount = price / 10;
                confirmOrderAdapter.setList(orderList, quantityList);
                confirmOrderAdapter.notifyDataSetChanged();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            show(relative_loading);
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

//            DecodeBitmap.setScaledImageView((ImageView) findViewById(R.id.image_confirm_order_background), R.drawable.asset_background_grid, ConfirmOrderActivity.this);

            text_price.setText("Sub Total : IDR " + Utils.priceFormat(price) + " ,-");
            text_tax.setText("Tax 10% : IDR " + Utils.priceFormat(taxAmount) + " ,-");
            text_total_price.setText("Total Price : IDR " + Utils.priceFormat(price + taxAmount) + " ,-");
            relative_loading.setVisibility(View.GONE);
        }
    }
}
