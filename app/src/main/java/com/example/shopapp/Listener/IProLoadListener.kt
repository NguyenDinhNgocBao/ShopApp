package com.example.shopapp.Listener

import com.example.shopapp.model.Pop_model

interface IProLoadListener {
    fun onLoadSuccess(ProModelList: List<Pop_model>)
    fun onLoadFailed(message:String?)
}