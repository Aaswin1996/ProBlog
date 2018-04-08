package com.developer.aaswin.problog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar mToolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    @BindView(R.id.Main_addBlog)
    FloatingActionButton addBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFirestore=FirebaseFirestore.getInstance();
        mToolbar = findViewById(R.id.MainActivity_toolbar);
        mAuth=FirebaseAuth.getInstance();

        //Setting uo Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ProBlog");
        
        addBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBlog();
            }
        });

    }

    private void addNewBlog() {
        startActivity(new Intent(getApplicationContext(),NewBlogActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLoginActivity();

        }
        else{
            String UID=mAuth.getCurrentUser().getUid();
            mFirestore.collection("Users").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(!task.getResult().exists()){
                    startActivity(new Intent(MainActivity.this,SetupActivity.class));
                    }

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_logout_button:
                mAuth.signOut();
                sendToLoginActivity();
                return true;
            case R.id.menu_accountSettings:
                sendToSetupActivity();
                return true;
            default:
                return false;
        }
    }

    private void sendToSetupActivity() {
        startActivity(new Intent(getApplicationContext(),SetupActivity.class));
    }

    private void sendToLoginActivity() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}
