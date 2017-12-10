package com.struggleassist.View.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.struggleassist.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Ryan on 10/20/2017.
 * Purpose: This class is designed only to push notifications to the user
 * This class formats the notification and sets up basic user interaction
 * NotificationReceiver.java is responsible for the outcomes of the actions (buttons) pressed
 */

/**
 * TODO: Notification Timeout Timer not resetting after each incident
 */

public class Notification {

    //Give notification context
    Context nContext;
    public Notification(Context nContext){
        this.nContext = nContext;
    }

    private static final int uniqueID = 2112;

    private static Notification mNotification = null;

    public static final String CANCEL_ACTION = "com.struggleassist.View.Notifications.Notification.cancelAction";
    public static final String CONFIRM_ACTION = "com.struggleassist.View.Notifications.Notification.confirmAction";
    public static final String TIMEOUT_ACTION = "com.struggleassist.View.Notifications.Notification.timeoutAction";

    public static final int timeoutLength = 30000;
    public static final int tickLength = 1000;

    CountDownTimer mCountDownTimer;

    public void Notify(String notificationTitle, String notificationMessage){

        mNotification = this;

        //Initialize notification layout
        RemoteViews remoteViews = new RemoteViews(nContext.getPackageName(),
                R.layout.small_notification_layout);

        //Confirm button's intent
        Intent confirmIntent = new Intent(nContext,NotificationReceiver.class)
                .setAction(CONFIRM_ACTION)
                .putExtra("uniqueID",uniqueID);
        PendingIntent confirmPendingIntent = PendingIntent.getBroadcast(nContext, 0, confirmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.smallNotificationButtonConfirm, confirmPendingIntent);

        //Cancel button's intent
        Intent cancelIntent = new Intent(nContext,NotificationReceiver.class)
                .setAction(CANCEL_ACTION)
                .putExtra("uniqueID",uniqueID);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(nContext,0,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.smallNotificationButtonCancel, cancelPendingIntent);

        //Notification Timeout's intent
        final Intent timeoutIntent = new Intent(nContext,NotificationReceiver.class)
                .setAction(TIMEOUT_ACTION)
                .putExtra("uniqueID",uniqueID);
        final PendingIntent timeoutPendingIntent = PendingIntent.getBroadcast(nContext, 0, timeoutIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        //Create notification
        android.app.Notification notification = new NotificationCompat.Builder(nContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContent(remoteViews)
                .setCustomBigContentView(remoteViews)
                .build();

        //Deploy notification
        NotificationManager notificationManager = (NotificationManager) nContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID,notification);

        //Countdown timer
        mCountDownTimer = new CountDownTimer(timeoutLength,tickLength){
            public void onTick(long millisUntilFinished){}
            public void onFinish(){
                try{
                    timeoutPendingIntent.send(nContext,0,timeoutIntent);
                } catch (PendingIntent.CanceledException e){
                    System.out.println("Sending timeoutIntent failed");
                }
            }
        }.start();
    }

    //To be called by the confirm/cancel buttons to cancel the timer
    public void cancelTimer(){
        mCountDownTimer.cancel();
    }

    //To give the notification receiver context of the current notification
    public static Notification getInstance(){
        return mNotification;
    }
}