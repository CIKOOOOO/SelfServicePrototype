package com.andrew.selfserviceprototype.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.selfserviceprototype.Adapter.MerchantListAdapter;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Payment;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
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

public class MerchantListActivity extends BaseActivity implements MerchantListAdapter.imageOnClick
        , View.OnClickListener {
    public static final String GETTING_MERCHANT_DATA = "get_merchant_data";

    private static final String TAG = MerchantListActivity.class.getSimpleName();

    private MerchantListAdapter merchantListAdapter;
    private RecyclerView recyclerView;
    private SnapHelper linearSnapHelper;
    private LinearLayoutManager linearLayoutManager;
    private CarouselView carouselView;
    private TextView text_qty, text_price;
    private RelativeLayout relative_ads;

    private int adsPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_list);
        StaticData.PRODUCT_ORDER_MAP.clear();
        StaticData.QUANTITY_ORDER_MAP.clear();
        initVar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        long totalPrice = 0;
        int totalItem = 0;
        for (int i = 0; i < StaticData.MERCHANT_LIST.size(); i++) {
            if (StaticData.PRODUCT_ORDER_MAP.containsKey(StaticData.MERCHANT_LIST.get(i).getMerchantId())) {
                for (int j = 0; j < StaticData.PRODUCT_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()).size(); j++) {
                    totalPrice += StaticData.PRODUCT_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()).get(j).getProductPrice()
                            * StaticData.QUANTITY_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()).get(j);
                    totalItem += StaticData.QUANTITY_ORDER_MAP.get(StaticData.MERCHANT_LIST.get(i).getMerchantId()).get(j);
                }
            }
        }
        text_qty.setText(totalItem + "");
        text_price.setText("Rp " + Utils.priceFormat(totalPrice) + ",-");
    }

    private void initVar() {
        adsPosition = -1;

        final ImageView image_ads = findViewById(R.id.image_ads_merchant_list);
        final TextView text_product_name = findViewById(R.id.text_product_name_merchant_list);
        final TextView text_product_price = findViewById(R.id.text_selected_price_ads_merchant_list);
        final TextView text_description = findViewById(R.id.text_description_merchant_list);
        final TextView text_term_condition = findViewById(R.id.text_term_condition_merchant_list);
        final Button btn_add_cart = findViewById(R.id.btn_add_cart_merchant_list);

        LinearLayout linearCart = findViewById(R.id.linear_cart_merchant_list);
        ImageView background = findViewById(R.id.image_merchant_list_background);
        ImageButton imageButtonCard = findViewById(R.id.image_button_trolley_merchant_list);
        ImageButton imageButtonBack = findViewById(R.id.img_btn_back_merchant_list);

        text_qty = findViewById(R.id.text_quantity_product_merchant_list);
        text_price = findViewById(R.id.text_price_merchant_list);
        relative_ads = findViewById(R.id.relative_ads_merchant_list);
        recyclerView = findViewById(R.id.recycler_merchant_list);
        carouselView = findViewById(R.id.carousel_merchant_list);

        linearSnapHelper = new LinearSnapHelper();
        linearLayoutManager = new GridLayoutManager(this, 3);

        DecodeBitmap.setScaledImageView(background, R.drawable.asset_background_grid, this);

        recyclerView.setLayoutManager(linearLayoutManager);

        relative_ads.setOnClickListener(this);

        btn_add_cart.setOnClickListener(this);
        imageButtonBack.setOnClickListener(this);
        linearCart.setOnClickListener(this);
        imageButtonCard.setOnClickListener(this);

        merchantListAdapter = new MerchantListAdapter(this, StaticData.MERCHANT_LIST, this);
        recyclerView.setAdapter(merchantListAdapter);
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(StaticData.PRODUCT_ADS_LIST.size());

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                adsPosition = position;
                relative_ads.getBackground().setAlpha(Constant.MAX_ALPHA);
                relative_ads.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(Constant.PRODUCT_URL + StaticData.PRODUCT_ADS_LIST.get(position).getProductImage())
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(image_ads);
                text_product_name.setText(StaticData.PRODUCT_ADS_LIST.get(position).getProductName());
                text_product_price.setText("Price : Rp " + Utils.priceFormat(StaticData.PRODUCT_ADS_LIST.get(position).getProductPrice()) + ",-");
                text_description.setText(Utils.escapeStringEnter(StaticData.PRODUCT_ADS_LIST.get(position).getProductDesc()));
                text_term_condition.setText(Utils.escapeStringEnter(StaticData.PRODUCT_ADS_LIST.get(position).getTermCondition()));

                if (StaticData.PRODUCT_ORDER_MAP.containsKey(StaticData.PRODUCT_ADS_LIST.get(position).getMerchantId())) {
                    List<Product> productList = new ArrayList<>(StaticData.PRODUCT_ORDER_MAP.get(StaticData.PRODUCT_ADS_LIST.get(position).getMerchantId()));

                    boolean check = true;

                    for (Product product : productList) {
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
            }
        });
    }

    @Override
    public void onClick(int position) {
        if (position < 6) {
            Intent intent = new Intent(MerchantListActivity.this, OrderActivity.class);
            intent.putExtra(OrderActivity.MERCHANT_DATA, (Parcelable) StaticData.MERCHANT_LIST.get(position));
            startActivity(intent);
        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.get()
                    .load(Constant.PRODUCT_URL + StaticData.PRODUCT_ADS_LIST.get(position).getProductImage())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(imageView);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_cart_merchant_list:
                if (Integer.parseInt(text_qty.getText().toString()) > 0) {
                    startActivity(new Intent(MerchantListActivity.this, ConfirmOrderActivity.class));
                }
                break;
            case R.id.image_button_trolley_merchant_list:
                if (Integer.parseInt(text_qty.getText().toString()) > 0) {
                    startActivity(new Intent(MerchantListActivity.this, ConfirmOrderActivity.class));
                }
                break;
            case R.id.relative_ads_merchant_list:
                relative_ads.setVisibility(View.GONE);
                break;
            case R.id.btn_add_cart_merchant_list:
                if (StaticData.PRODUCT_ORDER_MAP.containsKey(StaticData.PRODUCT_ADS_LIST.get(adsPosition).getMerchantId())) {
                    StaticData.PRODUCT_ORDER_MAP.get(StaticData.PRODUCT_ADS_LIST.get(adsPosition).getMerchantId()).add(StaticData.PRODUCT_ADS_LIST.get(adsPosition));
                    StaticData.QUANTITY_ORDER_MAP.get(StaticData.PRODUCT_ADS_LIST.get(adsPosition).getMerchantId()).add(1);
                } else {
                    List<Product> list = new ArrayList<>();
                    List<Integer> integerList = new ArrayList<>();

                    list.add(StaticData.PRODUCT_ADS_LIST.get(adsPosition));
                    integerList.add(1);

                    StaticData.PRODUCT_ORDER_MAP.put(StaticData.PRODUCT_ADS_LIST.get(adsPosition).getMerchantId(), list);
                    StaticData.QUANTITY_ORDER_MAP.put(StaticData.PRODUCT_ADS_LIST.get(adsPosition).getMerchantId(), integerList);
                }
                onResume();
                relative_ads.setVisibility(View.GONE);
                Toast.makeText(this, "Add to cart success", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_btn_back_merchant_list:
                StaticData.PRODUCT_ORDER_MAP.clear();
                StaticData.QUANTITY_ORDER_MAP.clear();
                finish();
                break;
        }
    }
}
