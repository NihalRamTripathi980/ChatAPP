package com.example.nrtchatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.nrtchatsapp.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTP_Activity extends AppCompatActivity {

    ActivityOtpBinding binding ;
    FirebaseAuth auth ;
    String verificationID;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        dialog= new ProgressDialog(OTP_Activity.this);
        dialog.setMessage("OTP Sending");
        dialog.setCancelable(false);
        dialog.show();

        auth= FirebaseAuth.getInstance();
        String phoneNumber = getIntent().getStringExtra("number");
        binding.numberView.setText("Verify - " + phoneNumber);

        PhoneAuthOptions  options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTP_Activity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull  FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull  String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);

                        verificationID=verifyID;
                        dialog.dismiss();
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential =PhoneAuthProvider.getCredential(verificationID,otp);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Intent intent = new Intent(OTP_Activity.this, profileActivity.class);
                            startActivity(intent);
                            finishAffinity();

                        }
                        else {
                            Toast.makeText(OTP_Activity.this,"Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}