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

public class RideRequest_RecyclerAdapter extends RecyclerView.Adapter<RideRequest_RecyclerAdapter.RequestViewHolder> {
    public RetrofitInterface retrofitInterface;
    public Retrofit retrofit;
    String request[],location[],destination[],email[],pickup[], driverName[], wheelChair[],uwwStudent[],elderly[],intoxicated[];
    Context context;
    String username="";
    String BASEURL = retrofitInterface.BASEURL;

    public RideRequest_RecyclerAdapter(Context ct, String req[], String em[], String loc[], String des[], String pick[], String user, String driver[], String wheels[], String uww[], String elder[], String intox[]){
        context=ct;
        request=req;
        location=loc;
        destination=des;
        email=em;
        pickup=pick;
        username=user;
        driverName=driver;
        wheelChair=wheels;
        uwwStudent=uww;
        elderly=elder;
        intoxicated=intox;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.request_row,viewGroup,false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder requestViewHolder,  final int i) {
        requestViewHolder.wheelChairIcon.setVisibility(View.INVISIBLE);
        requestViewHolder.uwwStdIcon.setVisibility(View.INVISIBLE);
        requestViewHolder.elderIcon.setVisibility(View.INVISIBLE);
        requestViewHolder.intoxIcon.setVisibility(View.INVISIBLE);
        if(wheelChair[i]!=null && elderly[i]!=null && uwwStudent[i]!=null && intoxicated[i]!=null) {
            if (wheelChair[i].equals("yes")) {
                requestViewHolder.wheelChairIcon.setVisibility(View.VISIBLE);
            }

            if (elderly[i].equals("yes")) {
                requestViewHolder.elderIcon.setVisibility(View.VISIBLE);
            }

            if (uwwStudent[i].equals("yes")) {
                requestViewHolder.uwwStdIcon.setVisibility(View.VISIBLE);
            }

            if (intoxicated[i].equals("yes")) {
                requestViewHolder.intoxIcon.setVisibility(View.VISIBLE);
            }
        }
        requestViewHolder.requestDetails.setText(request[i]); // request details of passenger get shown here at requestDetails.
        requestViewHolder.mapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Accept Request?");
                alertDialog.setMessage("By pressing Accept you will be accepting the passsenger's request. \nPress outside the dialog to cancel.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Accept",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if(!DriverDash.getInstance().driverLocation.equals(""))
                                    rideService(email[i],location[i],destination[i],pickup[i],username,DriverDash.getInstance().driverLocation,"no",wheelChair[i],elderly[i],intoxicated[i],uwwStudent[i]);
                                else
                                    Toast.makeText(context,"You do not have your GPS location available at the moment. \nPlease drag the nav bar to check if your GPS location is available.",Toast.LENGTH_LONG).show();
                            }
                        });
                alertDialog.setCancelable(true);
                alertDialog.show();
                           }
        });

        retrofit= new Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build(); // Retrofit is used to make http requests to the server. GsonConverterFactory method converts JSON to Java Object
        retrofitInterface= retrofit.create(RetrofitInterface.class);
    }

    public void rideService(String email, String location, String destination, String pickup, String username, String driverLocation, final String arrived, String wheelChair, String elderly, String intoxicated, String uwwStudent){

       if(arrived.equals("yes")) {
           Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);
           Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
           mapIntent.setPackage("com.google.android.apps.maps");
           context.startActivity(mapIntent);
       }

        HashMap<String,String> takePassenger= new HashMap<>();
        takePassenger.put("email",email);
        takePassenger.put("gpsCordinates",location);
        takePassenger.put("destination",destination);
        takePassenger.put("pickuptime",pickup);
        takePassenger.put("driver",username);
        takePassenger.put("driverLocation",driverLocation);
        takePassenger.put("arrived",arrived);
        if(elderly.equals("yes")){
            takePassenger.put("elderly","yes");
        }
        else
            takePassenger.put("elderly","no");
        if(uwwStudent.equals("yes")){
            takePassenger.put("uwwStudent","yes");
        }
        else
            takePassenger.put("uwwStudent","no");
        if(wheelChair.equals("yes")){
            takePassenger.put("wheelChair","yes");
        }
        else
            takePassenger.put("wheelChair","no");
        if(intoxicated.equals("yes")){
            takePassenger.put("intoxicated","yes");
        }
        else
            takePassenger.put("intoxicated","no");

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
        Call<Void> delete = retrofitInterface.executeAddRequest(email);
        delete.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Toast.makeText(context, "Ride request accepted!", Toast.LENGTH_LONG).show();
                } else if (response.code() == 400) {
                    Toast.makeText(context, "Error in accepting the request", Toast.LENGTH_LONG).show();
                }

                DriverDash.getInstance().getRequest();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return request.length;
    }

    public class RequestViewHolder extends  RecyclerView.ViewHolder {
        TextView requestDetails;
        ImageView clientImage, wheelChairIcon,uwwStdIcon,elderIcon,intoxIcon;
        ImageButton mapLocation;
        ConstraintLayout request_card;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestDetails=itemView.findViewById(R.id.ClientRequestDetails);
            clientImage=itemView.findViewById(R.id.ClientImage);
            mapLocation=itemView.findViewById(R.id.map);
            wheelChairIcon=itemView.findViewById(R.id.wheelIcon);
            uwwStdIcon=itemView.findViewById(R.id.uwwStdIcon);
            elderIcon=itemView.findViewById(R.id.elderIcon);
            intoxIcon=itemView.findViewById(R.id.intoxIcon);
            request_card=itemView.findViewById(R.id.request_card);
        }
    }
}
