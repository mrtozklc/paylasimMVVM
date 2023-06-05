package com.example.paylasimmvvm.model

class BildirimModel(  var bildirim_id:String?=null,
                      var bildirim_tur:Int?=null,
                      var time:Long?=null,
                      var user_id:String?=null,
                      var yorum:String?=null,
                      var yorum_key:String?=null,
                      var gonderi_id:String?=null,
                      var goruldu:Boolean?=null,
                      var goruldu_sayisi:Int?=0)