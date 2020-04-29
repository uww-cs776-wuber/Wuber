package com.example.rideshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseLoginSignup extends AppCompatActivity {
public FirebaseAuth mAuth;
public FirebaseAuth.AuthStateListener mAuthListener;
private EditText txtEmail,txtPassword;
private Button btnSignIn, btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firebase_login_signup);
         txtEmail=(EditText) findViewById(R.id.emailText);
         txtPassword= (EditText) findViewById(R.id.passwordText);
         btnSignIn=(Button) findViewById(R.id.signIn);
         btnSignUp=(Button) findViewById(R.id.signUp);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user!=null){
                   // Log.d("","onAuthStateChanged:signed_in"+user.getUid());
                }
                else{
                //    toastMsg("SignIn Unsuccessful");
                }
            }
        };
    btnSignIn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email= txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            if(!email.equals("")&& !password.equals("")){
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(FirebaseLoginSignup.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()) {
                            toastMsg("email and password do not match");
                        } else {
                            toastMsg("SignIn Successful");
                            startActivity(new Intent(FirebaseLoginSignup.this, PassengerDashboard.class));
                        }
                    }
                });
            }
            else
            {
                toastMsg("Please type in your email and password");
            }
        }
    });

    btnSignUp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email= txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            if(!email.equals("")&& !password.equals("")){
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(FirebaseLoginSignup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            toastMsg("email already exists");
                        } else {
                            toastMsg("Sign Up Successful");
                            startActivity(new Intent(FirebaseLoginSignup.this, FirebaseLoginSignup.class));
                        }
                    }
                });
            }
            else
            {
                toastMsg("Please type in your email and password");
            }
        }
    });
    }

    private void toastMsg(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
