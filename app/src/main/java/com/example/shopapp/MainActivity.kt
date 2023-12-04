package com.example.shopapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.Activity.CartActivity
import com.example.shopapp.Activity.DetailActivity
import com.example.shopapp.Activity.ProfileActivity
import com.example.shopapp.Adapter.PopAdapter
import com.example.shopapp.Category.LaptopActivity
import com.example.shopapp.Listener.ICartLoadListener
import com.example.shopapp.Listener.IProLoadListener
import com.example.shopapp.Listener.PopListener
import com.example.shopapp.databinding.ActivityMainBinding
import com.example.shopapp.evenbus.updateCartEvents
import com.example.shopapp.model.Cart_model
import com.example.shopapp.model.Pop_model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), ICartLoadListener, IProLoadListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var cartListener: ICartLoadListener
    lateinit var popListener: IProLoadListener
    private var popCLickListener:PopListener? = null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(updateCartEvents::class.java))
            EventBus.getDefault().removeStickyEvent(updateCartEvents::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public fun onUpdateCartEvent(event: updateCartEvents) {
        countCartFromFireBase()
    }

    private fun countCartFromFireBase() {
        val cartModels : MutableList<Cart_model> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Products").child("Cart")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(cartSnapshot in snapshot.children){
                            val cartModel = cartSnapshot.getValue(Cart_model::class.java)
                            cartModel!!.key = cartSnapshot.key
                            cartModels.add(cartModel)
                        }
                        cartListener.onLoadCartSucess(cartModels)
                    }else{
                        cartListener.onLoadCartFailed("Drink items not exists!!!")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCart(this)
        initRecyclerView()
        initProfile()
        setName()
        initCategory()
    }

    private fun initCategory() {
        binding.laptop.setOnClickListener {
            navigateToProductPage("Laptop")
        }
        binding.phone.setOnClickListener {
            navigateToProductPage("Mobile")
        }
        binding.headPhone.setOnClickListener {
            navigateToProductPage("HeadPhone")
        }
        binding.gaming.setOnClickListener {
            navigateToProductPage("Gaming")
        }
    }

    private fun navigateToProductPage(s: String) {
        val i = Intent(this@MainActivity, LaptopActivity::class.java)
        i.putExtra("name",s)
        startActivity(i)
    }

    private fun setName() {
        val sharedPreferences = getSharedPreferences("sharedPrefsMain", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        binding.txtNameAuth.setText(name)
    }

    private fun initProfile() {
        binding.profile.setOnClickListener {
           startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        val ds : MutableList<Pop_model> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Products").child("Laptop")
            .addListenerForSingleValueEvent(object : ValueEventListener{
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

    private fun initCart(view: MainActivity) {
        popListener = this
        cartListener = this
        binding.cart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java))
        }
        binding.rvView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
    }

    override fun onLoadCartSucess(CartModeList: List<Cart_model>) {
        var cartSum = 0
        for(cartModel in CartModeList!!){
            cartSum += cartModel!!.quantity
        }
        binding.badge.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Toast.makeText(this,"Empty", Toast.LENGTH_SHORT).show()
    }

    override fun onLoadSuccess(ProModelList: List<Pop_model>) {
        val Adapter = PopAdapter(this, ProModelList, cartListener, object : PopListener{
            override fun OnClick(pos: Int) {
                val selectedProduct = ProModelList[pos]
                val I = Intent(this@MainActivity, DetailActivity::class.java)
                I.putExtra("title", selectedProduct.title)
                I.putExtra("fee", selectedProduct.fee)
                I.putExtra("review", selectedProduct.review)
                I.putExtra("score", selectedProduct.score)
                I.putExtra("imgPic", selectedProduct.imgPic)
                I.putExtra("description", selectedProduct.description)

                startActivity(I)
            }

        })
        binding.rvView.adapter = Adapter
    }

    override fun onLoadFailed(message: String?) {
        Toast.makeText(this,"", Toast.LENGTH_SHORT).show()
    }
}