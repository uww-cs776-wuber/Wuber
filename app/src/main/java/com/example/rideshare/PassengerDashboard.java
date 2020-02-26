package com.example.rideshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PassengerDashboard extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public FirebaseLoginSignup auth = new FirebaseLoginSignup();
    private LocationManager locationManager;
    private LocationListener locationListener;
    public TextView locationTxt;
    String username;
    private String BASEURL="http://172.23.73.141:3000"; //current IP of machine.
    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_dashboard);

        retrofit= new Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build(); // Retrofit is used to make http requests to the server. GsonConverterFactory method converts JSON to Java Object

        retrofitInterface= retrofit.create(RetrofitInterface.class);

        auth.mAuth = FirebaseAuth.getInstance(); // Firebase reference not used now.

        TextView usernameTxt = (TextView) findViewById(R.id.username);
        final TextView timeTxt=(TextView) findViewById(R.id.timeTxt);

        username = getIntent().getStringExtra("welcome"); // get email from MainActivity
        String splitDomain[]=username.split("@");
        usernameTxt.setText(splitDomain[0]);

        locationTxt=(TextView)findViewById(R.id.location) ;

        final EditText destinationTxt=(EditText) findViewById(R.id.destinationTxt);
        //location service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationTxt.setText( "\t"+location.getLatitude() + ",\n\t" + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
                return;
            } else {
                getLocation();
            }
        }

        // Function to handle Request ride from client
        findViewById(R.id.requestRide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] userEmail = {""};
                final String[] destination={""};
                final String[] pickupTime={""};
                final String[] userLocation={""};
                if(!locationTxt.getText().toString().equals("") && !timeTxt.getText().toString().equals("") && !destinationTxt.getText().toString().equals("")){

                    // Send request to server and receiver response from server for client's email and pickup time

                    HashMap<String,String> emailTime= new HashMap<>();
                    emailTime.put("email",username);
                    emailTime.put("pickuptime",timeTxt.getText().toString());

                    Call<Result> callEmailTime = retrofitInterface.executePickupTime(emailTime);
                    callEmailTime.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            if (response.code() == 200) {
                                Result result= response.body();
                                userEmail[0] =result.getEmail();
                                pickupTime[0]=result.getPickupTime();
                                Toast.makeText(PassengerDashboard.this, result.getEmail()+" "+result.getPickupTime(), Toast.LENGTH_LONG).show();
                            } else if (response.code() == 400) {
                                Toast.makeText(PassengerDashboard.this, "Request Failed", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(PassengerDashboard.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    // Send request to server and receiver response from server for client's email and  destination

                    HashMap<String,String> emailDes= new HashMap<>();
                    emailDes.put("email",username);
                    emailDes.put("destination",destinationTxt.getText().toString());

                    Call<Result> callEmailDes = retrofitInterface.executeDesTime(emailDes);
                    callEmailDes.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            if (response.code() == 200) {
                                Result result= response.body();
                                userEmail[0] =result.getEmail();
                                destination[0]=result.getDestination();
                                Toast.makeText(PassengerDashboard.this, result.getEmail()+" "+result.getDestination(), Toast.LENGTH_LONG).show();
                            } else if (response.code() == 400) {
                                Toast.makeText(PassengerDashboard.this, "Request Failed", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(PassengerDashboard.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });



                    // Send request to server and receiver response from server for client's source location and email
                HashMap<String, String> hm = new HashMap<>();
                hm.put("email", username);
                hm.put("location", locationTxt.getText().toString());
                Call<Result> call = retrofitInterface.executeCLocate(hm);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        if (response.code() == 200) {
                            Result result= response.body();
                            userEmail[0]=result.getEmail();
                            userLocation[0]=result.getGpsCordinates();
                            rideNotificaton(userEmail[0],userLocation[0],destination[0],pickupTime[0]);
                            Toast.makeText(PassengerDashboard.this, result.getEmail()+" "+result.getGpsCordinates(), Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(PassengerDashboard.this, "Request Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(PassengerDashboard.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }
                else
                {
                    Toast.makeText(PassengerDashboard.this,"Please wait for your GPS location. \nPlease enter your destination and pickup time",Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation();
                return;

        }
    }


    public void rideNotificaton(String email, String userLocation, String destination, String pickupTime ){
        String username[]=email.split("@");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notify_001")
                .setSmallIcon(R.drawable.ic_ride)
                .setContentTitle("Pick up")
                .setContentText(username[0]+" wants a ride!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Passenger: "+username[0]+"\n"+"Location: "+userLocation+"\n"+"Destination: "+destination+"\n"+"Pickup Time: "+pickupTime)).setAutoCancel(true);

        NotificationManager
        mNotificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        mNotificationManager.notify(0, builder.build());
    }
    public void getLocation() {
        try {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        } catch (SecurityException se) {

        }
    }

    public void mapLocation(View view) {
        if (!locationTxt.getText().toString().equals("")) {
            Uri gmmIntentUri = Uri.parse("geo:"+locationTxt.getText().toString());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        else{
            Toast.makeText(PassengerDashboard.this,"Location field blank",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog alertDialog = new AlertDialog.Builder(PassengerDashboard.this).create();
        alertDialog.setTitle("Sign Out");
        alertDialog.setMessage("By pressing back you will be logged out. \nPress outside the dialog to cancel.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                       // auth.mAuth.signOut();
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                        finish();
                    }
                });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }


    public void setTime(View view) {
        DialogFragment timePicker= new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(),"time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView timeTxt=(TextView) findViewById(R.id.timeTxt);
        timeTxt.setText(hourOfDay+":"+minute);
    }
}
