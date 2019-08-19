package com.andrew.selfserviceprototype.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Transaction implements Parcelable {
    @SerializedName("response")
    private String response;

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("tax_amount")
    private long taxAmount;

    @SerializedName("transaction_date")
    private String transactionDate;

    @SerializedName("transaction_time")
    private String transactionTime;

    @SerializedName("payment_id")
    private String paymentId;

    @SerializedName("order_status")
    private String orderStatus;

    public Transaction(String transactionId, long taxAmount, String transactionDate, String orderStatus) {
        this.transactionId = transactionId;
        this.taxAmount = taxAmount;
        this.transactionDate = transactionDate;
        this.orderStatus = orderStatus;
    }

    protected Transaction(Parcel in) {
        response = in.readString();
        transactionId = in.readString();
        taxAmount = in.readLong();
        transactionDate = in.readString();
        transactionTime = in.readString();
        paymentId = in.readString();
        orderStatus = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getTaxAmount() {
        return taxAmount;
    }

    public String getResponse() {
        return response;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(response);
        parcel.writeString(transactionId);
        parcel.writeLong(taxAmount);
        parcel.writeString(transactionDate);
        parcel.writeString(transactionTime);
        parcel.writeString(paymentId);
        parcel.writeString(orderStatus);
    }

    public static class TransactionDetail implements Parcelable {
        @SerializedName("response")
        private String response;

        @SerializedName("transaction_detail_list")
        private List<TransactionDetail> transactionDetailList;

        @SerializedName("transaction_id")
        private String transactionId;

        @SerializedName("merchant_id")
        private String merchantId;

        @SerializedName("product_id")
        private String productId;

        @SerializedName("unit_price")
        private long unitPrice;

        @SerializedName("quantity")
        private int quantity;


        public TransactionDetail(String transactionId, String merchantId, String productId, long unitPrice, int quantity) {
            this.transactionId = transactionId;
            this.merchantId = merchantId;
            this.productId = productId;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }


        protected TransactionDetail(Parcel in) {
            response = in.readString();
            transactionDetailList = in.createTypedArrayList(TransactionDetail.CREATOR);
            transactionId = in.readString();
            merchantId = in.readString();
            productId = in.readString();
            unitPrice = in.readLong();
            quantity = in.readInt();
        }

        public static final Creator<TransactionDetail> CREATOR = new Creator<TransactionDetail>() {
            @Override
            public TransactionDetail createFromParcel(Parcel in) {
                return new TransactionDetail(in);
            }

            @Override
            public TransactionDetail[] newArray(int size) {
                return new TransactionDetail[size];
            }
        };

        public String getResponse() {
            return response;
        }

        public List<TransactionDetail> getTransactionDetailList() {
            return transactionDetailList;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getProductId() {
            return productId;
        }

        public long getUnitPrice() {
            return unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getMerchantId() {
            return merchantId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(response);
            parcel.writeTypedList(transactionDetailList);
            parcel.writeString(transactionId);
            parcel.writeString(merchantId);
            parcel.writeString(productId);
            parcel.writeLong(unitPrice);
            parcel.writeInt(quantity);
        }
    }
}
