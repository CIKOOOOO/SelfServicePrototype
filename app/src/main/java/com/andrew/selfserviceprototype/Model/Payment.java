package com.andrew.selfserviceprototype.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Payment {
    @SerializedName("response")
    private String response;

    @SerializedName("payment_list")
    private List<Payment> paymentList;

    @SerializedName("payment_id")
    private String paymentId;

    @SerializedName("payment_type")
    private String paymentType;

    public Payment(String paymentId, String paymentType) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
    }

    public String getResponse() {
        return response;
    }

    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentType() {
        return paymentType;
    }
}
