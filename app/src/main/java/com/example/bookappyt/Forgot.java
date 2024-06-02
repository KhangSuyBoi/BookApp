package com.example.bookappyt;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookappyt.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(v ->  {
            public void onClick(View v){
                onBackPressed();
            }
        });
        binding.submitBtn.setOnClickListener(v -> {
            public void onClick(View v){
                validateData()
            }
        });
    }
    private String email = "";
    private void validateData(){
        email = binding.emailEt.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Invalid Email Format");
        }else{
            recoverPassword();
        }
    }
    private void recoverPassword(){
        progressDialog.setMessage("Sending instructions to reset password...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(Forgot.this, "Password reset instructions sent to your email", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Forgot.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
