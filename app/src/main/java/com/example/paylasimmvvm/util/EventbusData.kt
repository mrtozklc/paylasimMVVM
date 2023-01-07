package com.example.paylasimmvvm.util

import com.example.paylasimmvvm.model.KullaniciBilgileri


class EventbusData {
        internal class kayitBilgileriniGonder(var telNo:String?, var email:String?, var verificationID:String?, var emailkayit:Boolean)

        internal class kullaniciBilgileriniGonder(var kullanici: KullaniciBilgileri?)

        internal class YorumYapilacakGonderininIDsiniGonder(var gonderiID:String?)
    }
