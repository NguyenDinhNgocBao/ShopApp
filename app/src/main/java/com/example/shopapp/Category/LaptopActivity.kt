package com.example.shopapp.Category

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.Activity.DetailActivity
import com.example.shopapp.Adapter.ItemListAdapter
import com.example.shopapp.Adapter.PopAdapter
import com.example.shopapp.Listener.IProLoadListener
import com.example.shopapp.Listener.PopListener
import com.example.shopapp.MainActivity
import com.example.shopapp.R
import com.example.shopapp.databinding.ActivityLaptopBinding
import com.example.shopapp.model.Pop_model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var binding: ActivityLaptopBinding
private var popCLickListener: PopListener? = null
lateinit var popListener: IProLoadListener
class LaptopActivity : AppCompatActivity(), IProLoadListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laptop)
        binding = ActivityLaptopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initList()
        initRecyclerView()
        backMain()
    }

    private fun backMain() {
        binding.imgPrev.setOnClickListener {
            startActivity(Intent(this@LaptopActivity, MainActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        val ds : MutableList<Pop_model> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Products").child("Laptop")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(drinkSnapshot in snapshot.children){
                            val drinkModel = drinkSnapshot.getValue(Pop_model::class.java)
                            drinkModel!!.key = drinkSnapshot.key
                            ds.add(drinkModel)
                        }
                        popListener.onLoadSuccess(ds)

                    }else{
                        popListener.onLoadFailed("Drink items not exists!!!")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    popListener.onLoadFailed(error.message)
                }

            })
    }

    private fun initList() {
        val name = intent.getStringExtra("name")
        binding.txtNameType.text = name

        popListener = this
        binding.rvItem.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )
    }

    override fun onLoadSuccess(ProModelList: List<Pop_model>) {
        val Adapter = ItemListAdapter(this, ProModelList, object : PopListener{
            override fun OnClick(pos: Int) {
                val selectedProduct = ProModelList[pos]
                val I = Intent(this@LaptopActivity, DetailActivity::class.java)
                I.putExtra("title", selectedProduct.title)
                I.putExtra("fee", selectedProduct.fee)
                I.putExtra("review", selectedProduct.review)
                I.putExtra("score", selectedProduct.score)
                I.putExtra("imgPic", selectedProduct.imgPic)
                I.putExtra("description", selectedProduct.description)

                startActivity(I)
            }

        })
        binding.rvItem.adapter = Adapter
    }

    override fun onLoadFailed(message: String?) {
        Toast.makeText(this,"", Toast.LENGTH_SHORT).show()
    }
}