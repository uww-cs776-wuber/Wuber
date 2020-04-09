package com.example.rideshare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {
    String BASEURL="http://192.168.1.8:3000";
    @POST("/login")
    Call<Result> executeLogin(@Body HashMap<String,String> map);
    @POST("/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);
    @POST("/clientRequest")
    Call<Result> executeClientRequest(@Body HashMap<String,String> map);
    @POST("/takePassenger")
    Call<Result> executeTakeRequest(@Body HashMap<String,String> map);
    @GET("/driverNotify")
    Call<List<Result>> executeDriverNotify();
    @GET("/passengerNotify/{email}")
    Call <Result> executePassengerNotify(@Path("email") String email);
    @GET("/pickupInProgress/{driver}")
    Call <List<Result>> executePickupInProgress(@Path("driver") String email);
    @DELETE("/closeClientRequest/{email}")
    Call<Void> executeCloseRequest(@Path("email") String email);
    @DELETE("/addClientRequest/{email}")
    Call<Void> executeAddRequest(@Path("email") String email);
}
