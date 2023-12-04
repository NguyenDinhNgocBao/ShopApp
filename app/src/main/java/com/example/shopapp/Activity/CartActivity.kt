package com.example.shopapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.example.shopapp.Adapter.CartAdapter
import com.example.shopapp.Listener.ICartLoadListener
import com.example.shopapp.MainActivity
import com.example.shopapp.R
import com.example.shopapp.databinding.ActivityCartBinding
import com.example.shopapp.evenbus.updateCartEvents
import com.example.shopapp.model.Cart_model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

private lateinit var binding: ActivityCartBinding
class CartActivity : AppCompatActivity(), ICartLoadListener {

    var cartLoadListener:ICartLoadListener?= null
    var sum = 0.0
    var current = 0.0
    var delivery = 0.0
    var tax = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgPrev.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


        init(this)
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        val cartModels : MutableList<Cart_model> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Products").child("Cart")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children) {
                        val cartModel = cartSnapshot.getValue(Cart_model::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSucess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }
            })
    }

    private fun init(cartActivity: CartActivity) {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        binding.rvCart.layoutManager = layoutManager
        binding.rvCart.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
    }

    override fun onLoadCartSucess(CartModeList: List<Cart_model>) {
        sum = 0.0
        tax = 0.0
        current = 0.0
        for(s in CartModeList!!){
            current += (s!!.totalPrice)
            tax += (s!!.tax!!)
            sum = current + tax
        }
        //sum = current + delivery + tax
        updatePrice(sum,current,tax)

        val adapter = CartAdapter(this, CartModeList!!)
        binding.rvCart!!.adapter = adapter
    }

    private fun updatePrice(sum: Double, current: Double, tax: Double) {
        this.sum = sum.toDouble()
        this.current = current.toDouble()
        this.tax = tax.toDouble()
        binding.txtTotalPrice.text = StringBuilder("$").append(String.format("%.3f",(sum)))
        binding.txtSubtotal.text = StringBuilder("$").append(String.format("%.3f",(current)))
        binding.txtDelivery.text = StringBuilder("Free Ship")
        binding.txtTotalTax.text = StringBuilder("$").append(String.format("%.3f",(tax)))
    }

    override fun onLoadCartFailed(message: String?) {
        Toast.makeText(this,"Empty", Toast.LENGTH_SHORT).show()
    }
}