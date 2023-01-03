package com.example.paylasimmvvm.service

import com.example.paylasimmvvm.model.Kokteyl
import com.example.paylasimmvvm.model.KokteylDetay
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KokteylApi {

     //https://www.thecocktaildb.com/api/json/v1/1/random.php
    //www.thecocktaildb.com/api/json/v1/1/search.php?f=a
   // www.thecocktaildb.com/api/json/v1/1/list.php?c=list
    //www.thecocktaildb.com/api/json/v1/1/filter.php?c=Cocktail
    //https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list
    //www.thecocktaildb.com/api/json/v1/1/filter.php?c=Ordinary_Drink
    //www.thecocktaildb.com/api/json/v1/1/filter.php?g=Cocktail_glass

    @GET("api/json/v1/1/list.php")
    fun getCategories(@Query("c") category: String): Single<Kokteyl>


    @GET("api/json/v1/1/list.php")
    fun getGlasses(@Query("g") glass: String): Single<Kokteyl>

    @GET("api/json/v1/1/list.php")
    fun getIngredients(@Query("i") ingredient: String): Single<Kokteyl>

    @GET("api/json/v1/1/filter.php?")
    fun getCategoriesItem(@Query("c") c: String): Single<Kokteyl>

    @GET("api/json/v1/1/filter.php?")
    fun getGlassItem(@Query("g") g: String): Single<Kokteyl>

    @GET("api/json/v1/1/filter.php?")
    fun getIngredientsItem(@Query("i") i: String): Single<Kokteyl>


}


//  https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=11007

 interface  KokteylDetayi{

     @GET("api/json/v1/1/lookup.php?i=11007")

     fun getKokteylDetayi(@Query("i") kokteylId: String): Single<KokteylDetay>
}