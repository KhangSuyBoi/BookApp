package com.example.bookappyt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {
    /*
    * 1) Kiểm tra xem người dùng đó có log in chưa
    * 2) Xem là "user" hay "admin"
    * */

    private lateinit var firebaseAuth: FirebaseAuth

    //hàm khởi tạo Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)//thiết lập giao diện bằng layout activity_splash

        firebaseAuth = FirebaseAuth.getInstance() //lấy 1 instance

        Handler().postDelayed(Runnable { // gửi và xử lí thông điệp
            checkUser() // chạy hàm này sau 2s
        }, 2000) // 2s
    }

    private fun checkUser() { // xem người dùng hiện tại là ai và chuyển đến màn hình phù hợp
        //kiểm tra trạng thái đăng nhập và xem là admin hay user
        val firebaseUser = firebaseAuth.currentUser //lấy thông tin về người dùng hiện tại

        //kiểm tra có đang đăng nhập không
        if (firebaseUser == null)
        {
            // nếu không có ai -> MainActivty
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            // nếu có đăng nhập thì phải kiểm tra loại người dùng (admin/user)
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            // cho phép truy cập và tham chiếu đến 1 nút cụ thể - nút Users trong cơ sở dữ liệu
            ref.child(firebaseUser.uid) // lấy thông tin về người dùng hiện tại userid

                //lắng nghe sự kiện khi thông tin người dùng tải về thành công
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) { //kiểm tra giá trị của user
                        // xem thử là "user" hay "admin" trong userType
                        val userType = snapshot.child("userType").value
                        if (userType == "user") {
                            //nếu là user -> chuyển đến trang dashboard của user
                            startActivity(Intent(this@SplashActivity, DashboardUserActivity::class.java))
                            finish()
                        } else if (userType == "admin") { //tương tự như admin
                            startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                            finish()

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }
}
