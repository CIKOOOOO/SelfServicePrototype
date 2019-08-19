package com.andrew.selfserviceprototype.Activity;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.selfserviceprototype.Adapter.MenuAdapter;
import com.andrew.selfserviceprototype.Adapter.MyOrderAdapter;
import com.andrew.selfserviceprototype.Api.ApiClient;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends BaseActivity implements MenuAdapter.menuOnClick
        , View.OnClickListener, MyOrderAdapter.addDeleteOrder, MyOrderAdapter.deleteOrder {
    public static final String MERCHANT_DATA = "merchant_data";

    private static final String TAG = OrderActivity.class.getSimpleName();
    private static final int RESULT = 12;

    private List<Product> myOrderList;
    private List<Integer> quantityList;

    private FrameLayout frame_order;
    private RelativeLayout relative_loading;
    private TextView text_quantity, text_frame_price, text_total_price;
    private Button btn_continue;
    private ImageView imageView;

    private MenuAdapter menuAdapter;
    private MyOrderAdapter myOrderAdapter;
    private MerchantData merchantData;
    private ApiInterface apiInterface;

    private List<Product> productList;

    private long unitPrice, totalPrice;
    private int totalQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initVar();
    }

    private void initVar() {
        totalQuantity = 0;
        unitPrice = 0;
        totalPrice = 0;

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        RecyclerView recycler_menu = findViewById(R.id.recycler_menu_order);
        RecyclerView recycler_my_order = findViewById(R.id.recycler_my_order);
        Button btn_cancel = findViewById(R.id.btn_cancel_order);
        Button btn_order_again = findViewById(R.id.btn_order_again_order);

        imageView = findViewById(R.id.image_promo_order);
        btn_continue = findViewById(R.id.btn_continue_order);
        relative_loading = findViewById(R.id.relative_order_loading);
        frame_order = findViewById(R.id.frame_order);
        text_quantity = findViewById(R.id.text_quantity_product_frame_order);
        text_frame_price = findViewById(R.id.text_price_frame_order);
        text_total_price = findViewById(R.id.text_total_price);

        productList = new ArrayList<>();
        myOrderList = new ArrayList<>();
        quantityList = new ArrayList<>();
        menuAdapter = new MenuAdapter(this, productList, this);
        myOrderAdapter = new MyOrderAdapter(this, productList, quantityList, this, this);

        text_total_price.setText("Total Price : IDR " + Utils.priceFormat(totalPrice) + ",- (" + totalQuantity + " pcs)");

        btn_continue.setEnabled(false);
        btn_continue.setTextColor(getResources().getColor(R.color.iron_palette));
        btn_continue.setBackground(getResources().getDrawable(R.drawable.rectangle_rounded_bombay));

        recycler_my_order.setLayoutManager(new LinearLayoutManager(this));
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 3));

        recycler_menu.setAdapter(menuAdapter);
        recycler_my_order.setAdapter(myOrderAdapter);

        btn_cancel.setOnClickListener(this);
        btn_continue.setOnClickListener(this);
        btn_continue.setEnabled(false);
        relative_loading.setOnClickListener(this);
        btn_order_again.setOnClickListener(this);

        new AsyncTaskRunner().execute();
    }

    @Override
    public void onMenuClick(final int pos) {
        boolean isOrder = false;
        final Product product = productList.get(pos);
        int position = -1;

        TextView title = findViewById(R.id.text_title_frame_order);
        ImageView imageView = findViewById(R.id.image_order);
        Button cancel = findViewById(R.id.btn_cancel_frame_order);
        Button add = findViewById(R.id.btn_add_frame_order);
        ImageButton plus = findViewById(R.id.btn_plus_frame_order);
        ImageButton minus = findViewById(R.id.btn_minus_frame_order);

        unitPrice = product.getProductPrice();
        text_quantity.setText("1");

        for (int i = 0; i < myOrderList.size(); i++) {
            if (myOrderList.get(i).getProductId().equals(product.getProductId())) {
                text_quantity.setText(quantityList.get(i) + "");
                position = i;
                isOrder = true;
                break;
            }
        }

        text_frame_price.setText("Price : IDR " + Utils.priceFormat(product.getProductPrice() * Integer.parseInt(text_quantity.getText().toString())));
        Picasso.get().load(Constant.PRODUCT_URL + product.getProductImage()).into(imageView);
        title.setText(product.getProductName());

        show(frame_order);

        frame_order.setOnClickListener(this);
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        cancel.setOnClickListener(this);

        final boolean finalIsOrder = isOrder;
        final int finalPosition = position;

        if (isOrder) {
            add.setText("Update");
        } else {
            add.setText("Add");
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalIsOrder) {
                    myOrderList.set(finalPosition, product);
                    quantityList.set(finalPosition, Integer.parseInt(text_quantity.getText().toString()));
                } else {
                    myOrderList.add(product);
                    quantityList.add(Integer.parseInt(text_quantity.getText().toString()));
                }
                totalQuantity += Integer.parseInt(text_quantity.getText().toString());
                totalPrice += product.getProductPrice() * Integer.parseInt(text_quantity.getText().toString());

                btn_continue.setTextColor(getResources().getColor(R.color.cerulean_palette));
                btn_continue.setBackground(getResources().getDrawable(R.drawable.selection_athens_iron));
                btn_continue.setEnabled(true);

                frame_order.setVisibility(View.GONE);

                myOrderAdapter.setList(myOrderList, quantityList);
                myOrderAdapter.notifyDataSetChanged();
                totalPriceQuantity();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_frame_order:
                frame_order.setVisibility(View.GONE);
                break;
            case R.id.btn_cancel_order:
                new AlertDialog
                        .Builder(view.getContext())
                        .setTitle("Are you sure? This " + merchantData.getMerchantName() + " cart will be empty")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (StaticData.PRODUCT_ORDER_MAP.containsKey(merchantData.getMerchantId())) {
                                    StaticData.PRODUCT_ORDER_MAP.remove(merchantData.getMerchantId());
                                    StaticData.QUANTITY_ORDER_MAP.remove(merchantData.getMerchantId());
                                }
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                break;
            case R.id.btn_continue_order:
                if (StaticData.PRODUCT_ORDER_MAP.containsKey(merchantData.getMerchantId())) {
                    StaticData.PRODUCT_ORDER_MAP.put(merchantData.getMerchantId(), myOrderList);
                    StaticData.QUANTITY_ORDER_MAP.put(merchantData.getMerchantId(), quantityList);
                } else {
                    StaticData.PRODUCT_ORDER_MAP.put(merchantData.getMerchantId(), myOrderList);
                    StaticData.QUANTITY_ORDER_MAP.put(merchantData.getMerchantId(), quantityList);
                }
                new ContinueRunner().execute();
                break;
            case R.id.btn_order_again_order:
                if (StaticData.PRODUCT_ORDER_MAP.containsKey(merchantData.getMerchantId())) {
                    if (myOrderList.size() > 0) {
                        StaticData.PRODUCT_ORDER_MAP.put(merchantData.getMerchantId(), myOrderList);
                        StaticData.QUANTITY_ORDER_MAP.put(merchantData.getMerchantId(), quantityList);
                    } else {
                        StaticData.PRODUCT_ORDER_MAP.remove(merchantData.getMerchantId());
                        StaticData.QUANTITY_ORDER_MAP.remove(merchantData.getMerchantId());
                    }
                } else {
                    StaticData.PRODUCT_ORDER_MAP.put(merchantData.getMerchantId(), myOrderList);
                    StaticData.QUANTITY_ORDER_MAP.put(merchantData.getMerchantId(), quantityList);
                }

                finish();
                break;
            case R.id.btn_plus_frame_order:
                text_quantity.setText(Integer.parseInt(text_quantity.getText().toString()) + 1 + "");
                long plus_quantity = Long.parseLong(text_quantity.getText().toString());
                text_frame_price.setText("Price : IDR " + Utils.priceFormat(unitPrice * plus_quantity));
                break;
            case R.id.btn_minus_frame_order:
                if (Integer.parseInt(text_quantity.getText().toString()) > 1) {
                    text_quantity.setText(Integer.parseInt(text_quantity.getText().toString()) - 1 + "");
                    long minus_quantity = Long.parseLong(text_quantity.getText().toString());
                    text_frame_price.setText("Price : IDR " + Utils.priceFormat(unitPrice * minus_quantity));
                }
                break;
        }
    }

    @Override
    public void onAddDeleteOrder(int pos, int quantity, boolean isDelete) {
        quantityList.set(pos, quantity);
        totalPriceQuantity();
    }

    @Override
    public void onDelete(int pos) {
        totalPrice -= myOrderList.get(pos).getProductPrice() * quantityList.get(pos);
        quantityList.remove(pos);
        myOrderList.remove(pos);
        if (myOrderList.size() == 0) {
            btn_continue.setEnabled(false);
            btn_continue.setTextColor(getResources().getColor(R.color.iron_palette));
            btn_continue.setBackground(getResources().getDrawable(R.drawable.rectangle_rounded_bombay));
            totalPrice = 0;
            totalQuantity = 0;
            text_total_price.setText("Total Price : IDR " + Utils.priceFormat(totalPrice) + ",- (" + totalQuantity + " pcs)");
            return;
        }
        totalPriceQuantity();
        myOrderAdapter.setList(myOrderList, quantityList);
    }

    private void totalPriceQuantity() {
        totalPrice = 0;
        totalQuantity = 0;
        for (int i = 0; i < myOrderList.size(); i++) {
            totalPrice += myOrderList.get(i).getProductPrice() * quantityList.get(i);
            totalQuantity += quantityList.get(i);
        }
        text_total_price.setText("Price : IDR " + Utils.priceFormat(totalPrice) + ",- (" + totalQuantity + " pcs)");
    }

    private void show(View view) {
        view.getBackground().setAlpha(Constant.MAX_ALPHA);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT) {
                relative_loading.setVisibility(View.GONE);
            }
        } else if (resultCode == 1) {
            /*
             * This condition will appear if PAYMENT ACTIVITY send result code = 1
             * Because user want to order again, so we need to clear all things
             * */
            totalQuantity = 0;
            unitPrice = 0;
            totalPrice = 0;
            relative_loading.setVisibility(View.GONE);
            quantityList.clear();
            myOrderList.clear();
            text_total_price.setText("Total Price : IDR 0,- (0 pcs)");
            myOrderAdapter.setList(myOrderList, quantityList);
            myOrderAdapter.notifyDataSetChanged();
        }
    }

    private class ContinueRunner extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            synchronized (this) {
                Intent intent = new Intent(OrderActivity.this, ConfirmOrderActivity.class);
//                intent.putParcelableArrayListExtra(ConfirmOrderActivity.CONFIRM_GETTING_ORDER_LIST, (ArrayList<? extends Parcelable>) myOrderList);
//                intent.putIntegerArrayListExtra(ConfirmOrderActivity.CONFIRM_GETTING_QUANTITY_LIST, (ArrayList<Integer>) quantityList);
                startActivityForResult(intent, RESULT);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            show(relative_loading);
        }
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ImageView background = findViewById(R.id.image_order_menu_background);
            Picasso.get()
                    .load(R.drawable.asset_background_grid)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(background);
//            DecodeBitmap.setScaledImageView(background, R.drawable.asset_background_grid, OrderActivity.this);
            show(relative_loading);
        }

        @Override
        protected Void doInBackground(Void... strings) {
            synchronized (this) {
                Intent intent = getIntent();
                if (intent.getParcelableExtra(MERCHANT_DATA) != null) {
                    merchantData = intent.getParcelableExtra(MERCHANT_DATA);
                    Call<Product> call = apiInterface.getMenu("getting_menu", merchantData.getMerchantId());
                    call.enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            if (response.body().getProductList() != null) {
                                productList = response.body().getProductList();
                                menuAdapter.setProductList(productList);
                                menuAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "WE GOT SITUATION DOWN HERE, MAYDAY MAYDAY!!!!!!!!");
                            }
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                            Log.e(TAG, "FAILURE !!!!! GOOD BYE MY FRIEND!");
                        }
                    });

                    if (StaticData.PRODUCT_ORDER_MAP.get(merchantData.getMerchantId()) != null) {
                        quantityList.addAll(StaticData.QUANTITY_ORDER_MAP.get(merchantData.getMerchantId()));
                        myOrderList.addAll(StaticData.PRODUCT_ORDER_MAP.get(merchantData.getMerchantId()));
                        totalPriceQuantity();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            Picasso.get()
                    .load(Constant.URL + merchantData.getMerchantImagePromo())
                    .into(imageView);

            relative_loading.setVisibility(View.GONE);

            if (myOrderList.size() > 0) {
                btn_continue.setTextColor(getResources().getColor(R.color.cerulean_palette));
                btn_continue.setBackground(getResources().getDrawable(R.drawable.selection_athens_iron));
                btn_continue.setEnabled(true);
            }

            myOrderAdapter.setList(myOrderList, quantityList);
            myOrderAdapter.notifyDataSetChanged();
        }
    }
}
