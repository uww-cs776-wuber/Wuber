package com.example.rideshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverDash extends AppCompatActivity implements Runnable {

    private AppBarConfiguration mAppBarConfiguration;
    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;
    private String BASEURL= retrofitInterface.BASEURL;
    private Thread worker;

    private final AtomicBoolean running = new AtomicBoolean(false); // boolean flag for Passenger details Thread
    RecyclerView recyclerView, recyclerView2;
    TextView navHeader, navLocation;
    public  static DriverDash driverDash;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static DriverDash getInstance(){ //For creating object of this activity
        return driverDash;
    }
    public String driverLocation="";
    public String username="";
    public AES_encrpyt encryption= new AES_encrpyt();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverDash=this;
        username=getIntent().getStringExtra("welcome");
        setContentView(R.layout.activity_driver_dash);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);
        navHeader = (TextView)header.findViewById(R.id.NavHeader);
        navLocation = (TextView)header.findViewById(R.id.NavLocation);
        navHeader.setText(username);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        retrofit= new Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build(); // Retrofit is used to make http requests to the server. GsonConverterFactory method converts JSON to Java Object
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView2= (RecyclerView) findViewById(R.id.recyclerView2);
        retrofitInterface= retrofit.create(RetrofitInterface.class);

        getRequest(); // Get details of all passenger request on recycler view

        start(); // start the get passenger request thread. This thread executes every 30 seconds
        //Location service to get GPS location of driver
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                driverLocation=location.getLatitude()+","+location.getLongitude();
                navLocation.setText("\nGPS : "+driverLocation);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // To check the Android version to access location permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
                return;
            } else {
                getLocation();
            }
        }

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


    public void getLocation() {
        try {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        } catch (SecurityException se) {

        }
    }

    public void getRequest(){
        Call<List<Result>> call = retrofitInterface.executeDriverNotify();
        call.enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                String displayRequest="";
                if (response.code() == 200) {
                    List<Result> results= response.body();
                    String reqArray[]=new String[results.size()];
                    String locationArray[]= new String[results.size()];
                    String destinationArray[]= new String[results.size()];
                    String emailArray[]= new String[results.size()];
                    String pickupArray[]= new String[results.size()];
                    String driverArray[]= new String[results.size()];
                    int i=0;
                    for(Result result: results){
                        displayRequest="";
                        displayRequest+="Passenger: "+result.getEmail()+"\n";
                    //    displayRequest+="Location: "+result.getGpsCordinates()+"\n";
                        displayRequest+="Destination: "+result.getDestination()+"\n";
                        displayRequest+="Pickup Time: "+result.getPickupTime()+"\n";
                        reqArray[i]= displayRequest;
                        emailArray[i]=result.getEmail();
                        locationArray[i]= result.getGpsCordinates();
                        destinationArray[i]= result.getDestination();
                        pickupArray[i]= result.getPickupTime();
                        driverArray[i]="";
                        i++;
                        rideNotificaton(result.getEmail(),result.getGpsCordinates(),result.getDestination(),result.getPickupTime());
                    }
                    RideRequest_RecyclerAdapter rideRequestRecyclerAdapter = new RideRequest_RecyclerAdapter(DriverDash.this,reqArray,emailArray,locationArray,destinationArray, pickupArray,username,driverArray);
                    recyclerView.setAdapter(rideRequestRecyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(DriverDash.this));
                } else if (response.code() == 400) {
                    Toast.makeText(DriverDash.this, "Request Failed", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Result>> call, Throwable t) {
            }
        });
        getRideInProgress();
    }
    public void getRideInProgress(){
        Call<List<Result>>  call = retrofitInterface.executePickupInProgress(username);
        call.enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                String displayRequest="";
                if (response.code() == 200) {
                    List<Result> results= response.body();
                    String reqArray[]=new String[results.size()];
                    String locationArray[]= new String[results.size()];
                    String destinationArray[]= new String[results.size()];
                    String emailArray[]= new String[results.size()];
                    String pickupArray[]= new String[results.size()];
                    String driverArray[]= new String[results.size()];
                    int i=0;
                    for(Result result: results){
                        displayRequest="";
                        displayRequest+="Passenger: "+result.getEmail()+"\n";
                    //    displayRequest+="Location: "+result.getGpsCordinates()+"\n";
                        displayRequest+="Destination: "+result.getDestination()+"\n";
                        displayRequest+="Pickup Time: "+result.getPickupTime()+"\n";
                        displayRequest+="Driver: "+result.getDriver();
                        reqArray[i]= displayRequest;
                        emailArray[i]=result.getEmail();
                        locationArray[i]= result.getGpsCordinates();
                        destinationArray[i]= result.getDestination();
                        pickupArray[i]= result.getPickupTime();
                        driverArray[i]=result.getDriver();
                        i++;
                    }

                  RideInProgress_RecyclerAdapter rideInProgress_recyclerAdapter = new RideInProgress_RecyclerAdapter(DriverDash.this,reqArray,emailArray,locationArray,destinationArray, pickupArray,username,driverArray);
                    recyclerView2.setAdapter(rideInProgress_recyclerAdapter);
                    recyclerView2.setLayoutManager(new LinearLayoutManager(DriverDash.this));
                } else if (response.code() == 400) {
                    Toast.makeText(DriverDash.this, "Not Found", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Result>> call, Throwable t) {
            }
        });

    }
    public void rideNotificaton(String email, String userLocation, String destination, String pickupTime ){
        String username[]=email.split("@");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notify_001")
                .setSmallIcon(R.drawable.ic_ride)
                .setContentTitle("Pick up request")
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
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog alertDialog = new AlertDialog.Builder(DriverDash.this).create();
        alertDialog.setTitle("Sign Out");
        alertDialog.setMessage("By pressing back you will be logged out. \nPress outside the dialog to cancel.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        interrupt(); // interrupt passenger details thread after sign out.
                        dialog.dismiss();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_dash, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void start() { //function to start the thread
        worker = new Thread(this);
        worker.start();
    }

    public void stop() { // function to stop thread
        running.set(false);
    }

    public void interrupt() { // function to interrupt the thread
        running.set(false);
        worker.interrupt();
    }
    @Override
    public void run() { // Run the thread.
        running.set(true);
        while (running.get()) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            getRequest();
           // Get passenger request function.
        }
    }
}
