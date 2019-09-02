package com.andrew.selfserviceprototype.Activity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.selfserviceprototype.Api.ApiClient;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
import com.andrew.selfserviceprototype.Utils.StaticData;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CarouselView carouselView;

    private List<MerchantData> merchantData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVar();
    }

    private void initVar() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/axure_handwriting.ttf");

        RelativeLayout linearLayout = findViewById(R.id.linear_main);
        TextView touch_to_continue = findViewById(R.id.text_touch_continue);

        carouselView = findViewById(R.id.carousel_main_activity);

        merchantData = new ArrayList<>();

        Call<MerchantData> call = apiInterface.getMerchantData("get_merchant_data");
        Call<Product> ads_product = apiInterface.getAdsProduct("get_ads_product");

        /*
         * To get all data that is advertise by merchant
         * */
        ads_product.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.body().getResponse().equals("ok")) {
                    StaticData.PRODUCT_ADS_LIST.clear();
                    StaticData.PRODUCT_ADS_LIST.addAll(response.body().getProductList());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });

        call.enqueue(new Callback<MerchantData>() {
            @Override
            public void onResponse(Call<MerchantData> call, Response<MerchantData> response) {
                if (response.body().getMerchantDataList() != null) {
                    merchantData.addAll(response.body().getMerchantDataList());
                    /*
                     * We need to clear this merchant static list, because it will causes redundant data
                     * so we clear and database can add the new one
                     * */
                    StaticData.MERCHANT_LIST.clear();
                    StaticData.MERCHANT_LIST.addAll(response.body().getMerchantDataList());
                    carouselView.setImageListener(imageListener);
                    carouselView.setPageCount(4);
                } else {
                    Log.e(TAG, "Merchant Data is null!!! CALL EMERGENCY!!!");
                }
            }

            @Override
            public void onFailure(Call<MerchantData> call, Throwable t) {
                Log.e(TAG, "MAYDAY MAYDAY, WE GOT SITUATION HERE \\N BACKUP IS NEEDED!!!!!!!");
            }
        });

//        DecodeBitmap.setScaledImageView(background, R.drawable.asset_background, this);

        touch_to_continue.setTypeface(typeface2);
//        touch_to_continue.startAnimation(getAnimation());
        linearLayout.setOnClickListener(this);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                if (merchantData.size() > 0) {
                    Intent intent = new Intent(MainActivity.this, OrderTypeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private Animation getAnimation() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(350);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        return anim;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, final ImageView imageView) {
            Picasso.get()
                    .load(Constant.URL + merchantData.get(position).getMerchantImagePlace())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(imageView);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_main:
                if (merchantData.size() > 0) {
                    Intent intent = new Intent(MainActivity.this, OrderTypeActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
