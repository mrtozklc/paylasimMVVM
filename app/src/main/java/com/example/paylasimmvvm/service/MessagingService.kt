package com.example.paylasimmvvm.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.media.RingtoneManager
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.home.MainActivity
import com.example.paylasimmvvm.massage.ChatFragment
import com.example.paylasimmvvm.massage.MesajlarFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService:FirebaseMessagingService() {



    override fun onMessageReceived(message: RemoteMessage) {

     Log.e("bildirimdata",""+message.data)

        if (message.data["bildirimTuru"]!!.toString() == "yeni_mesaj") {

            val mesajGonderenUserName = message.data["kimYolladi"]
            val sonMesaj = message.data["neYolladi"]
            val mesajGonderenUserID = message.data["secilenUserID"]


            if (!ChatFragment.fragmentAcikMi && !MesajlarFragment.fragmentAcikMi) {
                yeniMesajBildirimi(mesajGonderenUserName, sonMesaj, mesajGonderenUserID)

            }

        }
        else if(message.data["bildirimTuru"]!!.toString() == "1"){

            val begenenUserName= message.data["kimYolladi"]
            val begenenUserID= message.data["secilenUserID"]
            val begenilenUserID= message.data["begenilenUserID"]
            val gonderiID=message.data["gonderiID"]
            val bildirimID=message.data["bildirimID"]
            yeniBegeniBildirimi(begenenUserName, begenenUserID, gonderiID,begenilenUserID,bildirimID)


        }
        else if(message.data["bildirimTuru"]!!.toString() == "2"){

            val kimYolladi= message.data["kimYolladi"]
            val yorumYapanUserID= message.data["secilenUserID"]
            val begenilenUserID= message.data["begenilenUserID"]
            val gonderiID=message.data["gonderiID"]
            val yorum=message.data["yorum"]
            val yorumKey=message.data["yorumKey"]
            val bildirimID=message.data["bildirimID"]

            yeniYorumBildirimi(kimYolladi, yorumYapanUserID, gonderiID,begenilenUserID,yorum,yorumKey,bildirimID)

        }
        else if(message.data["bildirimTuru"]!!.toString() == "3"){

            val kimYolladi= message.data["kimYolladi"]
            val begenenUserID= message.data["secilenUserID"]
            val begenilenUserID= message.data["begenilenUserID"]
            val yorum=message.data["yorum"]
            val yorumKey=message.data["yorumKey"]
            val gonderiID=message.data["gonderiID"]
            val bildirimID=message.data["bildirimID"]
            val gonderiSahibiID=message.data["gonderiSahibiID"]


            yeniYorumBegeniBildirimi(kimYolladi, begenenUserID,yorum,yorumKey,begenilenUserID,gonderiID,bildirimID,gonderiSahibiID)


        }
        else if(message.data["bildirimTuru"]!!.toString() == "4"){

            val kimYolladi= message.data["kimYolladi"]
            val yorumYapanUserID= message.data["secilenUserID"]
            val yorumYapilanUserID= message.data["yorumYapilanUserID"]
            val yorum=message.data["yorum"]
            val yorumKey=message.data["yorumKey"]
            val bildirimID=message.data["bildirimID"]
            val gonderiSahibiID=message.data["gonderiSahibiID"]

           yeniYorumIsletmeBildirimi(kimYolladi, yorumYapanUserID, yorum,yorumKey,yorumYapilanUserID,bildirimID,gonderiSahibiID)


        }


    }

    private fun yeniYorumIsletmeBildirimi( kimYolladi: String?,yorumYapanUserID: String?,yorum: String?, yorumKey: String?,
        yorumYapilanUserID: String?,bildirimID:String?,gonderiSahibiID: String?) {
        val pendingIntent = Intent(this, MainActivity::class.java)

        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("isletmeYorumKey", yorumKey)
        pendingIntent.putExtra("isletmeYorumYapilanUserID", yorumYapilanUserID)
        pendingIntent.putExtra("isletmeYapilanYorum", yorum)
        pendingIntent.putExtra("bildirimID", bildirimID)
        pendingIntent.putExtra("gonderiSahibiID", gonderiSahibiID)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val bildirimPendingIntent = PendingIntent.getActivity(
            this,
            70,
            pendingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


                val contentView = RemoteViews(packageName, R.layout.notification_pp)
                val message = " işletmeniz hakkında bir yorum yaptı"
                val spannableString = SpannableString("$kimYolladi$message")
                spannableString.setSpan(StyleSpan(Typeface.BOLD), 0,
                    kimYolladi!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                contentView.setTextViewText(R.id.contentTitle, spannableString)
                contentView.setTextViewText(R.id.contentText, yorum)

                val builder = NotificationCompat.Builder(this, "isletme yorum")
                    .setSmallIcon(R.drawable.ic_baseline_circle_notifications)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setCustomContentView(contentView)
                    .setCustomBigContentView(contentView)
                    .setAutoCancel(true)
                    .setContentIntent(bildirimPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                notificationManager.notify(bildirimIDOlustur(yorumYapanUserID!!), builder)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("isletme yorum", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }


    }
    @SuppressLint("SuspiciousIndentation")
    private fun yeniYorumBegeniBildirimi(kimYolladi: String?, begenenUserID: String?, yorum: String?, yorumKey: String?, begenilenUserID:String?, gonderiID: String?,bildirimID:String?,gonderiSahibiID:String?) {
        val pendingIntent = Intent(this, MainActivity::class.java)

        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("begenilenYorumKey", yorumKey)
        pendingIntent.putExtra("begenilenYorumGonderiID", gonderiID)
        pendingIntent.putExtra("begenilenUserID", begenilenUserID)
        pendingIntent.putExtra("gonderiSahibiID", gonderiSahibiID)
        pendingIntent.putExtra("begenilenYorum", yorum)
        pendingIntent.putExtra("bildirimID", bildirimID)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val bildirimPendingIntent = PendingIntent.getActivity(
            this,
            30,
            pendingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentView = RemoteViews(packageName, R.layout.notification_pp)

        val message = " yorumunu beğendi"
        val spannableString = SpannableString("$kimYolladi$message")
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0,
            kimYolladi!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        contentView.setTextViewText(R.id.contentTitle, spannableString)
        contentView.setTextViewText(R.id.contentText, yorum)

        val builder = NotificationCompat.Builder(this, "Yorum Begenildi")
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCustomContentView(contentView)
            .setCustomBigContentView(contentView)
            .setAutoCancel(true)
            .setContentIntent(bildirimPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(bildirimIDOlustur(begenenUserID!!), builder)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Yorum Begenildi", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

    }
    private fun yeniYorumBildirimi(yorumYapanUserName: String?,yorumYapanUserID: String?, gonderiID: String?,begenilenUserID: String?,yorum: String?,yorumKey: String?,bildirimID:String?) {
        val pendingIntent = Intent(this, MainActivity::class.java)
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("gonderiYorumKey", yorumKey)
        pendingIntent.putExtra("yorumYapilanGonderi", gonderiID)
        pendingIntent.putExtra("begenilenUserID", begenilenUserID)
        pendingIntent.putExtra("bildirimID", bildirimID)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val bildirimPendingIntent = PendingIntent.getActivity(
            this,
            20,
            pendingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val contentView = RemoteViews(packageName, R.layout.notification_pp)
        val message = " gönderine yorum yaptı"
        val spannableString = SpannableString("$yorumYapanUserName$message")
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0,
            yorumYapanUserName!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        contentView.setTextViewText(R.id.contentTitle, spannableString)
        contentView.setTextViewText(R.id.contentText, yorum)
            val builder = NotificationCompat.Builder(this, "Yorum Yapildi")
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCustomContentView(contentView)
            .setCustomBigContentView(contentView)
            .setAutoCancel(true)
            .setContentIntent(bildirimPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

           notificationManager.notify(bildirimIDOlustur(yorumYapanUserID!!), builder)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Yorum Yapildi", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

    }
    private fun yeniBegeniBildirimi(begenenUserName: String?, begenenUserID: String?, gonderiID: String?, begenilenUserID: String?,bildirimID:String?) {
        val pendingIntent = Intent(this, MainActivity::class.java)
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("begenilenGonderi", gonderiID)
        pendingIntent.putExtra("begenilenUserID", begenilenUserID)
        pendingIntent.putExtra("bildirimID", bildirimID)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val bildirimPendingIntent = PendingIntent.getActivity(
            this,
            10,
            pendingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val contentView = RemoteViews(packageName, R.layout.notification_pp)
        val message = " gönderini beğendi"

        val spannableString = SpannableString("$begenenUserName$message")
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0,
            begenenUserName!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        contentView.setTextViewText(R.id.contentTitle, spannableString)

        val builder = NotificationCompat.Builder(this, "Gonderi begenildi")
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCustomContentView(contentView)
            .setCustomBigContentView(contentView)
            .setAutoCancel(true)
            .setContentIntent(bildirimPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(bildirimIDOlustur(begenenUserID!!), builder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Gonderi begenildi", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun yeniMesajBildirimi(bildirimTitle: String?, bildirimBody: String?, gidilecekUserID: String?) {
        val pendingIntent = Intent(this, MainActivity::class.java)
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("konusulacakKisi", gidilecekUserID)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val bildirimPendingIntent = PendingIntent.getActivity(
            this,
            15,
            pendingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentView = RemoteViews(packageName, R.layout.notification_pp)
        contentView.setTextViewText(R.id.contentTitle, bildirimTitle)
        contentView.setTextViewText(R.id.contentText, bildirimBody)

        val builder = NotificationCompat.Builder(this, "Yeni Mesaj")
            .setSmallIcon(R.drawable.ic_baseline_mesaj_24)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setCustomContentView(contentView)
            .setCustomBigContentView(contentView)
            .setAutoCancel(true)
            .setContentIntent(bildirimPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(bildirimIDOlustur(gidilecekUserID!!), builder)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("Yeni Mesaj", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }



    }
    private fun bildirimIDOlustur(gidilecekUserID: String): Int{

        return gidilecekUserID.hashCode()
    }
    override fun onNewToken(token: String) {

        newTokenAl(token)
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