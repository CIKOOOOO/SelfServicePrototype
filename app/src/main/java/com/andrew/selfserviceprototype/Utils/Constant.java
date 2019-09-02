package com.andrew.selfserviceprototype.Utils;

import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constant {
    public static final String URL = "http://192.168.43.154:80";
    public static final String FULL_URL = URL + "/self_service/";
    public static final String PRODUCT_URL = URL + "/self_service/asset/product/";
    public static final String ORDER_TYPE_URL = URL + "/self_service/asset/order_type/";
    public static final String PAYMENT_URL = URL + "/self_service/asset/payment/";

    public static final int MAX_ALPHA = 220;
    public static final long MAX_DURATION_PAYMENT = 5000;
    public static final long MAX_DURATION_IDLE = 30000;

    private static final String[] ratingList = {"Not Satisfied", "Less Satisfied", "Enough", "Satisfied", "Very Satisfied"};
    /*
     * Ganti promo, food option, unfriendly ux
     * */
    private static final String[] badRateList = {"Limited food variant", "Payment method", "Too complicated", "Less promo", "Machine too slow", "Others"};
    private static final Boolean[] falseList = {false, false, false, false, false, false};

    public static List<String> getRatingList() {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, ratingList);
        return list;
    }

    public static List<String> getBadRateList() {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, badRateList);
        return list;
    }

    public static List<Boolean> getFalseList() {
        List<Boolean> booleans = new ArrayList<>();
        Collections.addAll(booleans, falseList);
        return booleans;
    }

}
