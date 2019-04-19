package gwicks.com.earsnokeyboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

public class DailyEMAAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "DailyEMAAlarmReceiver";
    private NotificationManager mNotificationManager;

    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    String CHANNEL_DI = "Daily_EMA_notfication";
    Context mContext;
    String stringExtra = null;



    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

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

        Random random = new Random();

        int nxt = random.nextInt(99);

        Intent resultIntent = new Intent(context, DailyEMA.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, nxt, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_DI)
                        .setSmallIcon(R.drawable.noti_icon)
                        .setContentTitle("Daily Status Report")
                        .setAutoCancel(true)
                        .setContentText("Answer just one question")
                        .setOngoing(true)
                        .setChannelId(CHANNEL_DI)
                        .setSound(uri)
                        .setContentIntent(pendingIntent)
                        //.addAction(action)
                        .build();


        mNotificationManager.notify("first",4, mBuilder);
        Log.d(TAG, "onReceive OREO: should be notification built now");



    }
}
