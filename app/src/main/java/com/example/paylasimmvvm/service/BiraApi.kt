package com.example.paylasimmvvm.service

import com.example.paylasimmvvm.model.BiralarModel
import io.reactivex.Single
import retrofit2.http.GET

interface BiraApi {

  //  https://api.punkapi.com/v2/beers

    @GET("v2/beers?page=1&per_page=80")
    fun getBira(): Single<List<BiralarModel>>
}