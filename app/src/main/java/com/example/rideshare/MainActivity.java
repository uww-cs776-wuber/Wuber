package com.example.rideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
private RetrofitInterface retrofitInterface;
private Retrofit retrofit;
private String BASEURL= retrofitInterface.BASEURL;
private EditText txtEmail,txtPassword;
public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrofit= new Retrofit.Builder().baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Retrofit is used to make requests to the server. GsonConverterFactory method converts JSON to Java Object

        retrofitInterface= retrofit.create(RetrofitInterface.class);
        txtEmail=(EditText) findViewById(R.id.emailText);
        txtPassword= (EditText) findViewById(R.id.passwordText);

        final String userType=getIntent().getStringExtra("userType");
        if(userType.equals("driver")){
            findViewById(R.id.signUp).setVisibility(View.INVISIBLE);
        }

        //Handle user Login authentication
        findViewById(R.id.signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestService restService= new RestService();
                String type="";
                if (!txtEmail.getText().toString().equals("") && !txtPassword.getText().toString().equals(""))
                { HashMap<String,String> hm = new HashMap<>();
                hm.put("email",txtEmail.getText().toString());
                hm.put("password",txtPassword.getText().toString());
                hm.put("userType",userType);
                    Call<Result> call = retrofitInterface.executeLogin(hm); // body of post request to send to nodejs server
                    call.enqueue(new Callback<Result>() { // call the http request.
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            // This function is called when the http request is successful
                            if(response.code()==200){
                                Result result= response.body();
                                Toast.makeText(MainActivity.this,"Welcome: "+result.getEmail(),Toast.LENGTH_SHORT).show();
                                if(result.getUserType().equals("driver")) {
                                  goToDriverDashboard(result.getEmail());
                                }
                                else if(result.getUserType().equals("passenger")){
                                   goToPassengerDashboard(result.getEmail());
                                }
                            }
                            else if(response.code()==404){
                                Toast.makeText(MainActivity.this,"User not found",Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            // This function is called when the http request fails.
                             Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            else
                {
                    Toast.makeText(MainActivity.this,"email and password cannot be blank.",Toast.LENGTH_LONG).show();
                }
            }
        });

        //Handle User SignUp
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtEmail.getText().toString().equals("") && !txtPassword.getText().toString().equals("") && txtEmail.getText().toString().matches(emailPattern) && txtEmail.getText().toString().length()>0 && txtPassword.getText().toString().length()>8) {
                    HashMap<String, String> hm = new HashMap<>();
                    hm.put("email", txtEmail.getText().toString());
                    hm.put("password", txtPassword.getText().toString());
                    hm.put("userType",userType);
                    Call<Void> call = retrofitInterface.executeSignup(hm);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() == 200) {
                                Toast.makeText(MainActivity.this, "Sign In Successful", Toast.LENGTH_LONG).show();
                            } else if (response.code() == 400) {
                                Toast.makeText(MainActivity.this, "Email already exists!", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Please enter valid email and password. \nPassword length should be > 8.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Passing email id of logged in user from MainActivity.class to PassengerDashboard.class
    public void goToPassengerDashboard(String email){
        txtPassword.setText("");
        Intent intent = new Intent(MainActivity.this, PassengerDashboard.class);
        intent.putExtra("welcome", email);
        startActivity(intent);
    }

    public void goToDriverDashboard(String email){
        txtPassword.setText("");
        Intent intent = new Intent(MainActivity.this, DriverDash.class);
        intent.putExtra("welcome", email);
        startActivity(intent);
    }

}
