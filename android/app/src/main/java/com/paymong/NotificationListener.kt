package com.paymong

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import java.time.LocalDateTime

class NotificationListener : NotificationListenerService() {
    private var start = LocalDateTime.now()
    init {
        start = LocalDateTime.now()
    }
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Things
//        val packageName: String = sbn.packageName ?: ""
//        val extras = sbn.notification.extras
//
//        val extraTitle: String = extras?.get(Notification.EXTRA_TITLE).toString()
//        val extraText: String = extras?.get(Notification.EXTRA_TEXT).toString()
//        val extraBigText: String = extras?.get(Notification.EXTRA_BIG_TEXT).toString()
//        val extraInfoText: String = extras?.get(Notification.EXTRA_INFO_TEXT).toString()
//        val extraSubText: String = extras?.get(Notification.EXTRA_SUB_TEXT).toString()
//        val extraSummaryText: String = extras?.get(Notification.EXTRA_SUMMARY_TEXT).toString()
//
//        Log.d("noti", "$packageName : $extraTitle : $extraText :  $extraBigText : $extraInfoText : $extraSubText : $extraSummaryText")
//        $extraTitle : $extraText

        // samsungPay
//        val extras = sbn.notification.extras
//        val packageName: String = sbn.packageName ?: ""
//        val message: List<String> = extras.get(Notification.EXTRA_TITLE).toString().split("\\")


//        val content : String = message[0]
//        val price : Int = message[1].toInt()
//
//        Log.e("samsung pay", "$packageName : $content : $price : $start")
//        Toast.makeText(applicationContext, "$content : $price : $start", Toast.LENGTH_LONG).show()
    }
}

