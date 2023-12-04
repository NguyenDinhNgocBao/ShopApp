package com.example.shopapp.Listener

import com.example.shopapp.model.Cart_model

interface ICartLoadListener {
    fun onLoadCartSucess(CartModeList: List<Cart_model>)
    fun onLoadCartFailed(message:String?)
}