package com.example.shopapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopapp.Listener.PopListener
import com.example.shopapp.R
import com.example.shopapp.model.Pop_model
import java.lang.StringBuilder

class ItemListAdapter(private val context: Context, var list: List<Pop_model>, val popClickListener: PopListener): RecyclerView.Adapter<ItemListAdapter.MyItemListViewHolder>(){
    class MyItemListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var imgPic: ImageView? = null
        var txtTitle: TextView? = null
        var txtFee: TextView? = null
        var txtScore: TextView? = null
        var imgAdd: ImageView? = null

        init{
            imgPic = itemView.findViewById(R.id.imgItem) as ImageView
            txtTitle = itemView.findViewById(R.id.txtTitleItem) as TextView
            txtFee = itemView.findViewById(R.id.txtPrice) as TextView
            txtScore = itemView.findViewById(R.id.txtScoreItem) as TextView
            imgAdd = itemView.findViewById(R.id.imgAddItem) as ImageView
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemListViewHolder {
        return MyItemListViewHolder(LayoutInflater.from(context).inflate(R.layout.viewholder_item_list,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyItemListViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].imgPic) // Đường dẫn URL của hình ảnh
            .into(holder.imgPic!!)
        holder.txtTitle!!.text = StringBuilder().append(list[position].title)
        holder.txtScore!!.text = StringBuilder().append(list[position].score)
        holder.txtFee!!.text = StringBuilder().append(list[position].fee).append("$")

        //Detail
        holder.itemView.setOnClickListener {
            popClickListener.OnClick(position)
        }
    }
}