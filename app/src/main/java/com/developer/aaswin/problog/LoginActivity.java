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

public class LoginActivity extends AppCompatActivity {

    EditText Email,Password;
    Button Login,SignUp;
    private FirebaseAuth mAuth;
    String TAG="LOGIN ACTIVITY :";
    private ProgressBar mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        Email=findViewById(R.id.Login_email);
        Password=findViewById(R.id.Login_password);
        SignUp=findViewById(R.id.login_sendToSignUpButton);
        Login=findViewById(R.id.login_loginButton);
        mProgress=findViewById(R.id.Login_progressBar);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email=Email.getText().toString().trim();
                String password=Password.getText().toString().trim();
                if(email!=null && password!=null) {
                    mProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        mProgress.setVisibility(View.INVISIBLE);
                                        sendUserToMainActivity();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                    mProgress.setVisibility(View.INVISIBLE);

                                    // ...
                                }
                            });
                }



            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendtoRegisterActivity();
            }
        });

    }

    private void sendtoRegisterActivity() {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        finish();
    }

    private void sendUserToMainActivity() {

        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null){
            sendUserToMainActivity();
        }
    }
}
