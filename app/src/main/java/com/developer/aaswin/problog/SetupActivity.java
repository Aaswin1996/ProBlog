package com.developer.aaswin.problog;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    Toolbar mToolbar;
    CircleImageView mImage;
    Uri mainImageURI = null;
    FirebaseAuth mAUth;
    FirebaseStorage mDatabase;
    StorageReference mStorage;
    FirebaseFirestore mFirestore;
    Boolean isChanged=false;
    @BindView(R.id.Setup_ProfilePic)
    CircleImageView mProfileImage;
    @BindView(R.id.setup_userName)
    EditText userName;
    @BindView(R.id.setup_saveSettings_btn)
    Button saveSettings;
    @BindView(R.id.setup_Progress)
    ProgressBar mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mToolbar = findViewById(R.id.Setup_toolbar);
        mImage = findViewById(R.id.Setup_ProfilePic);
        ButterKnife.bind(this);


        //Setting up Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");

        //Firebase References
        mDatabase = FirebaseStorage.getInstance();
        mFirestore=FirebaseFirestore.getInstance();
        mAUth = FirebaseAuth.getInstance();
        final String UserId = mAUth.getCurrentUser().getUid();

        //Retrieving Data from Firestore
        mProgress.setVisibility(View.VISIBLE);
        saveSettings.setEnabled(false);
        mFirestore.collection("Users").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String Name=documentSnapshot.getString("Name");
                    String image=documentSnapshot.getString("Download URL");

                    userName.setText(Name);
                    mainImageURI = Uri.parse(image);
                    RequestOptions placeholderRequest = new RequestOptions();
                    placeholderRequest.placeholder(R.drawable.blank_avatar);
                    Glide.with(SetupActivity.this).applyDefaultRequestOptions(placeholderRequest).load(image).into(mProfileImage);



                }
                else{
                    userName.setText("Set your Name here");
                }
                mProgress.setVisibility(View.INVISIBLE);
                saveSettings.setEnabled(true);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetupActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });



        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(SetupActivity.this);
                    }
                }
            }
        });

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChanged) {
                    mProgress.setVisibility(View.VISIBLE);
                    Log.d("SetupActivity: ", "Storing Image to Firebase");

                    mStorage = mDatabase.getReference("ProBlog").child(UserId).child("Images").child("ProfileImage").child(UserId + ".jpg");
                    UploadTask task = mStorage.putFile(mainImageURI);
                    task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storeToFireStore(taskSnapshot, UserId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("SetupActivity: ", e.getMessage().toString());

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
                }
                else{
                    storeToFireStore(null,UserId);
                }
            }
        });


    }

    private void storeToFireStore(UploadTask.TaskSnapshot taskSnapshot, String userId) {
        Uri ImageUri;
        if(taskSnapshot!=null){
             ImageUri= taskSnapshot.getDownloadUrl();
        }
        else{
            ImageUri=mainImageURI;
        }
        mProgress.setVisibility(View.INVISIBLE);
        Map<String,String> userData=new HashMap<>();
        userData.put("Name",userName.getText().toString());
        userData.put("Download URL",ImageUri.toString());
        mFirestore.collection("Users").document(userId).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("SetupActivity:","stored to Firestore");
                    startActivity(new Intent(SetupActivity.this,MainActivity.class));
                }
                else{
                    Log.d("SetupActivity: ","Error Occured in storing data to Firestore");
                }
            }
        });
        Log.d("SetupActivity: ", "Successfully stored Image to FirebaseStorage");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                mImage.setImageURI(mainImageURI);
                isChanged=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
