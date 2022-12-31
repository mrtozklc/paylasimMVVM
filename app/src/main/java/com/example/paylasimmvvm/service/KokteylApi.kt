package com.example.paylasimmvvm.service

import com.example.paylasimmvvm.model.Kokteyl
import com.example.paylasimmvvm.model.KokteylDetay
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KokteylApi {

     //https://www.thecocktaildb.com/api/json/v1/1/random.php

    @GET("api/json/v1/1/random.php")


    fun getKokteyl(): Single<Kokteyl>



}
//  https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=11007

 interface  KokteylDetayi{

     @GET("api/json/v1/1/lookup.php?i=11007")

     fun getKokteylDetayi(@Query("i") kokteylId: String): Single<KokteylDetay>
}