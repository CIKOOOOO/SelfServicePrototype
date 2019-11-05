package com.andrew.selfserviceprototype.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {


    public static String priceFormat(long totalPrice) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(totalPrice);
    }

    public static String priceFormat(double totalPrice) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(totalPrice);
    }

    public static String getTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String escapeStringEnter(String description) {
        return description.replace("\\n", "\r\n");
    }

    public static String getCRC(String data) {
        int crc = 0xFFFF;
        int polynomial = 0x1021;

        byte[] bytes = data.getBytes();

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;
        String crc_temp = Integer.toHexString(crc);
        while (crc_temp.length() != 4) {
            if (crc_temp.length() < 4) {
                /*
                 * If length of crc is less then 4
                 * , then we should add 0 in front of value until it length become 4
                 * */
                crc_temp = "0" + crc_temp;
            } else {
                crc_temp = crc_temp.substring(0, 4);
            }
        }
        return crc_temp;
    }

    public static String removeDot(String NUMBER) {
        if (!NUMBER.contains(".")) {
            return NUMBER;
        }
        return NUMBER.replaceAll(".?0*$", "");
    }

    public static void hideSoftKeyboardFromRoot(View view, final Activity activity) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideSoftKeyboardFromRoot(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) return;
        else if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
