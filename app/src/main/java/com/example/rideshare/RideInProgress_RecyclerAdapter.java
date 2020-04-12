package com.example.rideshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RideInProgress_RecyclerAdapter extends RecyclerView.Adapter<RideInProgress_RecyclerAdapter.RequestViewHolder>{

    public RetrofitInterface retrofitInterface;
    public Retrofit retrofit;
    String request[],location[],destination[],email[],pickup[], driverName[];
    Context context;
    String username="";
    String BASEURL = retrofitInterface.BASEURL;

    public RideInProgress_RecyclerAdapter(Context ct, String req[], String em[], String loc[], String des[], String pick[], String user, String driver[]){
        context=ct;
        request=req;
        location=loc;
        destination=des;
        email=em;
        pickup=pick;
        username=user;
        driverName=driver;
    }

    @NonNull
    @Override
    public RideInProgress_RecyclerAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.ride_in_progress_row,viewGroup,false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideInProgress_RecyclerAdapter.RequestViewHolder requestViewHolder, final int i) {
        requestViewHolder.requestDetails.setText("Ride In Progress\n"+request[i]);

        requestViewHolder.mapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    Toast.makeText(context,DriverDash.getInstance().driverLocation,Toast.LENGTH_LONG).show();
                if(!DriverDash.getInstance().driverLocation.equals(""))
                    rideService(email[i],location[i],destination[i],pickup[i],username,DriverDash.getInstance().driverLocation,"no");
                else
                    Toast.makeText(context,"You do not have your GPS location available at the moment. \nPlease drag the nav bar to check if your GPS location is available.",Toast.LENGTH_LONG).show();
            }
        });
        requestViewHolder.driverLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DriverDash.getInstance().driverLocation.equals(""))
                    rideService(email[i],location[i],destination[i],pickup[i],username,DriverDash.getInstance().driverLocation,"yes");
                else
                    Toast.makeText(context,"You do not have your GPS location available at the moment.\nPlease drag the nav bar to check if your GPS location is available.",Toast.LENGTH_LONG).show();
            }
        });

        retrofit= new Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build(); // Retrofit is used to make http requests to the server. GsonConverterFactory method converts JSON to Java Object

        retrofitInterface= retrofit.create(RetrofitInterface.class);

        requestViewHolder.close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DeleteRequest(i);
            }
        });

    }
    public void DeleteRequest(final int i){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Ride Request Complete");
        alertDialog.setMessage("By pressing Terminate you will be terminating the ride request for this passenger.\nPress outside the dialog to cancel");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Terminate",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Call<Void> call = retrofitInterface.executeCloseRequest(email[i]);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(context, "Ride request done", Toast.LENGTH_LONG).show();
                                } else if (response.code() == 400) {
                                    Toast.makeText(context, "Error in closing the request", Toast.LENGTH_LONG).show();
                                }

                                DriverDash.getInstance().getRequest();
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    }
                });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public void rideService(String email, String location, String destination, String pickup, String username, String driverLocation, final String arrived){

        if(arrived.equals("yes")) {
            location=destination;
        }
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);

        HashMap<String,String> takePassenger= new HashMap<>();
        takePassenger.put("email",email);
        takePassenger.put("gpsCordinates",location);
        takePassenger.put("destination",destination);
        takePassenger.put("pickuptime",pickup);
        takePassenger.put("driver",username);
        takePassenger.put("driverLocation",driverLocation);
        takePassenger.put("arrived",arrived);
        Call<Result> call = retrofitInterface.executeTakeRequest(takePassenger);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200) {
                    Result result= response.body();
                    if(result.getArrived().equals("no"))
                        Toast.makeText(context, "Ride request for "+result.getEmail()+" accepted. \nPickup time: "+result.getPickupTime(), Toast.LENGTH_LONG).show();
                    else if(result.getArrived().equals("yes"))
                        Toast.makeText(context, "Notifying "+result.getEmail()+" of your arrival. \nYour location has been shared to the passenger", Toast.LENGTH_LONG).show();
                } else if (response.code() == 400) {
                    Toast.makeText(context, "Request Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return request.length;
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView requestDetails;
        ImageView clientImage;
        ImageButton mapLocation, close, driverLocation;
        ConstraintLayout request_card;

    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
        requestDetails=itemView.findViewById(R.id.ClientRequestDetails);
        clientImage=itemView.findViewById(R.id.ClientImage);
        mapLocation=itemView.findViewById(R.id.map);
        close=itemView.findViewById(R.id.close);
        driverLocation=itemView.findViewById(R.id.driverLocation);
        request_card=itemView.findViewById(R.id.request_card);
        }
    }
}
