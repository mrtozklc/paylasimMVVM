package com.example.paylasimmvvm.service

import com.example.paylasimmvvm.model.Kokteyl
import com.example.paylasimmvvm.model.KokteylDetay
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class KokteylApiServis {
    //https://raw.githubusercontent.com/atilsamancioglu/BTK20-JSONVeriSeti/master/besinler.json
    //https://www.thecocktaildb.com/api/json/v1/1/search.php?f=a

    private val BASE_URL = "https://www.thecocktaildb.com/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(KokteylApi::class.java)

    private val apiDetay = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(KokteylDetayi ::class.java)


    fun getData() : Single<Kokteyl> {
        return api.getKokteyl()

    }

    fun getKokteylDetayi(kokteylId: String):Single<KokteylDetay>{
        return apiDetay.getKokteylDetayi(kokteylId)
    }
}