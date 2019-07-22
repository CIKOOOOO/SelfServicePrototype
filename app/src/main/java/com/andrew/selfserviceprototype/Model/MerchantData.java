package com.andrew.selfserviceprototype.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MerchantData implements Parcelable {

    @SerializedName("merchant_data_list")
    private List<MerchantData> merchantDataList;

    @SerializedName("response")
    private String response;

    @SerializedName("merchant_name")
    private String merchantName;

    @SerializedName("merchant_id")
    private String merchantId;

    @SerializedName("merchant_icon")
    private String merchantIcon;

    @SerializedName("merchant_image_promo")
    private String merchantImagePromo;

    @SerializedName("merchant_image_place")
    private String merchantImagePlace;

    @SerializedName("merchant_location")
    private String merchantLocation;

    @SerializedName("merchant_address")
    private String merchantAddress;

    public MerchantData(String merchantName, String merchantId, String merchantIcon, String merchantImagePromo, String merchantImagePlace, String merchantLocation, String merchantAddress) {
        this.merchantName = merchantName;
        this.merchantId = merchantId;
        this.merchantIcon = merchantIcon;
        this.merchantImagePromo = merchantImagePromo;
        this.merchantImagePlace = merchantImagePlace;
        this.merchantLocation = merchantLocation;
        this.merchantAddress = merchantAddress;
    }

    public List<MerchantData> getMerchantDataList() {
        return merchantDataList;
    }

    public String getResponse() {
        return response;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantIcon() {
        return merchantIcon;
    }

    public String getMerchantImagePromo() {
        return merchantImagePromo;
    }

    public String getMerchantImagePlace() {
        return merchantImagePlace;
    }

    public String getMerchantLocation() {
        return merchantLocation;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public static Creator<MerchantData> getCREATOR() {
        return CREATOR;
    }

    protected MerchantData(Parcel in) {
        merchantDataList = in.createTypedArrayList(MerchantData.CREATOR);
        response = in.readString();
        merchantName = in.readString();
        merchantId = in.readString();
        merchantIcon = in.readString();
        merchantImagePromo = in.readString();
        merchantImagePlace = in.readString();
        merchantLocation = in.readString();
        merchantAddress = in.readString();
    }

    public static final Creator<MerchantData> CREATOR = new Creator<MerchantData>() {
        @Override
        public MerchantData createFromParcel(Parcel in) {
            return new MerchantData(in);
        }

        @Override
        public MerchantData[] newArray(int size) {
            return new MerchantData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(merchantDataList);
        parcel.writeString(response);
        parcel.writeString(merchantName);
        parcel.writeString(merchantId);
        parcel.writeString(merchantIcon);
        parcel.writeString(merchantImagePromo);
        parcel.writeString(merchantImagePlace);
        parcel.writeString(merchantLocation);
        parcel.writeString(merchantAddress);
    }
}
