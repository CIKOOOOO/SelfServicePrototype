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

    @SerializedName("payment_icon")
    private String paymentIcon;

    public Payment() {
    }

    public Payment(String paymentId, String paymentType, String paymentIcon) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.paymentIcon = paymentIcon;
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

    public String getPaymentIcon() {
        return paymentIcon;
    }
}
