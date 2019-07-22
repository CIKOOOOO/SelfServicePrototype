package com.andrew.selfserviceprototype.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderType {

    @SerializedName("response")
    private String response;

    @SerializedName("order_type_list")
    private List<OrderType> orderTypeList;

    @SerializedName("order_type_id")
    private String orderTypeId;

    @SerializedName("order_type_name")
    private String orderTypeName;

    public String getResponse() {
        return response;
    }

    public List<OrderType> getOrderTypeList() {
        return orderTypeList;
    }

    public String getOrderTypeId() {
        return orderTypeId;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }
}
