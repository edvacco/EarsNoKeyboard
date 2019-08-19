package gwicks.com.earsnokeyboard;

import android.content.Context;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import research.ResearchEncoding;

/**
 * Created by gwicks on 11/05/2018.
 * Listen to all notifications, and if it is a music, record. Will not get a complete list of all music apps, but seems to work with the
 * major ones like spotify, pandora etc
 */

public class MusicNotificationListener extends NotificationListenerService {
    private static final String TAG = "MusicNotificationListen";

    Context mContext;
    String prevTitle;
    String currentTitle;

    @Override

    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = "";
        try {
            //title = extras.getString("android.title");
            title = extras.getCharSequence("android.title").toString();

        }
        catch(NullPointerException e){
           // e.printStackTrace();
        }

        String text = ""; // Needed to avoid null errors

        Calendar c = Calendar.getInstance();

        long time  = System.currentTimeMillis();

        SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy");
        String currentDate = df2.format(c.getTime());

        String path = mContext.getExternalFilesDir(null) + "/videoDIARY/Music/";


        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.mkdirs();
        }

        File location = new File(directory, currentDate +".txt");

        if(location.length() == 0){
            WriteToFileHelper.writeHeader(location);
        }

        try {
            text = extras.getCharSequence("android.text").toString();
        }
        catch(NullPointerException e){
           // e.printStackTrace();
        }
        FileOutputStream fos = null;

        currentTitle = title;


        if(pack.contains("music") && (!currentTitle.equals(prevTitle))){

            // Start of Protobuf

            ResearchEncoding.MusicEvent event = null;
            try{
                event = ResearchEncoding.MusicEvent.parseFrom(ResearchEncoding.MusicEvent.newBuilder()
                        .setTimestamp(time)
                        .setApp(pack)
                        .setTitle(title)
                        .setText(text)
                        .build().toByteArray());
            }catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }


//            ResearchEncoding.MusicEvent event = ResearchEncoding.MusicEvent.newBuilder()
//                    .setTimestamp(time)
//                    .setApp(pack)
//                    .setTitle(title)
//                    .setText(text)
//                    .build();

            try {
                fos = new FileOutputStream(location, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                event.writeDelimitedTo(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }




            // End of protobuf

//            writeToFile(location, " { Time: " + time + ",\nPackage: " + pack + ",\nTitle: " + title + ",\nText: " + text + "\n}\n");
            prevTitle = currentTitle;
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }


    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;

        try {
             stream = new FileOutputStream(file, true);
             stream.write(data.getBytes());
            //Log.d(TAG, "writeToFile: 3");
        } catch (FileNotFoundException e) {
            //Log.e("History", "In catch");
           // e.printStackTrace();
        } catch (IOException e) {
           // e.printStackTrace();

        }
        try {

            stream.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}