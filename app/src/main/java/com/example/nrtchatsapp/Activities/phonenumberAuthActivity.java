package com.example.nrtchatsapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nrtchatsapp.databinding.ActivityPhonenumberAuthBinding;
import com.google.firebase.auth.FirebaseAuth;

public class phonenumberAuthActivity extends AppCompatActivity {

     ActivityPhonenumberAuthBinding binding ;

     FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPhonenumberAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            Intent intent=new Intent(phonenumberAuthActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        getSupportActionBar().hide();
        binding.phoneBox.requestFocus();


        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(phonenumberAuthActivity.this, OTP_Activity.class);
                i.putExtra("number",binding.phoneBox.getText().toString());
                startActivity(i);
            }
        });
    }
}