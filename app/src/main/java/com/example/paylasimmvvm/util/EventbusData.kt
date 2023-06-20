package com.example.paylasimmvvm.util

import com.example.paylasimmvvm.login.KullaniciBilgileri


class EventbusData {
        internal class kayitBilgileriniGonder(var telNo:String?, var email:String?, var verificationID:String?, var emailkayit:Boolean)

        internal class kullaniciBilgileriniGonder(var kullanici: KullaniciBilgileri?)




    }
