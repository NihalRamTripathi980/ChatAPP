package com.example.nrtchatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.nrtchatsapp.Modelss.users;
import com.example.nrtchatsapp.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class profileActivity extends AppCompatActivity {

    ActivityProfileBinding binding ;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.setCancelable(false);



        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage =FirebaseStorage.getInstance();

        binding.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,21);
            }
        });

        binding.proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =binding.nameBox.getText().toString();
                if (name.isEmpty()){
                    binding.nameBox.setError("Please Enter Your Name");
                    return;
                }
                dialog.show();

                if (selectedImage!=null){

                    StorageReference reference = storage.getReference().child("Profile").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl =uri.toString();
                                        String uid = auth.getUid();
                                        String name = binding.nameBox.getText().toString();
                                        String phoneNumber = auth.getCurrentUser().getPhoneNumber();

                                        users users = new users(uid,name,phoneNumber,imageUrl);



                                        database.getReference()
                                                .child("users")
                                                .child(uid)
                                                .setValue(users)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialog.dismiss();

                                                        Intent intent = new Intent(profileActivity.this, MainActivity.class);
                                                        startActivity(intent);

                                                        finish();
                                                    }
                                                });

                                    }
                                });
                            }
                        }
                    });
                }
                else {

                    String uid = auth.getUid();

                    String phoneNumber = auth.getCurrentUser().getPhoneNumber();

                    users users = new users(uid,name,phoneNumber,"No Image");



                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(users)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();

                                    Intent intent = new Intent(profileActivity.this,MainActivity.class);
                                    startActivity(intent);

                                    finish();
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null){
            if (data.getData()!= null){
                binding.profilePic.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }
    }
}