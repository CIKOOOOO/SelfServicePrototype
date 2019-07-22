package com.andrew.selfserviceprototype.Activity;

import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.selfserviceprototype.Api.ApiClient;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<MerchantData> merchantData;
    private CarouselView carouselView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVar();
    }

    private void initVar() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/sniglet_reguler.ttf");

        LinearLayout linearLayout = findViewById(R.id.linear_main);
        TextView title = findViewById(R.id.text_title_main);
        TextView touch_to_continue = findViewById(R.id.text_touch_continue);
        ImageView background = findViewById(R.id.image_main_background);

        carouselView = findViewById(R.id.carousel_main_activity);

        merchantData = new ArrayList<>();

        Call<MerchantData> call = apiInterface.getMerchantData("ads_promo");

        call.enqueue(new Callback<MerchantData>() {
            @Override
            public void onResponse(Call<MerchantData> call, Response<MerchantData> response) {
                if (response.body().getMerchantDataList() != null) {
                    merchantData.addAll(response.body().getMerchantDataList());
                    carouselView.setImageListener(imageListener);
                    carouselView.setPageCount(merchantData.size());
                } else {
                    Log.e(TAG, "Merchant Data is null!!! CALL EMERGENCY!!!");
                }
            }

            @Override
            public void onFailure(Call<MerchantData> call, Throwable t) {
                Log.e(TAG, "MAYDAY MAYDAY, WE GOT SITUATION HERE \\N BACKUP IS NEEDED!!!!!!!");
            }
        });

        DecodeBitmap.setScaledImageView(background, R.drawable.asset_background, this);

        title.setTypeface(typeface);
        touch_to_continue.startAnimation(getAnimation());
        linearLayout.setOnClickListener(this);
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
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
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
