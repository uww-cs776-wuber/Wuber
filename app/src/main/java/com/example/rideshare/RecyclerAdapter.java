package com.example.rideshare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RequestViewHolder> {
    public RetrofitInterface retrofitInterface;
    public Retrofit retrofit;
    String request[];
    String location[];
    String destination[];
    String email[];
    String intoxicated[];
    Context context;
    String BASEURL = retrofitInterface.BASEURL;



    public  RecyclerAdapter(Context ct, String req[], String em[], String loc[], String des[], String intox[]){
        context=ct;
        request=req;
        location=loc;
        destination=des;
        email=em;
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


        requestViewHolder.requestDetails.setText(request[i]);

        if (intoxicated[i].equals("true")) {
            requestViewHolder.intoxicatedImage.setImageResource(R.drawable.intoxicated);
        }

        Log.d(request[i], "------REQUEST-------");
        requestViewHolder.mapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+location[i]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
        requestViewHolder.driverLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // restService.deleteClientRequest(email[i]);
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+destination[i]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        retrofit= new Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build(); // Retrofit is used to make http requests to the server. GsonConverterFactory method converts JSON to Java Object

        retrofitInterface= retrofit.create(RetrofitInterface.class);

        requestViewHolder.close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return request.length;
    }

    public class RequestViewHolder extends  RecyclerView.ViewHolder {
        TextView requestDetails;
        ImageView clientImage;
        ImageView intoxicatedImage;
        ImageButton mapLocation, close, driverLocation;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestDetails=itemView.findViewById(R.id.ClientRequestDetails);
            clientImage=itemView.findViewById(R.id.ClientImage);
            intoxicatedImage=itemView.findViewById(R.id.IntoxicatedImage);
            mapLocation=itemView.findViewById(R.id.map);
            close=itemView.findViewById(R.id.close);
            driverLocation=itemView.findViewById(R.id.driverLocation);

        }
    }
}
