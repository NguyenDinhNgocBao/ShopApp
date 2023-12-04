package com.example.shopapp.Auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.shopapp.Activity.ProfileActivity
import com.example.shopapp.R
import com.example.shopapp.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var binding:ActivityLogInBinding
private lateinit var auth: FirebaseAuth

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        innit()
        registerEvents()
    }

    private fun registerEvents() {
        binding.txtAuth.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnNext.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPassword.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Lấy UID của người dùng đăng nhập thành công
                        val userId = it.result?.user?.uid
                        // Truy vấn Realtime Database để lấy thông tin name và nickname của người dùng
                        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val name = snapshot.child("name").value.toString()
                                    val nickname = snapshot.child("nickname").value.toString()

                                    // Chuyển hướng sang ProfileActivity và truyền thông tin name và nickname
                                    val intent = Intent(this@LogInActivity, ProfileActivity::class.java)
                                    //SharedPreferences để lưu tên và nickname
                                    val shared = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                                    val editor = shared.edit()
                                    editor.putString("name",name)
                                    editor.putString("nickname",nickname)
                                    editor.apply()

                                    startActivity(intent)
                                    finish()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@LogInActivity, "Error", Toast.LENGTH_LONG).show()
                            }
                        })
                    }else {
                        Toast.makeText(this@LogInActivity, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }else{
                Toast.makeText(this,"Mật khẩu nhập lại không đúng!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun innit() {
        auth = FirebaseAuth.getInstance()
    }
}