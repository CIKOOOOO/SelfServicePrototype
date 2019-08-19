package com.andrew.selfserviceprototype.Utils;

import android.util.ArrayMap;

import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.Model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaticData {
    public static List<Transaction.TransactionDetail> TRANSACTION_DETAIL_LIST = new ArrayList<>();

    /*
     * Key nya harus berdasarkan merchant ID, sehingga saat produk didelete
     * Posisi arraynya pun bisa diketahui, namun akibatnya proses customable menu menjadi tidak bisa digunakan
     * */

    public static Map<String, List<Product>> PRODUCT_ORDER_MAP = new ArrayMap<>();
    public static Map<String, List<Integer>> QUANTITY_ORDER_MAP = new ArrayMap<>();

    public static List<MerchantData> MERCHANT_LIST = new ArrayList<>();
}
