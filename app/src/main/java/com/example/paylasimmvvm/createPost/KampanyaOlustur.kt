package com.example.paylasimmvvm.createPost

data class KampanyaOlustur(    var user_id:String?="",
                               var post_id:String?="" ,
                               var yuklenme_tarih:Long?=null,
                               var aciklama:String?="",
                               var geri_sayim:String?="",
                               var file_url:String?="",
                               var user:Boolean?=null
)