package gwicks.com.earsnokeyboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class FirebaseEMAReceiver extends BroadcastReceiver {

    private static final String TAG = "FirebaseEMAReciever";
    private NotificationManager mNotificationManager;

    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    String CHANNEL_DI = "EMA_notfication";
    Context mContext;
    String stringExtra = null;

    //public String myString;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 21");

        // This next part is to ensure the notification does not go off during sleep.
        // Note may still be firing at night, which is bad for battery life, and should be fixed
        //TODO: Change time checking method to no longer fire during sleep, and be discarded ( bad for battery)
        mContext = context;

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        int installDay = mSharedPreferences.getInt("doyf", 0);
        Log.d(TAG, "onReceive: install day: " + installDay);



        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        Log.d(TAG, "onReceive: 1.1");

        int finishDay = finishDay(installDay);
        Log.d(TAG, "onReceive: 2");

        //TODO this will keep the alarm firing every 2 hours for years....not ideal, how can i make better?

        Log.d(TAG, "onReceive: day of year = " + dayOfYear + "finsish day: " + finishDay);

        if(dayOfYear > finishDay){
            Log.d(TAG, "onReceive: skip 1");

            // Attempt to cancel the alarm:

//            AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//            Intent intentNew;
//            intentNew = new Intent(this, FirebaseEMAReceiver.class);
//
//            int alarmID = intent.getExtras().getInt("alarmID");
//            PendingIntent alarmIntent;
//            alarmIntent = PendingIntent.getBroadcast(this, alarmID,
//                    new Intent(this, FirebaseEMAReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
//            cancelAlarm(alarmIntent);

//            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(this, FirebaseEMAReceiver.class);
//            intent.putExtra("EMA", "EMA1");
//            startEMAFirebaseIntent = PendingIntent.getBroadcast(this, 91, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
//            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 120, startEMAFirebaseIntent);
//            Log.d(TAG, "startFirebaseEMAAlarm: alarm shjould be set");

            return;
        }
//        if(dayOfYear - installDay > 8){
//            return;
//        }


        if(isWeekday(dow)){
            if((hour < 16) || (hour > 23)){
                Log.d(TAG, "onReceive: wrong hour skipping weekday");
                return;
            }

        }else{
            if((hour < 10) || (hour > 23)){
                Log.d(TAG, "onReceive:  wrong hour skipping weekend");
                return;
            }
        }



        Log.d(TAG, "onReceive: in receive ");

        if(mNotificationManager == null){
            Log.d(TAG, "onReceive: in notification manager = null");
            //mNotificationManager = FinishInstallScreen.notificationManager;
            mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE) ;
            Log.d(TAG, "onReceive: notificiation manager  = " + mNotificationManager);
            //mNotificationManager = EMA.
            //mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Log.d(TAG, "onReceive: before clear notifications");

        clearNotfications();
        Log.d(TAG, "onReceive: after clear notifications");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String name = "Oreo_Notificaitons";
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(CHANNEL_DI);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_DI, name, importance);
                mChannel.setDescription("blah_1");
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);

            }
        }

//        Intent snoozeIntent = new Intent(context, EMASleepReceiver.class);
//        snoozeIntent.setAction("SNOOZE");
//        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
//        PendingIntent snoozePendingIntent =
//                PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);

        Random random = new Random();

        int nxt = random.nextInt(99);


        Intent resultIntent = new Intent(context, FireBaseEMA.class);

        //5th October 2018, prevent not firing properly

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //Intent resultIntent = new Intent(context, SecondEMA.class);


        // 3rd October thing to stop not firing properly lots of ideas below:

        //int dummyInt = new Random().nextInt(3435);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, dummyInt, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, nxt, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, "SNOOZE", snoozePendingIntent).build();

        Notification mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_DI)
                        .setSmallIcon(R.drawable.noti_icon)
                        .setContentTitle("Quick Survey")
                        .setAutoCancel(true)
                        .setContentText("Time for another quick survey :)")
                        .setOngoing(true)
                        .setChannelId(CHANNEL_DI)
                        .setSound(uri)
                        .setContentIntent(pendingIntent)
                        //.addAction(action)
                        .build();


        mNotificationManager.notify("first",1, mBuilder);
        Log.d(TAG, "onReceive OREO: should be notification built now");



    }

    private Calendar setTimeToCalendar(String dateFormat, String date, boolean addADay) {
        Date time = null;
        try {
            time = new SimpleDateFormat(dateFormat).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(time );

        if(addADay) {
            cal.add(Calendar.DATE, 1);
        }
        return cal;
    }

    public void clearNotfications(){

        Log.d(TAG, "clearNotfication: in cancel");

        //NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //Log.d(TAG, "clearNotfications: " + notificationManager.getActiveNotifications().toString());
        //mNotificationManager.

        mNotificationManager.cancel("first", 1);
        mNotificationManager.cancel("second", 2);
        mNotificationManager.cancel("third", 3);

        if(mNotificationManager != null){
            mNotificationManager.cancelAll();
        }
    }

    public boolean isWeekday(int dayOfWeek){
        return ((dayOfWeek >= Calendar.MONDAY) && dayOfWeek <= Calendar.FRIDAY);
    }

    public int finishDay(int doy){

        int i;
        if(doy <= 358){
            i = doy +7;
        }else{
            i = (doy - 365) + 7;
        }
        Log.d(TAG, "calculateEduLinkDay: so calculated dow is: " + i);

        return i;

    }
}

