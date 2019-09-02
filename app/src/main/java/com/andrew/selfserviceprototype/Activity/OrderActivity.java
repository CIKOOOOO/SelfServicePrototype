package com.andrew.selfserviceprototype.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrew.selfserviceprototype.Adapter.MenuAdapter;
import com.andrew.selfserviceprototype.Adapter.MyOrderAdapter;
import com.andrew.selfserviceprototype.Api.ApiClient;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.andrew.selfserviceprototype.Utils.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends BaseActivity implements MenuAdapter.menuOnClick
        , View.OnClickListener, MyOrderAdapter.addDeleteOrder, MyOrderAdapter.deleteOrder {
    public static final String MERCHANT_DATA = "merchant_data";

    private static final String TAG = OrderActivity.class.getSimpleName();
    private static final int RESULT = 12;

    private FrameLayout frame_order;
    private RelativeLayout relative_loading, relative_ads;
    private TextView text_quantity, text_frame_price, text_total_price;
    private Button btn_continue, btn_back;
    private CarouselView carousel_promo;
    private RecyclerView recycler_my_order;

    private MenuAdapter menuAdapter;
    private MyOrderAdapter myOrderAdapter;
    private MerchantData merchantData;
    private ApiInterface apiInterface;

    private List<Product> productList;
    private List<Product> myOrderList;
    private List<Integer> quantityList;

    private double unitPrice, totalPrice;
    private int totalQuantity, adsPosition;

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
        adsPosition = -1;

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        final ImageView img_promo = findViewById(R.id.image_ads_order);
        final TextView text_product_name = findViewById(R.id.text_product_name_order);
        final TextView text_product_price = findViewById(R.id.text_selected_price_ads_order);
        final TextView text_description = findViewById(R.id.text_description_order);
        final TextView text_term_condition = findViewById(R.id.text_term_condition_order);
        final Button btn_add_cart = findViewById(R.id.btn_add_cart_order);

        RecyclerView recycler_menu = findViewById(R.id.recycler_menu_order);

        recycler_my_order = findViewById(R.id.recycler_my_order);
        btn_back = findViewById(R.id.btn_order_again_order);
        carousel_promo = findViewById(R.id.carousel_image_promo_order);
        btn_continue = findViewById(R.id.btn_continue_order);
        relative_loading = findViewById(R.id.relative_order_loading);
        frame_order = findViewById(R.id.frame_order);
        text_quantity = findViewById(R.id.text_quantity_product_frame_order);
        text_frame_price = findViewById(R.id.text_price_frame_order);
        text_total_price = findViewById(R.id.text_total_price);
        relative_ads = findViewById(R.id.relative_ads_order);

        productList = new ArrayList<>();
        myOrderList = new ArrayList<>();
        quantityList = new ArrayList<>();
        menuAdapter = new MenuAdapter(this, productList, this);
        myOrderAdapter = new MyOrderAdapter(this, productList, quantityList, this, this);

        text_total_price.setText("Price : IDR " + Utils.priceFormat(totalPrice) + ",- (" + totalQuantity + " pcs)");

        btn_continue.setEnabled(false);
        btn_continue.setTextColor(getResources().getColor(R.color.iron_palette));
        btn_continue.setBackground(getResources().getDrawable(R.drawable.rectangle_rounded_bombay));

        recycler_my_order.setLayoutManager(new LinearLayoutManager(this));
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 3));

        carousel_promo.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                adsPosition = position;
                relative_ads.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(Constant.PRODUCT_URL + StaticData.PRODUCT_ADS_LIST.get(position).getProductImage())
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(img_promo);

                text_product_name.setText(StaticData.PRODUCT_ADS_LIST.get(position).getProductName());
                text_product_price.setText("Price : Rp " + Utils.priceFormat(StaticData.PRODUCT_ADS_LIST.get(position).getProductPrice()) + ",-");
                text_description.setText(Utils.escapeStringEnter(StaticData.PRODUCT_ADS_LIST.get(position).getProductDesc()));
                text_term_condition.setText(Utils.escapeStringEnter(StaticData.PRODUCT_ADS_LIST.get(position).getTermCondition()));

                boolean check = true;

                for (Product product : myOrderList) {
                    if (product.getProductId().equals(StaticData.PRODUCT_ADS_LIST.get(position).getProductId())) {
                        check = false;
                        btn_add_cart.setBackground(getDrawable(R.drawable.rectangle_rounded_bombay));
                        btn_add_cart.setTextColor(getResources().getColor(R.color.iron_palette));
                        btn_add_cart.setEnabled(false);
                        break;
                    }
                }

                if (check) {
                    btn_add_cart.setEnabled(true);
                    btn_add_cart.setBackground(getDrawable(R.drawable.selection_athens_iron));
                    btn_add_cart.setTextColor(getResources().getColor(R.color.cerulean_palette));
                }
            }
        });

        recycler_menu.setAdapter(menuAdapter);
        recycler_my_order.setAdapter(myOrderAdapter);

        btn_continue.setOnClickListener(this);
        btn_continue.setEnabled(false);
        relative_loading.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_add_cart.setOnClickListener(this);
        relative_ads.setOnClickListener(this);

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

        text_frame_price.setText("Price : IDR " + Utils.priceFormat(product.getProductPrice() * Integer.parseInt(text_quantity.getText().toString())) + ",-");
        Picasso.get().load(Constant.PRODUCT_URL + product.getProductImage()).into(imageView);
        title.setText(product.getProductName());

//        show(frame_order);

        frame_order.setOnClickListener(this);
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        cancel.setOnClickListener(this);

        if (isOrder) {
            add.setText("Update");
            myOrderList.set(position, product);
            quantityList.set(position, Integer.parseInt(text_quantity.getText().toString()));
        } else {
            add.setText("Add");
            myOrderList.add(product);
            quantityList.add(Integer.parseInt(text_quantity.getText().toString()));
        }

        totalQuantity += Integer.parseInt(text_quantity.getText().toString());
        totalPrice += product.getProductPrice() * Integer.parseInt(text_quantity.getText().toString());

        frame_order.setVisibility(View.GONE);

        myOrderAdapter.setList(myOrderList, quantityList);
        myOrderAdapter.notifyDataSetChanged();
        btn_back.setText(getResources().getString(R.string.additional_order));
        recycler_my_order.smoothScrollToPosition(((LinearLayout) findViewById(R.id.linear_my_order)).getBottom());
        totalPriceQuantity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_frame_order:
                frame_order.setVisibility(View.GONE);
                break;
            case R.id.btn_continue_order:
                /*
                 * Ini untuk masukkin datanya agar tidak berulang, sehingga harus di clear sebelum dimasukkin ulang
                 * Saya juga bingung cara optimalnya spt apa untuk menampilkan seluruh list dengan head
                 * Jadi saya pakai map yang keynya adalah setiap merchant ID dan valuenya adalah setiap produk yang dipesan oleh nasabah
                 * */

                StaticData.PRODUCT_ORDER_MAP.clear();
                StaticData.QUANTITY_ORDER_MAP.clear();

                for (int j = 0; j < StaticData.MERCHANT_LIST.size(); j++) {
                    List<Product> list = new ArrayList<>();
                    List<Integer> integerList = new ArrayList<>();

                    for (int i = 0; i < myOrderList.size(); i++) {
                        if (myOrderList.get(i).getMerchantId().equals(StaticData.MERCHANT_LIST.get(j).getMerchantId()) && quantityList.get(i) > 0) {
                            list.add(myOrderList.get(i));
                            integerList.add(quantityList.get(i));
                        }
                    }

                    if (list.size() > 0) {
                        StaticData.PRODUCT_ORDER_MAP.put(StaticData.MERCHANT_LIST.get(j).getMerchantId(), list);
                        StaticData.QUANTITY_ORDER_MAP.put(StaticData.MERCHANT_LIST.get(j).getMerchantId(), integerList);
                    }
                }

                /*
                 * Awalnya ini dipakai, tapi karena ada iklan yang dimana iklan tersebut bisa dari berbagai
                 * macam merchant, maka kondisi dibawah ini tidak bisa digunakan kembali
                 * */
//
//                if (!cc) {
//                    /*
//                     * Nah untuk yang ini, kondisi ini akan berjalan jika dalam merchant tersebut
//                     * pembeli belum membeli 1 produk pun
//                     * */
//                    List<Product> list = new ArrayList<>();
//                    List<Integer> integerList = new ArrayList<>();
//
//                    for (int i = 0; i < myOrderList.size(); i++) {
//                        if (myOrderList.get(i).getMerchantId().equals(merchantData.getMerchantId())) {
//                            list.add(myOrderList.get(i));
//                            integerList.add(quantityList.get(i));
//                        }
//                    }
//
//                    if (list.size() > 0) {
//                        StaticData.PRODUCT_ORDER_MAP.put(merchantData.getMerchantId(), list);
//                        StaticData.QUANTITY_ORDER_MAP.put(merchantData.getMerchantId(), integerList);
//                    }
//
//                }
                new ContinueRunner().execute();
                break;
            case R.id.btn_order_again_order:
                StaticData.PRODUCT_ORDER_MAP.clear();
                StaticData.QUANTITY_ORDER_MAP.clear();

                for (int j = 0; j < StaticData.MERCHANT_LIST.size(); j++) {
                    List<Product> list = new ArrayList<>();
                    List<Integer> integerList = new ArrayList<>();

                    for (int i = 0; i < myOrderList.size(); i++) {
                        if (myOrderList.get(i).getMerchantId().equals(StaticData.MERCHANT_LIST.get(j).getMerchantId())) {
                            list.add(myOrderList.get(i));
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
            case R.id.btn_add_cart_order:
                btn_back.setText(getResources().getString(R.string.additional_order));
                Toast.makeText(this, "Add to cart success", Toast.LENGTH_SHORT).show();
                relative_ads.setVisibility(View.GONE);
                myOrderList.add(StaticData.PRODUCT_ADS_LIST.get(adsPosition));
                quantityList.add(1);
                myOrderAdapter.setList(myOrderList, quantityList);
                myOrderAdapter.notifyDataSetChanged();
                totalPriceQuantity();
                recycler_my_order.smoothScrollToPosition(((LinearLayout) findViewById(R.id.linear_my_order)).getBottom());
                break;
            case R.id.relative_ads_order:
                relative_ads.setVisibility(View.GONE);
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
        totalPriceQuantity();
        if (myOrderList.size() == 0) {
            btn_back.setText(getResources().getString(R.string.back));
            return;
        }
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

        if (totalQuantity > 0) {
            btn_continue.setTextColor(getResources().getColor(R.color.cerulean_palette));
            btn_continue.setBackground(getResources().getDrawable(R.drawable.selection_athens_iron));
            btn_continue.setEnabled(true);
        } else {
            btn_continue.setEnabled(false);
            btn_continue.setTextColor(getResources().getColor(R.color.iron_palette));
            btn_continue.setBackground(getResources().getDrawable(R.drawable.rectangle_rounded_bombay));
        }
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
            initVar();
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
            text_total_price.setText("Price : IDR 0,- (0 pcs)");
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

                    for (String key : StaticData.PRODUCT_ORDER_MAP.keySet()) {
                        quantityList.addAll(StaticData.QUANTITY_ORDER_MAP.get(key));
                        myOrderList.addAll(StaticData.PRODUCT_ORDER_MAP.get(key));
                    }

//                    if (StaticData.PRODUCT_ORDER_MAP.get(merchantData.getMerchantId()) != null) {
//                        quantityList.addAll(StaticData.QUANTITY_ORDER_MAP.get(merchantData.getMerchantId()));
//                        myOrderList.addAll(StaticData.PRODUCT_ORDER_MAP.get(merchantData.getMerchantId()));
//                        totalPriceQuantity();
//                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            totalPriceQuantity();

            carousel_promo.setImageListener(new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    Picasso.get()
                            .load(Constant.PRODUCT_URL + StaticData.PRODUCT_ADS_LIST.get(position).getProductImage())
                            .into(imageView);
                }
            });

            carousel_promo.setPageCount(StaticData.PRODUCT_ADS_LIST.size());

            relative_loading.setVisibility(View.GONE);

            if (myOrderList.size() > 0) {
                btn_back.setText(getResources().getString(R.string.additional_order));
            } else {
                btn_back.setText(getResources().getString(R.string.back));
            }

            myOrderAdapter.setList(myOrderList, quantityList);
            myOrderAdapter.notifyDataSetChanged();
        }
    }
}
