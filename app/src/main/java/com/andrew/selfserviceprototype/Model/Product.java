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

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_price")
    private long productPrice;

    @SerializedName("product_desc")
    private String productDesc;

    @SerializedName("product_img")
    private String productImage;

    public Product(String productId, String productName, long productPrice, String productDesc, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productImage = productImage;
    }

    protected Product(Parcel in) {
        productList = in.createTypedArrayList(Product.CREATOR);
        response = in.readString();
        productId = in.readString();
        productName = in.readString();
        productPrice = in.readLong();
        productDesc = in.readString();
        productImage = in.readString();
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

    public String getProductName() {
        return productName;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductImage() {
        return productImage;
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
        parcel.writeString(productName);
        parcel.writeLong(productPrice);
        parcel.writeString(productDesc);
        parcel.writeString(productImage);
    }
}
