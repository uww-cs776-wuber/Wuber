package com.example.rideshare;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/login")
    Call<Result> executeLogin(@Body HashMap<String,String> map);
    @POST("/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);
    @POST("/clientlocate")
    Call<Result> executeCLocate(@Body HashMap<String,String> map);
    @POST("/destination")
    Call<Result> executeDesTime(@Body HashMap<String,String > map);
    @POST("/pickuptime")
    Call<Result> executePickupTime(@Body HashMap<String,String > map);
}
