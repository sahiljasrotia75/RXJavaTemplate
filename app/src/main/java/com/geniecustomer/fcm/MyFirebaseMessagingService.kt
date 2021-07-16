package com.geniecustomer.fcm

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.geniecustomer.R
import com.geniecustomer.view.activities.BookingDetailActivity
import com.geniecustomer.view.activities.ConversationActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService()  {
    private val TAG = MyFirebaseMessagingService::class.java.simpleName
    private var mChannel: NotificationChannel? = null
    private var notifManager: NotificationManager? = null
    var title = ""
    var type = ""
    var receiverId = ""
    var message = ""
    var intent : Intent?=null
    private lateinit var broadcaster : LocalBroadcastManager


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("newToken","hhh $p0")
    }

    override fun onCreate()
    {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    @SuppressLint("WrongConstant")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("csmdncmdsn", "cnksdncknds")

        if (remoteMessage.notification != null) {
            message = remoteMessage.notification!!.body!!
            Log.e("man", "Message Notification Body: " + remoteMessage.notification!!.body!!)
            Log.e("man", "Message Notification Body: " + remoteMessage.notification!!.title!!)
        } else {
            Log.e("Data sadasdasdas", " " + remoteMessage.data)

            displayNotifications(
                remoteMessage.data.get("title").toString(),
                remoteMessage.data.get("body").toString(),
                remoteMessage.data.get("click_action").toString(),
                JSONObject(remoteMessage.data.get("extra").toString())
            )

            val intent = Intent("mNotiDataReceiver")
            intent.putExtra("notidata",""+remoteMessage.data.get("click_action").toString())
            broadcaster.sendBroadcast(intent)

        }
        /*receiverId
        driverData*/
        val gson = Gson()

       /* displayNotifications(
            remoteMessage.data.get("title").toString(),
            remoteMessage.data.get("message").toString(),
            remoteMessage.data.get("eventType").toString(),
            remoteMessage.data.get("bookingId").toString()
        )*/
        // }




    }

    @SuppressLint("WrongConstant")
    private fun displayNotifications(title: String, message: String, clickAction: String, jsonObject: JSONObject? ) {
        Log.e("titil", " $title message ")
        Log.e("titil ", " clickAction $clickAction")

        try {
            if (notifManager == null) {
                notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            if(clickAction.equals("Booking_Request",true) || clickAction.equals("Booking_Request_Complete",true))
            {
                //var obj = JSONObject(remoteMessage.data.get("extra").toString())

                val ii = Intent("notification")
                ii.putExtra("_id",jsonObject?.getString("bookingId"))
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(ii)

                intent = Intent(applicationContext, BookingDetailActivity::class.java)
                intent!!.putExtra("_id",jsonObject?.getString("bookingId"))
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP


            }
            else if(clickAction.equals("New_Message")){
                val messageObj = jsonObject?.getJSONObject("message")
                val receiverObj =  messageObj?.getJSONObject("receiver")
                val senderObj =  messageObj?.getJSONObject("sender")
                val bookingIdObj =  jsonObject?.getJSONObject("bookingId")

                val bookingId =  messageObj?.getString("bookingId")
                val name = senderObj?.getString("name")
                val image = senderObj?.getString("image")
                val senderId = senderObj?.getString("_id")
                val bookingStatus = bookingIdObj?.getString("status")

                intent = Intent(applicationContext,ConversationActivity::class.java)
                intent!!.putExtra("notification","")
                intent!!.putExtra("clickAction",clickAction)
                intent!!.putExtra("name",name)
                intent!!.putExtra("image",image)
                intent!!.putExtra("providerId",senderId)
                intent!!.putExtra("bookingId",bookingId)
                intent!!.putExtra("bookingStatus",bookingStatus)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            /*
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(myIntent)

            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
             */

            val icon = R.mipmap.ic_app_icon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val builder: NotificationCompat.Builder
                val pendingIntent: PendingIntent
                val importance = NotificationManager.IMPORTANCE_HIGH
                if (mChannel == null) {
                    mChannel = NotificationChannel(packageName, title, importance)
                    mChannel!!.description = message
                    mChannel!!.enableVibration(true)
                    notifManager!!.createNotificationChannel(mChannel!!)
                }
                builder = NotificationCompat.Builder(this, packageName)
                pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_app_icon) // required
//                    .setContentText(message)  // required
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, icon))
                    .setBadgeIconType(icon)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                val notification = builder.build()
                notifManager!!.notify(1251, notification)
            }
            else {
                Log.e("2334", "12345")
                var pendingIntent: PendingIntent? = null
                pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = NotificationCompat.Builder(this, packageName)
                    .setContentTitle(title)
//                    .setContentText(message)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.mipmap.ic_app_icon)
                    .setContentIntent(pendingIntent)
                    .setBadgeIconType(icon)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message))
                notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notifManager!!.notify(1251, notificationBuilder.build())
            }
        } catch (e: Exception) {
            Log.e("noti"," Exception "+e)
        }
    }



    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            Log.e("Active process : ", activeProcess)
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity
                Log.e("componentInfo : ", componentInfo.toString())
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return isInBackground
    }
}

/* private val receiver1 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("recierv "," manana")
            if (intent.action!!.equals("notification", ignoreCase = true)) {
                Log.e("data "," "+intent.getStringExtra("message"))
                 if(intent.hasExtra("driverData")){
                    val dataString = intent.getStringExtra("driverData")
                     driverDataForPair = Gson().fromJson(dataString , object : TypeToken<DriverData>() {}.type)
                     //setDataForGenerate()
                }
            }
        }
    }
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver1, IntentFilter("notification"))
    */