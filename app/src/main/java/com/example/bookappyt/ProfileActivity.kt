package com.example.bookappyt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bookappyt.MyApplication
import com.example.bookappyt.R
import com.example.bookappyt.AdapterPdfFavorite
import com.example.bookappyt.databinding.ActivityProfileBinding
import com.example.bookappyt.ModelPdf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivityProfileBinding

    // firebase auth, for loading user data using user uid
    private lateinit var firebaseAuth: FirebaseAuth

    // arraylist to hold the books
    private lateinit var pdfArrayList: ArrayList<ModelPdf>

    // adapter to set in recyclerview
    private lateinit var adapterPdfFavorite: AdapterPdfFavorite

    companion object {
        private const val TAG = "PROFILE_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()
        loadFavoriteBooks()

        // handle click, start profile edit page
        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, ProfileEditActivity::class.java))
        }

        // handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info of user ${firebaseAuth.uid}")

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // get all info of user here from snapshot
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    // format date to ddMMyyyy
                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                    // set data to ui
                    binding.emailTv.text = email
                    binding.nameTv.text = name
                    binding.memberDateTv.text = formattedDate
                    binding.accountTypeTv.text = userType

                    // set image, using glide
                    Glide.with(this@ProfileActivity)
                        .load(profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(binding.profileTv)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Failed to load user info", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadFavoriteBooks() {
        // init list
        pdfArrayList = ArrayList()

        // load favorite books from database
        // Users -> userId -> Favorites
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // clear list before starting adding data
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        // will get only the bookId here, and got other details in adapter using that bookId
                        val bookId = "${ds.child("bookId").value}"

                        // set id to model
                        val modelPdf = ModelPdf()
                        modelPdf.id = bookId

                        // add model to list
                        pdfArrayList.add(modelPdf)
                    }

                    // set number of favorite books
                    binding.favoriteBookCountTv.text = "${pdfArrayList.size}"
                    // setup adapter
                    adapterPdfFavorite = AdapterPdfFavorite(this@ProfileActivity, pdfArrayList)
                    // set adapter to recyclerview
                    binding.booksRv.adapter = adapterPdfFavorite
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Failed to load favorite books", Toast.LENGTH_SHORT).show()
                }
            })
    }
}