package com.example.paylasimmvvm.service

import com.example.paylasimmvvm.model.BiralarModel
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class BiraApiServis {

    //  https://api.punkapi.com/v2/beers

    private val BASE_URL = " https://api.punkapi.com/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(BiraApi::class.java)

    fun getData(): Single<List<BiralarModel>> {
        return api.getBira()
    }

}
