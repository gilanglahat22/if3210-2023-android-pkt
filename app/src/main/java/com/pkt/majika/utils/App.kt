package com.pkt.majika.utils

import android.app.Application

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Config {
    private const val baseUrl = "http://139.144.117.143:9786"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

class App : Application() {
    private val database by lazy {CartItemRoomDatabase.getDatabase(this)}
    val repository by lazy { CartItemResources(database.CartItemModels())}
}