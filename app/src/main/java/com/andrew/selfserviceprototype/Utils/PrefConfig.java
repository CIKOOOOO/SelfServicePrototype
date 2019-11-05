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

    public void setTutorial(boolean check) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_tutorial), check);
        editor.commit();
    }

    public boolean isShow() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_tutorial), false);
    }
//
//    public String getOrderTypeId() {
//        return sharedPreferences.getString(context.getString(R.string.pref_order_type_id), "");
//    }
}
