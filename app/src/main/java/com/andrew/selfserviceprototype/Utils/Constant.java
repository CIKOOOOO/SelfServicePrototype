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
    public static final String URL = "http://192.168.43.55:80";
    public static final String FULL_URL = URL + "/self_service/";
    public static final String PRODUCT_URL = URL + "/self_service/asset/product/";

    public static final int MAX_ALPHA = 220;
    public static final long MAX_DURATION_PAYMENT = 5000;

    private static final String[] ratingList = {"Tidak Puas", "Kurang Puas", "Cukup", "Puas", "Sangat Puas"};

    public static List<String> getRatingList() {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, ratingList);
        return list;
    }

}
