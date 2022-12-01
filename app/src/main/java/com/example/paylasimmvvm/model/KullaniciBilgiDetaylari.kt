package com.example.paylasimmvvm.model

data class KullaniciBilgiDetaylari (
    var post:String?=null,
    var profile_picture:String? = null,
    var biography:String? = null,
    var web_site:String? = null,
    var adress:String?=null,
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0){
}