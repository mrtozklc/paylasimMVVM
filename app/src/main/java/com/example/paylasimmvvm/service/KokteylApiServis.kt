package com.example.paylasimmvvm.service

import com.example.paylasimmvvm.model.Kokteyl
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class KokteylApiServis {

    private val BASE_URL = "https://raw.githubusercontent.com/"



    private val api = Retrofit.Builder()

        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(KokteylApi::class.java)

    fun getCategoryList(category: String)=api.getCategories(category)

    fun getGlassList(glass: String) = api.getGlasses(glass)

    fun getIngredientList(ingredient: String) = api.getIngredients(ingredient)

    fun getCategoriesItem(c:String)=api.getCategoriesItem(c)

    fun getGlassItem(g:String)=api.getGlassItem(g)

    fun getIngredientsItem(i:String)=api.getIngredientsItem(i)

    fun getKokteylDetayi(kokteylId: String) = api.getKokteylDetayi(kokteylId)









   // fun getDataList(List:String)=api.getDataList(List)



}