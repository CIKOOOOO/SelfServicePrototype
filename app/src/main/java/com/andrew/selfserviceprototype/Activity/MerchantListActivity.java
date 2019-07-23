package com.andrew.selfserviceprototype.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.andrew.selfserviceprototype.Adapter.MerchantListAdapter;
import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.R;
import com.andrew.selfserviceprototype.Utils.BaseActivity;
import com.andrew.selfserviceprototype.Utils.Constant;
import com.andrew.selfserviceprototype.Utils.DecodeBitmap;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MerchantListActivity extends BaseActivity implements MerchantListAdapter.imageOnClick, View.OnScrollChangeListener {

    public static final String GETTING_MERCHANT_DATA = "get_merchant_data";

    private static final String TAG = MerchantListActivity.class.getSimpleName();

    private MerchantListAdapter merchantListAdapter;
    private RecyclerView recyclerView;
    private LinearSnapHelper linearSnapHelper;
    private LinearLayoutManager linearLayoutManager;
    private List<MerchantData> merchantData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_list);
        initVar();
    }

    private void initVar() {
        recyclerView = findViewById(R.id.recycler_merchant_list);

        ImageView background = findViewById(R.id.image_merchant_list_background);

        merchantData = new ArrayList<>();
        linearSnapHelper = new LinearSnapHelper();
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        DecodeBitmap.setScaledImageView(background, R.drawable.asset_background_grid, this);

        recyclerView.setLayoutManager(linearLayoutManager);
        linearSnapHelper.attachToRecyclerView(recyclerView);

        Intent intent = getIntent();
        if (intent.getParcelableArrayListExtra(GETTING_MERCHANT_DATA) != null) {
            merchantData.addAll(intent.<MerchantData>getParcelableArrayListExtra(GETTING_MERCHANT_DATA));
            if (merchantData.size() > 0) {
                Log.e(TAG, "Merchant DATA is NOT NULL");
                merchantListAdapter = new MerchantListAdapter(this, merchantData, this);
                recyclerView.setAdapter(merchantListAdapter);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener(this);
        }
    }

    @Override
    public void onClick(int position, boolean isMoveActivity) {
        if (isMoveActivity) {
            Intent intent = new Intent(MerchantListActivity.this, OrderActivity.class);
            intent.putExtra(OrderActivity.MERCHANT_DATA, (Parcelable) merchantData.get(position));
            startActivity(intent);
        } else {
//            if (position == merchantData.size() - 1) {
//                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, new RecyclerView.State(), merchantData.size());
//            }
//            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, new RecyclerView.State(), position);
            recyclerView.smoothScrollToPosition(position);
//            merchantListAdapter.notifyDataSetChanged();
        }
    }
    @Override

    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        merchantListAdapter.setLastPosition(linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        merchantListAdapter.notifyDataSetChanged();
    }
}
