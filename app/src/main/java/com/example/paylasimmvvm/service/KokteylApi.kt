package com.example.paylasimmvvm.service

import com.example.paylasimmvvm.model.Kokteyl
import com.example.paylasimmvvm.model.KokteylDetay
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KokteylApi {



    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getCategories(@Query("c") category: String): Single<Kokteyl>


    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getCategoriesItem(@Query("c") c: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getName(@Query("n") n: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")

    fun getKokteylDetayi(@Query("idDrink") kokteylId: String): Single<KokteylDetay>


}


