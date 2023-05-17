package com.example.paylasimmvvm.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.view.home.HomeFragment
import com.example.paylasimmvvm.view.home.MainActivity
import com.example.paylasimmvvm.view.mesajlar.ChatFragment
import com.example.paylasimmvvm.view.mesajlar.MesajlarFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService:FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        var  bildirimTitle=message.notification!!.title
        var bildirimBody=message.notification!!.body
        var bildirimData=message.data.get("konusulacakKisi")

        Log.e("bildirim", "bildirim$bildirimTitle$bildirimBody$bildirimData")

        if(!ChatFragment.fragmentAcikMi && !MesajlarFragment.fragmentAcikMi)
        {
            yeniMesajBildirimi(bildirimTitle,bildirimBody,bildirimData)
        }





    }


    override fun onNewToken(token: String) {
        var newToken=token!!

       newTokenAl(newToken)
    }

    private fun yeniMesajBildirimi(bildirimTitle: String?, bildirimBody: String?, gidilecekUserID: String?) {

        var pendingIntent= Intent(this,MainActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("konusulacakKisi",gidilecekUserID)

        var bildirimPendingIntent= PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_IMMUTABLE)

        var builder= NotificationCompat.Builder(this,"Yeni Mesaj")
            .setSmallIcon(R.drawable.ic_baseline_mesaj_24)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_baseline_mesaj_24))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(bildirimTitle)
            .setContentText(bildirimBody)
            .setAutoCancel(true)
            .setContentIntent(bildirimPendingIntent)
            .build()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        @RequiresApi(Build.VERSION_CODES.O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Yeni Mesaj", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(bildirimIDOlustur(gidilecekUserID!!),builder)




    }


    private fun bildirimIDOlustur(gidilecekUserID: String): Int{
        var id= 0

        for(i in 0..5){
            id= id + gidilecekUserID[i].toInt()
        }

        return id
    }


    private fun newTokenAl(newToken: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val database = FirebaseDatabase.getInstance().reference
            val usersRef = database.child("users")
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        for (userSnapshot in childSnapshot.children) {
                            if (userSnapshot.child("user_id").value == currentUser.uid) {
                                val userRef = userSnapshot.ref
                                userRef.child("FCM_TOKEN").setValue(newToken)
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
        }
    }

}