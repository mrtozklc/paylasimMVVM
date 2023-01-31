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
    //www.thecocktaildb.com/api/json/v1/1/filter.php?a=Alcoholic

    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getCategories(@Query("c") category: String): Single<Kokteyl>


    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getGlasses(@Query("g") glass: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getIngredients(@Query("i") ingredient: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getCategoriesItem(@Query("c") c: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getGlassItem(@Query("g") g: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getIngredientsItem(@Query("i") i: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")
    fun getName(@Query("n") n: String): Single<Kokteyl>

    @GET("mrtozklc/Coctaildb/main/db.json")

    fun getKokteylDetayi(@Query("idDrink") kokteylId: String): Single<KokteylDetay>


}


