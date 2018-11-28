package gwicks.com.earsnokeyboard;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private SharedPreferences prefs;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");

//        Calendar cal = Calendar.getInstance();
//        int doy = cal.get(Calendar.DAY_OF_YEAR);
//
//        Log.d(TAG, "onMessageReceived: day of year: " + doy);
//
//        Date date = new Date(System.currentTimeMillis());
//        long millis = date.getTime();
//
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putLong("InstallTime", millis);
//        editor.putInt("doyf", doy);
//        editor.apply();

//        Log.d(TAG, "onMessageReceived: I am here");
//
//        Map<String, String> data = remoteMessage.getData();
//        String mydata = data.get("extra_information");
//        Log.d(TAG, "onMessageReceived: mydata: " + mydata);
//
//        JSONObject object = new JSONObject(data);
//        Log.d(TAG, "onMessageReceived: myobject: " + object.toString());
//        JSONArray itemsArray = object.optJSONArray("feelings");
//        String arraydata = data.get("feelings");
//        Log.d(TAG, "onMessageReceived: arraydata: " + arraydata);
//
//
//
//        Type listType = new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType();
//
//        Gson gson = new Gson();
//
//        ArrayList<Map<String,String>> myList = gson.fromJson(arraydata, listType);
//
//        ArrayList<String> listy = new ArrayList<>();
//
//        // List<String> otem = myList.toArray();
//        //Log.d(TAG, "onMessageReceived: myList: " + my);
//
//        for (Map<String,String> m : myList)
//        {
//            Log.d(TAG, "onMessageReceived: 1");
//            listy.add(String.valueOf(m.keySet()));
//            System.out.println(m.keySet());
//            Log.d(TAG, "onMessageReceived: " + m.entrySet());
//            //listy.add(m.keySet());
//
//        }
//        for(Map.Entry<String, String> entry : data.entrySet()){
//            Log.d(TAG, "onMessageReceived: entry: " + entry.getKey() + " value: " + entry.getValue());
//        }
//        // Arrays.toString(listy.toArray());
//
//        List<String> list2 = new ArrayList<String>();
//
//        for(String s : listy){
//            Log.d(TAG, "onMessageReceived: s: " + s);
//            list2 = new ArrayList<String>(Arrays.asList(s.split(" , ")));
//
//        }
//
//        for(String s : list2){
//            Log.d(TAG, "onMessageReceived: s: " +  s);
//        }



//        try {
//            JSONObject o2 = new JSONObject(arraydata);
//            Log.d(TAG, "onMessageReceived: o2" + o2.toString());
//            try {
//                JSONArray newArray2 = new JSONArray(o2);
//                Log.d(TAG, "onMessageReceived: newArr " + o2.length());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            JSONArray newArray = new JSONArray(arraydata);
//            Log.d(TAG, "onMessageReceived: newArr " + newArray.length());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }





        //Log.d(TAG, "onMessageReceived: itemsarray: " + itemsArray.length());
        //JSONArray itemsArray = object.optJSONArray("feelings");
//
//        for(int i = 0;i<itemsArray.length();i++){
//            try {
//                Log.d(TAG, "onMessageReceived: items array: " + itemsArray.get(i));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }










        //VERSION WORKING FOR IN FOREGROUND:


//        String title = remoteMessage.getNotification().getTitle();
//        String message = remoteMessage.getNotification().getBody();
        String title = "EARS EMA";
        String message = "Please complete another EMA";

        Log.d(TAG, "onMessageReceived: message: " + message + " titel: " + title);
        Log.d(TAG, "onMessageReceived: all: " + remoteMessage.getData());

        boolean bool = Boolean.parseBoolean(remoteMessage.getData().get("reset"));
        Log.d(TAG, "onMessageReceived: boolean is:  " + bool);

        // The reset boolean = TRUE, so we need to restart the app, not start the EMA's

        if(bool){
            sendResetNotification("EARS TOOL", "Please press to restart");

        }else{
            sendNotification(title, message);
        }



        //sendNotification(title, message);

        // TODO why is this not called everytime???? Confused
        // TODO does this only happen when in foreground?
        // TODO: seems to be only when in foreground

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.

        //TODO if the app is in the foreground, I need to do something here to call sendNotification(), because it will not hire, probs not much chance of that happening though
        // TODO the data key values are caught in MainActivity, should be easy to do something with them

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                //scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title1 = remoteMessage.getNotification().getTitle();
            String message2 = remoteMessage.getNotification().getBody();
            Log.d(TAG, "onMessageReceived: message: " + message2 + " titel: " + title1);
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //Log.d(TAG, "onMessageReceived: data: " + remoteMessage.getData().toString());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
//    private void scheduleJob() {
//
//        Log.d(TAG, "scheduleJob: ");
//        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
//        // [END dispatch_job]
//    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {


        Calendar cal = Calendar.getInstance();
        int doy = cal.get(Calendar.DAY_OF_YEAR);

        Log.d(TAG, "onMessageReceived: day of year: " + doy);

        Date date = new Date(System.currentTimeMillis());
        long millis = date.getTime();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("InstallTime", millis);
        editor.putInt("doyf", doy);
        editor.apply();



        PendingIntent startEMAFirebaseIntent;
        Log.d(TAG, "startEMAAlarm: in start ema alarm");

        //Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();

        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 15);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, FirebaseEMAReceiver.class);
        intent.putExtra("EMA", "EMA1");
        startEMAFirebaseIntent = PendingIntent.getBroadcast(this, 91, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),alarmMgr.INTERVAL_DAY * 7 , startEMAIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 120, startEMAFirebaseIntent);
        Log.d(TAG, "startFirebaseEMAAlarm: alarm shjould be set");
        //alarmStarted = true;



        // This is to show a notification? Do I need to do this?

//        Random random = new Random();
//
//        int nxt = random.nextInt(99);
//        Log.d(TAG, "sendNotification: what the actual fuck");
//        Intent intent = new Intent(this, FirebaseEMAStart.class);
//        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, nxt, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        String channelId = "CHANNEL_ID";
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.noti_icon)
//                        .setContentTitle(title)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendResetNotification(String title, String messageBody){
        Random random = new Random();

        int nxt = random.nextInt(99);
        Log.d(TAG, "sendNotification: what the actual fuck");
        Intent intent = new Intent(this, FinishInstallScreen.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, nxt, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "CHANNEL_ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.noti_icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}