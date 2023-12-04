package com.example.shopapp.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.shopapp.R
import com.example.shopapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

private lateinit var binding: ActivitySignUpBinding
private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase
class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        innit()
        registerEvents()
    }

    private fun registerEvents() {
        binding.txtAuth.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        binding.btnNext.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPassword.text.toString().trim()
            val rePass = binding.edtRePassword.text.toString().trim()
            val name = binding.edtName.text.toString()
            val nickname = binding.edtNickName.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && rePass.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                if(pass.equals(rePass)){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            // Lấy UID của người dùng mới đăng ký
                            val userId = it.result?.user?.uid
                            // Lưu thông tin name và nickname vào Realtime Database
                            val userRef = database.reference.child("users").child(userId!!)
                            val userData = HashMap<String, Any>()
                            userData["name"] = name
                            userData["nickname"] = nickname
                            userRef.setValue(userData)


                            Toast.makeText(this, "Register Successfully", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, LogInActivity::class.java))

                        }else{
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }else{
                    Toast.makeText(this, "Mật khẩu nhập lại không đúng!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun innit() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }
}