package com.example.paylasimmvvm.createPost

 data class KullaniciKampanya( var userID:String?=null,
                         var userName:String?=null,
                         var userPhotoURL:String?=null,
                         var postID:String?=null,
                         var postAciklama:String?=null,
                         var geri_sayim:String?=null,
                         var postURL:String?=null,
                         var postYuklenmeTarih:Long?=null,
                         var isletmeLatitude: Double? = 0.0,
                         var isletmeLongitude: Double? = 0.0,
                         var muzik_turu:String? ="",
                         var isletme_turu:String?="",
                         var mudavim_sayisi:Int?=null)