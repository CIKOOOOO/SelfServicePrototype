package com.andrew.selfserviceprototype.Api;


import com.andrew.selfserviceprototype.Model.MerchantData;
import com.andrew.selfserviceprototype.Model.OrderType;
import com.andrew.selfserviceprototype.Model.Payment;
import com.andrew.selfserviceprototype.Model.Product;
import com.andrew.selfserviceprototype.Model.Transaction;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("getting_data.php")
    Call<MerchantData> getMerchantData(@Query("response") String response);

    @GET("getting_data.php")
    Call<Product> getMenu(@Query("response") String response, @Query("merchant_id") String merchantId);

    @GET("getting_data.php")
    Call<Payment> getPaymentType(@Query("response") String response);

    @GET("getting_data.php")
    Call<OrderType> getOrderType(@Query("response") String response);

    @POST("transaction.php")
    @FormUrlEncoded
    Call<Transaction> sendTransaction(@Field("response") String response, @Field("tid") String tid
            , @Field("mid") String mid, @Field("tax") long tax, @Field("transaction_date") String transaction_date
            , @Field("transaction_time") String transaction_time, @Field("payment_id") String payment_id
            , @Field("order_type_id") String order_type_id, @Field("order_status") String order_status);

    @POST("transaction.php")
    @FormUrlEncoded
    Call<Transaction.TransactionDetail> sendTransactionDetail(@Field("response") String response
            , @Field("tid") String tid, @Field("pid") String pid
            , @Field("unit_price") long unit_price, @Field("quantity") int quantity);

    @POST("transaction.php")
    @FormUrlEncoded
    Call<Transaction> updateFeedback(@Field("response") String response, @Field("tid") String TID, @Field("feedback_star") int feedback_star);
}
