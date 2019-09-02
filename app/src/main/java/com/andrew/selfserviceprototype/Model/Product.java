package com.andrew.selfserviceprototype.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product implements Parcelable {
    @SerializedName("product_list")
    private List<Product> productList;

    @SerializedName("response")
    private String response;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("mid")
    private String merchantId;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_price")
    private double productPrice;

    @SerializedName("product_desc")
    private String productDesc;

    @SerializedName("product_img")
    private String productImage;

    @SerializedName("is_advertisement")
    private boolean isAdvertisement;

    @SerializedName("term_condition")
    private String termCondition;

    @SerializedName("date_from")
    private String dateFrom;

    @SerializedName("date_to")
    private String dateTo;

    @SerializedName("discount")
    private double discount;

    public Product(String productId, String merchantId, String productName, long productPrice, String productDesc, String productImage, boolean isAdvertisement, String termCondition, String dateFrom, String dateTo, double discount) {
        this.productId = productId;
        this.merchantId = merchantId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productImage = productImage;
        this.isAdvertisement = isAdvertisement;
        this.termCondition = termCondition;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.discount = discount;
    }

    protected Product(Parcel in) {
        productList = in.createTypedArrayList(Product.CREATOR);
        response = in.readString();
        productId = in.readString();
        merchantId = in.readString();
        productName = in.readString();
        productPrice = in.readLong();
        productDesc = in.readString();
        productImage = in.readString();
        isAdvertisement = in.readByte() != 0;
        termCondition = in.readString();
        dateFrom = in.readString();
        dateTo = in.readString();
        discount = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public List<Product> getProductList() {
        return productList;
    }

    public String getResponse() {
        return response;
    }

    public String getProductId() {
        return productId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductImage() {
        return productImage;
    }

    public boolean isAdvertisement() {
        return isAdvertisement;
    }

    public String getTermCondition() {
        return termCondition;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public double getDiscount() {
        return discount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(productList);
        parcel.writeString(response);
        parcel.writeString(productId);
        parcel.writeString(merchantId);
        parcel.writeString(productName);
        parcel.writeDouble(productPrice);
        parcel.writeString(productDesc);
        parcel.writeString(productImage);
        parcel.writeByte((byte) (isAdvertisement ? 1 : 0));
        parcel.writeString(termCondition);
        parcel.writeString(dateFrom);
        parcel.writeString(dateTo);
        parcel.writeDouble(discount);
    }
}
