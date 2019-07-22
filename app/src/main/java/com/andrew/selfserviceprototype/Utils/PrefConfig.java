package com.andrew.selfserviceprototype.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.andrew.selfserviceprototype.R;

public class PrefConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public PrefConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE);
    }

    public void saveOrderType(String orderTypeId, String orderType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_order_type), orderType);
        editor.putString(context.getString(R.string.pref_order_type), orderTypeId);
        editor.commit();
    }

    public String getOrderType() {
        return sharedPreferences.getString(context.getString(R.string.pref_order_type), "");
    }

    public String getOrderTypeId() {
        return sharedPreferences.getString(context.getString(R.string.pref_order_type_id), "");
    }
}
