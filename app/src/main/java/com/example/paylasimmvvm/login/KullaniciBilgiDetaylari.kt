package com.example.paylasimmvvm.login

data class KullaniciBilgiDetaylari (
    var post:String?="",
    var profile_picture:String?="",
    var biography:String? ="",
    var muzik_turu:String? ="",
    var isletme_turu:String?="",
    var adress:String?="",
    var latitude: Double?=0.0 ,
    var longitude: Double?=0.0,
    var mudavim_sayisi:Int?=null

)