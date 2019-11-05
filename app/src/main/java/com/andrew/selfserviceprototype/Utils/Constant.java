package com.andrew.selfserviceprototype.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constant {
    public static final String URL = "http://10.20.212.241:80";
    public static final String FULL_URL = URL + "/self_service/";
    public static final String PRODUCT_URL = URL + "/self_service/asset/product/";
    public static final String ORDER_TYPE_URL = URL + "/self_service/asset/order_type/";
    public static final String PAYMENT_URL = URL + "/self_service/asset/payment/";

    public static final int MAX_ALPHA = 220;
    public static final long MAX_DURATION_PAYMENT = 15000;
    public static final long MAX_DURATION_TUTORIAL = 5000;
    public static final long MAX_DURATION_IDLE = 30000;

    private static final String[] ratingList = {"Not Satisfied", "Less Satisfied", "Enough"
            , "Satisfied", "Very Satisfied"};
    private static final String[] badRateList = {"Limited food variant", "Payment method"
            , "Too complicated", "Less promo", "Machine too slow", "Others"};
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
