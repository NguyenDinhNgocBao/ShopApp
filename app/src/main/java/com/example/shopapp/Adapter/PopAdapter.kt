package com.example.shopapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopapp.Listener.ICartLoadListener
import com.example.shopapp.Listener.PopListener
import com.example.shopapp.R
import com.example.shopapp.databinding.ViewholderPopListBinding
import com.example.shopapp.evenbus.updateCartEvents
import com.example.shopapp.model.Cart_model
import com.example.shopapp.model.Pop_model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

private lateinit var binding: ViewholderPopListBinding
class PopAdapter(private val context: Context, var list: List<Pop_model>, val cartListener: ICartLoadListener, val popCLickListener: PopListener):RecyclerView.Adapter<PopAdapter.MyPopViewHolder>() {
    class MyPopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var imgPic: ImageView? = null
        var txtTitle: TextView? = null
        var txtFee: TextView? = null
        var txtScore: TextView? = null
        var txtReview: TextView? = null
        var imgAdd: ImageView? = null

        init {
            imgPic = itemView.findViewById(R.id.imgPic) as ImageView
            txtTitle = itemView.findViewById(R.id.txtTitle) as TextView
            txtFee = itemView.findViewById(R.id.txtFee) as TextView
            txtScore = itemView.findViewById(R.id.txtScore) as TextView
            txtReview = itemView.findViewById(R.id.txtReview) as TextView
            imgAdd = itemView.findViewById(R.id.imgAdd) as ImageView
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPopViewHolder {
        return MyPopViewHolder(LayoutInflater.from(context).inflate(R.layout.viewholder_pop_list,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyPopViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].imgPic) // Đường dẫn URL của hình ảnh
            .into(holder.imgPic!!)
        holder.txtTitle!!.text = StringBuilder().append(list[position].title)
        holder.txtScore!!.text = StringBuilder().append(list[position].score)
        holder.txtReview!!.text = StringBuilder().append(list[position].review)
        holder.txtFee!!.text = StringBuilder().append(list[position].fee).append("$")

        holder.imgAdd!!.setOnClickListener {
            AddToCart(list[position])
        }

        //Detail
        holder.itemView.setOnClickListener {
            popCLickListener.OnClick(position)
        }
    }

    private fun AddToCart(popModel: Pop_model) {
        val proCart= FirebaseDatabase.getInstance().getReference("Products").child("Cart")
        proCart.child(popModel.key!!).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    //If item has in Cart, just Update
                    val cartModel =snapshot.getValue(Cart_model::class.java)
                    val updateCart:MutableMap<String, Any> = HashMap()
                    cartModel!!.quantity = cartModel!!.quantity+1
                    updateCart["quantity"] =cartModel!!.quantity
                    updateCart["totalPrice"] = cartModel!!.quantity * cartModel.fee!!.toFloat()

                    proCart.child(popModel.key!!).updateChildren(updateCart)
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(updateCartEvents())
                        }
                }else{
                    //if item not current in Cart, add to cart
                    val cartModel = Cart_model()
                    cartModel.key = popModel.key
                    cartModel.tilte = popModel.title
                    cartModel.imgPic = popModel.imgPic
                    cartModel.fee = popModel.fee
                    cartModel.quantity = 1
                    cartModel.totalPrice = popModel.fee!!.toFloat()
                    cartModel.tax = popModel.tax!!.toFloat()

                    proCart.child(popModel.key!!).setValue(cartModel)
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(updateCartEvents())
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                cartListener.onLoadCartFailed(error.message)
            }

        })
    }


}