package com.example.bookapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.bookapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait!")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        binding.loginBtn.setOnClickListener{

        }






    }

}