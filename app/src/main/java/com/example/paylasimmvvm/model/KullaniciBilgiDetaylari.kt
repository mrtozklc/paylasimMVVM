package com.example.paylasimmvvm.model

data class KullaniciBilgiDetaylari (
    var post:String?="",
    var profile_picture:String?="" ,
    var biography:String? ="",
    var web_site:String? ="",
    var adress:String?="",
    var latitude: Double?=0.0 ,
    var longitude: Double?=0.0){
}