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
<<<<<<< HEAD
    String BASEURL="http://192.168.1.6:3000";
=======
    //https://uww-uber.herokuapp.com/
    String BASEURL="https://uww-uber.herokuapp.com/";
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
    @POST("/login")
    Call<Result> executeLogin(@Body HashMap<String,String> map);
    @POST("/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);
    @POST("/clientRequest")
    Call<Result> executeClientRequest(@Body HashMap<String,String> map);
    @POST("/takePassenger")
    Call<Result> executeTakeRequest(@Body HashMap<String,String> map);
<<<<<<< HEAD
    @POST("/logRide")
    Call<Void> executeLogRide(@Body HashMap<String,String> map);
=======
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
    @GET("/driverNotify")
    Call<List<Result>> executeDriverNotify();
    @GET("/passengerNotify/{email}")
    Call <Result> executePassengerNotify(@Path("email") String email);
    @GET("/pickupInProgress/{driver}")
    Call <List<Result>> executePickupInProgress(@Path("driver") String email);
    @GET("/checkPassengerRequest/{email}")
    Call<Result> executeCheckRequest(@Path("email") String email);
    @DELETE("/closeClientRequest/{email}")
    Call<Void> executeCloseRequest(@Path("email") String email);
    @DELETE("/addClientRequest/{email}")
    Call<Void> executeAddRequest(@Path("email") String email);
}
