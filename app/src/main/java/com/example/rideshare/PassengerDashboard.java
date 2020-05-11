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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
<<<<<<< HEAD
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
=======
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
<<<<<<< HEAD

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

=======
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PassengerDashboard extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, Runnable {
    // public FirebaseLoginSignup auth = new FirebaseLoginSignup();
    private LocationManager locationManager;
    private LocationListener locationListener;
    public TextView locationTxt, timeTxt, usernameTxt;
<<<<<<< HEAD
    public EditText destinationTxt;
    String username, mapDriverLocation = "";
    private Thread worker;

=======
    public CheckBox wheelChair, uwwStudent, elderly, intoxcicated;
    public EditText destinationTxt;
    String username, mapDriverLocation = "";
    private Thread worker;
    public int hr=0,min=0;
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
    public AES_encrpyt encryption = new AES_encrpyt();
    public String EncryptedEmail = "", EncryptedPassword = "", EncrpytedUserType = "", DecryptedEmail = "", DecryptedUserType = "", EncryptedUserLocation = "", EncryptedUserDestination = "", EncryptedPickupTime = "";

    private final AtomicBoolean running = new AtomicBoolean(false); // boolean flag for Passenger details Thread
    private RetrofitInterface retrofitInterface;
    private String BASEURL = retrofitInterface.BASEURL;
    private Retrofit retrofit;
    public TextView driverUsername;
    public String passengerLocation = "";
    public String Status="false";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_dashboard);


        retrofit = new Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build(); // Retrofit is used to make http requests to the server. GsonConverterFactory method converts JSON to Java Object

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        //  auth.mAuth = FirebaseAuth.getInstance(); // Firebase reference not used now.

        usernameTxt = (TextView) findViewById(R.id.username);
        timeTxt = (TextView) findViewById(R.id.timeTxt);
        username = getIntent().getStringExtra("welcome"); // get email from MainActivity
        usernameTxt.setText(username);
        checkRideStatus(username);
        findViewById(R.id.driverInfo).setVisibility(View.INVISIBLE);
<<<<<<< HEAD
=======
        wheelChair=(CheckBox) findViewById(R.id.wheelChair);
        uwwStudent=(CheckBox) findViewById(R.id.uwwStudent);
        elderly=(CheckBox) findViewById(R.id.elderly);
        intoxcicated=(CheckBox) findViewById(R.id.intoxicated);
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
        locationTxt = (TextView) findViewById(R.id.location);
        destinationTxt = (EditText) findViewById(R.id.destinationTxt);
        getRequest();
        start(); // start the get passenger request thread. This thread executes every 30 seconds
        //location service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationTxt.setText("\t" + location.getLatitude() + ", " + location.getLongitude());
                passengerLocation = location.getLatitude() + "," + location.getLongitude();
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
<<<<<<< HEAD
=======
        wheelChair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(PassengerDashboard.this,"Passenger is requesting for wheelchair",Toast.LENGTH_SHORT).show();
                }
            }
        });
        uwwStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(PassengerDashboard.this,"Passenger is a UWW Student",Toast.LENGTH_SHORT).show();
                }
            }
        });
        intoxcicated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(PassengerDashboard.this,"Passenger is intoxicated",Toast.LENGTH_SHORT).show();
                }
            }
        });
        elderly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(PassengerDashboard.this,"Passenger is elderly",Toast.LENGTH_SHORT).show();
                }
            }
        });
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672

        // Function to handle Request ride from client
        findViewById(R.id.requestRide).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String status=checkRideStatus(username);
                if (status.equals(username)) {
                    Toast.makeText(PassengerDashboard.this, "Your request has been accepted. The Driver will be here shortly.", Toast.LENGTH_SHORT).show();
                } else {
                    // Encrypt all the user request details using Encryption class and its getMessages function
                    try {
                        EncryptedEmail = encryption.getEncryptedData(username);
                        EncryptedUserLocation = encryption.getEncryptedData(passengerLocation);
                        EncryptedUserDestination = encryption.getEncryptedData(destinationTxt.getText().toString());
                        EncryptedPickupTime = encryption.getEncryptedData(timeTxt.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // This above try/catch part is for the security part only so SoftwareEngineering class do not need to care for this

                    if (!locationTxt.getText().toString().equals("") && !timeTxt.getText().toString().equals("") && !destinationTxt.getText().toString().equals("")) {
                        // Send encrypted ride request by client to server and receive response from server
                        HashMap<String, String> clientRequest = new HashMap<>();
                        clientRequest.put("email", EncryptedEmail);
                        clientRequest.put("destination", EncryptedUserDestination);
                        clientRequest.put("gpsCordinates", EncryptedUserLocation);
                        clientRequest.put("pickuptime", EncryptedPickupTime);

<<<<<<< HEAD
=======
                        if(wheelChair.isChecked()){
                            clientRequest.put("wheelChair","yes");
                        }else
                            clientRequest.put("wheelChair","no");

                        if(intoxcicated.isChecked()){
                            clientRequest.put("intoxicated","yes");
                        }else
                            clientRequest.put("intoxicated","no");

                        if(elderly.isChecked()){
                            clientRequest.put("elderly","yes");
                        }else
                            clientRequest.put("elderly","no");

                        if(uwwStudent.isChecked()){
                            clientRequest.put("uwwStudent","yes");
                        }else
                            clientRequest.put("uwwStudent","no");

>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
                        Call<Result> call = retrofitInterface.executeClientRequest(clientRequest);
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                if (response.code() == 200) {
                                    Result result = response.body();
                                    Toast.makeText(PassengerDashboard.this, "Requested By "+result.getEmail() + " Pickup Time: " + result.getPickupTime(), Toast.LENGTH_LONG).show();
                                } else if (response.code() == 400) {
                                    Toast.makeText(PassengerDashboard.this, "Request Failed", Toast.LENGTH_LONG).show();
                                }
                            }

                             @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Toast.makeText(PassengerDashboard.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(PassengerDashboard.this, "Please wait for your GPS location. \nPlease enter your destination and pickup time", Toast.LENGTH_SHORT).show();
                    }
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


    public void getLocation() {
        try {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        } catch (SecurityException se) {

        }
    }

    public void driverNotifier(String drivername, String title, String message) {
        String user[] = username.split("@");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notify_001")
                .setSmallIcon(R.drawable.ic_ride)
                .setContentTitle("Hey " + user[0] + " " + title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)).setAutoCancel(true);

        NotificationManager
                mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    public void mapLocation(View view) {
        if (!mapDriverLocation.equals("")) {
            Uri gmmIntentUri = Uri.parse("geo:" + mapDriverLocation);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            Toast.makeText(PassengerDashboard.this, "Sorry the driver is not here. You will be notified once the driver is here to get you.", Toast.LENGTH_LONG).show();
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
                        interrupt();
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

<<<<<<< HEAD

=======
>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
    public void setTime(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView timeTxt = (TextView) findViewById(R.id.timeTxt);
<<<<<<< HEAD
        timeTxt.setText(hourOfDay + ":" + minute);
    }

=======
        hr=hourOfDay;
        min=minute;
        if(checkTime(hr,min).equals("true")){
            timeTxt.setText(hourOfDay + ":" + minute);
        }
        else
            timeTxt.setText("");

    }

    public String checkTime(int hour, int min) {
        String timeStat="true";
        Date currentTime = Calendar.getInstance().getTime();
        int currentHr=currentTime.getHours();
        int currentMin=currentTime.getMinutes();
        if( currentHr>hour) {
            timeStat="false";
            Toast.makeText(PassengerDashboard.this, "The Time you selected has already passed. Please select a valid time.",Toast.LENGTH_LONG).show();
        }
        if( currentMin> min){
            if(currentHr>=hour){
                timeStat="false";
                Toast.makeText(PassengerDashboard.this,"The Time you selected has already passed. Please select a valid time.",Toast.LENGTH_LONG).show();
            }
        }
    return timeStat;
    }


>>>>>>> 7a9e0b99345cc93cc8ba96182ff9e7c90994e672
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
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            getRequest(); // Get passenger request function.
        }
    }

    public void getRequest() {
        driverUsername = findViewById(R.id.driverInfo);
        Call<Result> call = retrofitInterface.executePassengerNotify(username);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200) {
                    Result result = response.body();
                    //  Toast.makeText(PassengerDashboard.this, result.getDriver()+" "+result.getDriverLocation(), Toast.LENGTH_LONG).show();
                    if (result.getArrived().equals("yes")) {
                        driverUsername.setText("Driver :" + result.getDriver() + "\n" + "Location: " + result.getDriverLocation());
                        mapDriverLocation = result.getDriverLocation();
                        driverNotifier(result.getDriver(), "your ride is here!","Your driver for today is " + result.getDriver() + ".\nClick on the Red map icon on your dashboard to view the driver's location.");
                    } else if (result.getArrived().equals("no")) {
                        driverNotifier(result.getDriver(), "your ride will be here shortly.","Your driver for today is "+ result.getDriver()+". and will be there soon");
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(PassengerDashboard.this, "Error in closing the request", Toast.LENGTH_LONG).show();
                }

                //   DriverDash.getInstance().getRequest();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(PassengerDashboard.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public String checkRideStatus(String username) {
        Call<Result> call = retrofitInterface.executeCheckRequest(username);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200) {
                   Result result= response.body();
                      Status=result.getEmail();
                } else if (response.code() == 400) {
                    Toast.makeText(PassengerDashboard.this, "Error in fetching passenger ride status", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(PassengerDashboard.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return Status;
    }

}
