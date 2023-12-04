package com.example.shopapp.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shopapp.Auth.LogInActivity
import com.example.shopapp.MainActivity
import com.example.shopapp.R
import com.example.shopapp.databinding.ActivityProfileBinding

private lateinit var binding:ActivityProfileBinding
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgPrev.setOnClickListener {
            //SharedPreferences để lưu tên và nickname
            val name = binding.txtNameUser.text.toString()
            val shared = getSharedPreferences("sharedPrefsMain", Context.MODE_PRIVATE)
            val editor = shared.edit()
            editor.apply{
                editor.putString("name", name)
            }.apply()
            startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
        }

        //Fragment
        binding.Account.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        //GetName & nickname
        setName()
    }

    private fun setName() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        val nickname = sharedPreferences.getString("nickname", null)

        binding.txtNameUser.setText(name)
        binding.txtNickNameUser.setText(nickname)
    }
}