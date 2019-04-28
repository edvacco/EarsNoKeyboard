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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by gwicks on 31/03/2018.
 * Notification for EMA. Checks time to make sure the EMA is received every two hours
 * between 8am and 10pm. Sets up the snooze function ( EMASleepReceiver )
 */

public class EMAAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "EMAAlarmReciever";
    private NotificationManager mNotificationManager;

    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    String CHANNEL_DI = "EMA_notfication";
    Context mContext;
    String stringExtra = null;

    private int timeStartHour = 16;
    private int timeEndHour = 23;
    private int timeStartHourWeekEnd = 10;
    private int timeEndHourWeekend = 23;

    //public String myString;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 1");

        // This next part is to ensure the notification does not go off during sleep.
        // Note may still be firing at night, which is bad for battery life, and should be fixed
        //TODO: Change time checking method to no longer fire during sleep, and be discarded ( bad for battery)
        mContext = context;


        // THIS IS FOR THE ABCD STUDY TO DO EMAS FOR ONE WEEK:
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int doy = mSharedPreferences.getInt("doy",0);
        Log.d(TAG, "onReceive: doy = " + doy);
        Calendar cal = Calendar.getInstance();
        int doy2 = cal.get(Calendar.DAY_OF_YEAR);
        int finishDay = finishDay(doy);
        if(doy2 > finishDay){


            //TODO untested code below to cancel the alarms, leave commented out for now
//
//            PendingIntent startEMAIntent = PendingIntent.getBroadcast(mContext, 21, new Intent(mContext, EMAAlarmReceiver.class),
//                    PendingIntent.FLAG_CANCEL_CURRENT);
//            AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//            alarmMgr.cancel(startEMAIntent);


            return;
        }

        int hour = cal.get(Calendar.HOUR_OF_DAY);

        // END ABCD STUDY TIME

        Log.d(TAG, "onReceive: THe intent extra is: "+ intent.getStringExtra("EMA"));
        Log.d(TAG, "onReceive: the intent action is : " + intent.getAction());


        // What the fuck is this?

        if(stringExtra != null){
            Log.d(TAG, "onReceive: stringExtra is already set to EMA1");
            return;
        }

        int dow = cal.get(Calendar.DAY_OF_WEEK);

        if(isWeekday(dow)){

            if((hour < timeStartHour) || (hour > timeEndHour)){
                Log.d(TAG, "onReceive: wrong time weekday");
                return;
            }
        }

        if(!isWeekday(dow)){
            //int hour = cal.get(Calendar.HOUR_OF_DAY);
            if((hour < timeStartHourWeekEnd) || (hour > timeEndHourWeekend)){
                Log.d(TAG, "onReceive: wrong time weekend");
                return;
            }
        }

        // OLD method of getting time for EMA's depreciated


        stringExtra = intent.getStringExtra("EMA");
//        Log.d(TAG, "onReceive: IN FIRST 1");
//        String dateFormat = "HH:mm:ss";
//        String endTime= "23:30:00";
//        String startTime = "08:00:00";
//        String currentTime = new SimpleDateFormat(dateFormat).format(new Date());
//
//        Calendar cStart = setTimeToCalendar(dateFormat, startTime, false);
//        Calendar cEnd = setTimeToCalendar(dateFormat, endTime, false);
//        Calendar cNow = setTimeToCalendar(dateFormat, currentTime, false );
//        Date curDate = cNow.getTime();
//
//        Log.d(TAG, "onReceive: cStart: " + cStart.getTime() + " cEnd: " + cEnd.getTime());
//        Log.d(TAG, "onReceive: curdate.after(cStart: " + curDate.after(cStart.getTime()));
//        Log.d(TAG, "onReceive: curDate.before(Cend: " + curDate.before(cEnd.getTime()));
//
//        if (curDate.after(cStart.getTime()) && curDate.before(cEnd.getTime())) {
//            System.out.println("Time is out of range time is: " + currentTime + " curDate: " + curDate);
//            Log.d(TAG, "Date is in range, firing notification:");
//
//        } else {
//            System.out.println("Date is out of range there fore skippingf: ");
//            return;
//        }

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
//
//        if(mNotificationManager != null){
//            mNotificationManager.cancelAll();
//        }
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

    public boolean isWeekday(int dayOfWeek){
        return ((dayOfWeek >= Calendar.MONDAY) && dayOfWeek <= Calendar.FRIDAY);
    }
}
