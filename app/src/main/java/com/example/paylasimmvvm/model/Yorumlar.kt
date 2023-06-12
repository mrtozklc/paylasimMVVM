package com.example.paylasimmvvm.model

data class Yorumlar(    var user_id:String?=null,
                        var user_name:String?=null,
                        var profilPhoto:String?=null,
                        var yorum:String?=null,
                        var yorum_key:String?=null,
                        var yorum_begeni:String?=null,
                        var yorum_tarih:Long?=null,
                        var yorum_sayisi:Int?=null,
                        val yanitlar: HashMap<String, Reply> = HashMap<String, Reply>()
                        )

data class Reply(
    var user_id: String? = null,
    var user_name: String? = null,
    var profilPhoto: String? = null,
    var yorum: String? = null,
    var yorum_key: String? = null,
    var yorum_begeni: String? = null,
    var yorum_tarih: Long? = null,
    var yorum_sayisi: Int? = null
)
