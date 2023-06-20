package com.example.paylasimmvvm.notifications

class BildirimModel(  var bildirim_id:String?=null,
                      var bildirim_tur:Int?=null,
                      var time:Long?=null,
                      var bildirim_yapan_id:String?=null,
                      var gonderi_sahibi_id:String?=null,
                      var yorum:String?=null,
                      var yorum_key:String?=null,
                      var gonderi_id:String?=null,
                      var goruldu:Boolean?=null,
                      var goruldu_sayisi:Int?=0)