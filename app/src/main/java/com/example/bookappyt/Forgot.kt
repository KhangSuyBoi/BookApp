package com.example.bookappyt

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.bookappyt.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class Forgot : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener { onBackPressed() }
        binding.submitBtn.setOnClickListener { validateData() }
    }

    private var email = ""
    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Invalid Email Format"
        } else {
            recoverPassword()
        }
    }

    private fun recoverPassword() {
        progressDialog.setMessage("Sending instructions to reset password...")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
            progressDialog.dismiss()
            toast("Password reset instructions sent to your email")
        }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
            toast(e.message.orEmpty())
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}