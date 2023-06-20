package com.example.paylasimmvvm.login

import com.example.paylasimmvvm.login.KullaniciBilgiDetaylari

data class KullaniciBilgileri (
    var email: String? = null,
    var password: String? = null,
    var user_name: String? = null,
    var adi_soyadi: String? = null,
    var phone_number: String? = null,
    var user_id: String? = null,
    var FCM_TOKEN:String?=null,
    var user_detail: KullaniciBilgiDetaylari?=null){
}