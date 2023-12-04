package com.example.shopapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.shopapp.MainActivity
import com.example.shopapp.R
import com.example.shopapp.databinding.ActivityDetailBinding

private lateinit var binding: ActivityDetailBinding
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPopData()
        binding.imgPrev.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun getPopData() {
        val title = intent.getStringExtra("title")
        val fee = intent.getStringExtra("fee")
        val review = intent.getStringExtra("review")
        val score = intent.getStringExtra("score")
        val imgPic = intent.getStringExtra("imgPic")
        val description = intent.getStringExtra("description")

        // Hiển thị thông tin trên giao diện người dùng
        // (Bạn cần có các TextView, ImageView tương ứng trong layout của DetailActivity)

        binding.txtNameProduct.text = title
        binding.txtPriceProduct.text = "$fee$"
        binding.txtReviewDetail.text = review
        binding.txtScoreDetail.text = score
        binding.txtDescription.text = description
        val imgProduct = binding.imgProduct
        Glide.with(this)
            .load(imgPic) // Đường dẫn URL của hình ảnh
            .into(imgProduct)
    }
}