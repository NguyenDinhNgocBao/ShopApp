package com.example.shopapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopapp.R
import com.example.shopapp.evenbus.updateCartEvents
import com.example.shopapp.model.Cart_model
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class CartAdapter(private val context: Context, private val list: List<Cart_model>): RecyclerView.Adapter<CartAdapter.MyCartViewHolder>() {
    class MyCartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imgPic: ImageView? = null
        var txtTitle: TextView? = null
        var txtFeeItem: TextView? = null
        var txtQuantity: TextView? = null
        var btnPlus: TextView? = null
        var btnMinus: TextView? = null
        var btnDelete: ImageView? = null

        init {
            imgPic = itemView.findViewById(R.id.imgPicItem) as ImageView
            txtTitle = itemView.findViewById(R.id.txtTitleItem) as TextView
            txtFeeItem = itemView.findViewById(R.id.txtFeeItem) as TextView
            btnMinus = itemView.findViewById(R.id.txtMinus) as TextView
            btnPlus = itemView.findViewById(R.id.txtPlus) as TextView
            txtQuantity = itemView.findViewById(R.id.txtQuantity) as TextView
            btnDelete = itemView.findViewById(R.id.imgDelete) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(LayoutInflater.from(context).inflate(R.layout.viewholder_cart,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].imgPic)
            .into(holder.imgPic!!)
        holder.txtTitle!!.text = StringBuilder().append(list[position].tilte)
        holder.txtFeeItem!!.text = StringBuilder("$").append(list[position].fee)
        holder.txtQuantity!!.text = StringBuilder().append(list[position].quantity)

        //Events
        holder.btnMinus!!.setOnClickListener {_ -> minusCartItem(holder,list[position]) }
        holder.btnPlus!!.setOnClickListener {_ -> plusCartItem(holder,list[position]) }
        holder.btnDelete!!.setOnClickListener {_ ->
            val dialog = AlertDialog.Builder(context).setTitle("Delete product ?")
                .setMessage("Do you wanna delete this item ?")
                .setNegativeButton("Cancel"){dialog,_ -> dialog.dismiss()}
                .setPositiveButton("Ok"){dialog,_ ->
                    val removedItem = list[position] // Lưu trữ item sẽ bị xóa để cập nhật danh sách sau khi xóa
                    FirebaseDatabase.getInstance().getReference("Products").child("Cart")
                        .child(removedItem.key!!)
                        .removeValue()
                        .addOnSuccessListener {
                            notifyItemRemoved(position)
                            EventBus.getDefault().postSticky(updateCartEvents())
                        }
                }
                .create()
            dialog.show()
        }
    }

    private fun plusCartItem(holder: CartAdapter.MyCartViewHolder, cartModel: Cart_model) {
        cartModel.quantity += 1
        cartModel.totalPrice = cartModel!!.quantity * cartModel.fee!!.toFloat()
        holder.txtQuantity!!.text = StringBuilder().append(cartModel.quantity)
        updateFirebase(cartModel)
        notifyItemChanged(holder.adapterPosition) // Thông báo cho Adapter biết là dữ liệu đã thay đổi
    }

    private fun minusCartItem(holder: MyCartViewHolder, cartModel: Cart_model) {
        if(cartModel.quantity > 1){
            cartModel.quantity -= 1
            cartModel.totalPrice = cartModel!!.quantity * cartModel.fee!!.toFloat()
            holder.txtQuantity!!.text = StringBuilder().append(cartModel.quantity)
            updateFirebase(cartModel)
            notifyItemChanged(holder.adapterPosition)
        }else if(cartModel.quantity == 1){
            val dialog = AlertDialog.Builder(context).setTitle("Delete Item ?")
                .setMessage("?")
                .setNegativeButton("OK") {dialog, _ -> dialog.dismiss()}
                .create()
            dialog.show()
        }
    }

    private fun updateFirebase(cartModel: Cart_model) {
        FirebaseDatabase.getInstance().getReference("Products")
            .child("Cart")
            .child(cartModel.key!!)
            .setValue(cartModel)
            .addOnSuccessListener {
                EventBus.getDefault().postSticky(updateCartEvents())
            }
    }
}