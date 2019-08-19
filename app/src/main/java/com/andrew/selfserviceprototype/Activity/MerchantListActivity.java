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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.selfserviceprototype.Adapter.MerchantListAdapter;
import com.andrew.selfserviceprototype.Api.ApiInterface;
import com.andrew.selfserviceprototype.Model.MerchantData;
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
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MerchantListActivity extends BaseActivity implements MerchantListAdapter.imageOnClick, View.OnClickListener
//        , View.OnScrollChangeListener
{

    public static final String GETTING_MERCHANT_DATA = "get_merchant_data";

    private static final String TAG = MerchantListActivity.class.getSimpleName();

    private MerchantListAdapter merchantListAdapter;
    private RecyclerView recyclerView;
    private SnapHelper linearSnapHelper;
    private LinearLayoutManager linearLayoutManager;
    private CarouselView carouselView;
    private TextView text_qty, text_price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_list);
        StaticData.PRODUCT_ORDER_MAP.clear();
        StaticData.QUANTITY_ORDER_MAP.clear();
        initVar();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        recyclerView = findViewById(R.id.recycler_merchant_list);
        carouselView = findViewById(R.id.carousel_merchant_list);

        ImageButton imageButton = findViewById(R.id.image_button_trolley_merchant_list);
        ImageView background = findViewById(R.id.image_merchant_list_background);
        text_qty = findViewById(R.id.text_quantity_product_merchant_list);
        text_price = findViewById(R.id.text_price_merchant_list);

        linearSnapHelper = new LinearSnapHelper();
        linearLayoutManager = new GridLayoutManager(this, 3);

        DecodeBitmap.setScaledImageView(background, R.drawable.asset_background_grid, this);

        recyclerView.setLayoutManager(linearLayoutManager);
        imageButton.setOnClickListener(this);
//        linearSnapHelper.attachToRecyclerView(recyclerView);

//        Intent intent = getIntent();
//        if (intent.getParcelableArrayListExtra(GETTING_MERCHANT_DATA) != null) {
//            merchantData.addAll(intent.<MerchantData>getParcelableArrayListExtra(GETTING_MERCHANT_DATA));
//            if (merchantData.size() > 0) {
        Log.e(TAG, "Merchant DATA is NOT NULL");
        merchantListAdapter = new MerchantListAdapter(this, StaticData.MERCHANT_LIST, this);
        recyclerView.setAdapter(merchantListAdapter);
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(5);
//            }
//        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            recyclerView.setOnScrollChangeListener(this);
//        }
    }

    @Override
    public void onClick(int position) {
        if (position < 5) {
            Intent intent = new Intent(MerchantListActivity.this, OrderActivity.class);
            intent.putExtra(OrderActivity.MERCHANT_DATA, (Parcelable) StaticData.MERCHANT_LIST.get(position));
            startActivity(intent);
        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.get()
                    .load(Constant.URL + StaticData.MERCHANT_LIST.get(position).getMerchantImagePromo())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(imageView);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_trolley_merchant_list:
                if (Integer.parseInt(text_qty.getText().toString()) > 0) {
                    startActivity(new Intent(MerchantListActivity.this, ConfirmOrderActivity.class));
                }
                break;
        }
    }


//    @Override
//    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//        merchantListAdapter.setLastPosition(linearLayoutManager.findFirstCompletelyVisibleItemPosition());
//        merchantListAdapter.notifyDataSetChanged();
//    }
}
