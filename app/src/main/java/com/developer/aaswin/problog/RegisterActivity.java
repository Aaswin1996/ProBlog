package com.developer.aaswin.problog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText mEmail, mPassword, mConfirmPassword;
    Button mRegister, mAlreadyHaveAnccount;
    ProgressBar mProgress;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmail = findViewById(R.id.Register_email);
        mPassword = findViewById(R.id.Register_Password);
        mConfirmPassword = findViewById(R.id.Register_Matchpassword);
        mRegister = findViewById(R.id.Register_CreateButton);
        mAlreadyHaveAnccount = findViewById(R.id.Register_sendToLoginButton);
        mProgress = findViewById(R.id.Register_progress);
        mAuth = FirebaseAuth.getInstance();

        mAlreadyHaveAnccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLoginActivity();
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setVisibility(View.VISIBLE);
                if (mEmail != null && mPassword != null && mConfirmPassword != null) {

                    String Email = mEmail.getText().toString().trim();
                    String Password = mPassword.getText().toString().trim();
                    String MatchPassword = mConfirmPassword.getText().toString().trim();
                    if (Password.equals(MatchPassword)) {
                        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //sendToMainActivity();
                                    startActivity(new Intent(getApplicationContext(),SetupActivity.class));
                                    mProgress.setVisibility(View.INVISIBLE);
                                } else {
                                    Log.d("RegisterActivity: ", task.getException().toString());

                                }
                            }
                        });
                    }
                    else{
                        mConfirmPassword.setError("Passwords do not match");
                    }

                }
                mProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void sendToMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void sendToLoginActivity() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}

