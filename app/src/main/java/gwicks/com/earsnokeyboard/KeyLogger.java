package gwicks.com.earsnokeyboard;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import research.ResearchEncoding;

import static gwicks.com.earsnokeyboard.Setup.SetupStepThree.isAccessibilityEnabled;

/**
 * Created by gwicks on 11/05/2018.
 * The Keylogger that logs the keyboard input using the accessibility service.
 */

public class KeyLogger extends AccessibilityService {

    private static final String TAG = "KeyLogger";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        String data = "";

        String dataPackage = "";


        Calendar c = Calendar.getInstance();


        SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy");
        String currentDate = df2.format(c.getTime());
        String path = this.getExternalFilesDir(null) + "/videoDIARY/KeyLogger/";

        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.mkdirs();
        }

        File location = new File(directory, currentDate +".txt");


        if(location.length() == 0){


            WriteToFileHelper.writeHeader(location);

//            FileOutputStream fos = null;
//            Log.d(TAG, "onSensorChanged: New File");
//
//            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
//                    Locale.getDefault());
//            Date currentLocalTime = calendar.getTime();
//            SimpleDateFormat date = new SimpleDateFormat("Z");
//            String localTime = date.format(currentLocalTime);


//            ResearchEncoding.Header header = ResearchEncoding.Header.newBuilder()
//                    .setDeviceID(Constants.deviceID)
//                    .setModelName(Constants.modelName)
//                    .setModelNumber(Constants.modelNumber)
//                    .setOsVersion(Integer.toString(Constants.androidVersion))
//                    .setAppVersion(Constants.earsVersion)
//                    .setTimezone(localTime)
//                    .build();
//
//            try {
//                fos = new FileOutputStream(location);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try{
//                header.writeTo(fos);
//            }catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                if(fos!=null){
//                    try {
//                        fos.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }




        }
        //Constants.writeHeaderToFile(location, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + ","+ Constants.androidVersion + "," + Constants.earsVersion + "\n");




        //Log.d(TAG, "onAccessibilityEvent: WE ARE IN accessibility Event");
        //boolean onOrOff = MainActivity.isAccessibilityServiceEnabled(this, MainActivity.class);
        boolean enabled2 = isAccessibilityEnabled(this, "gwicks.com.keylogger/.KeyLogger");
        //Log.d(TAG, "onAccessibilityEvent: on or off enalbed 2 = :" + enabled2);
        //Log.d(TAG, "onAccessibilityEvent: on or off : " + onOrOff);
        long unixTime = System.currentTimeMillis();
        if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){

            FileOutputStream fos = null;

            try{
                data = accessibilityEvent.getText().toString();
            }catch(Exception e){
                Log.d(TAG, "onAccessibilityEvent: exception caught");
            }

            try{
                dataPackage =  accessibilityEvent.getPackageName().toString();
            }catch(Exception e){
                Log.d(TAG, "onAccessibilityEvent: caught excpetion");
            }

            Log.d(TAG, "TIME: " + unixTime + ": The data is: " + data);
            Log.d(TAG,unixTime + "," + dataPackage +"," + data);


            // Protobuf test begin


            ResearchEncoding.KeyEvent event = null;

            try{
                event = ResearchEncoding.KeyEvent.parseFrom(ResearchEncoding.KeyEvent.newBuilder()
                .setTimestamp(unixTime)
                        .setApp(dataPackage)
                        .setTextField(data)
                        .build().toByteArray() );
            }catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }


//            ResearchEncoding.KeyEvent event = ResearchEncoding.KeyEvent.newBuilder()
//                    .setTimestamp(unixTime)
//                    .setApp(dataPackage)
//                    .setTextField(data)
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





            // Proto end



            //writeToFile(location, unixTime + "," + dataPackage +"," + data +"\n");
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        Log.d("Keylogger", "Starting service");
    }


    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
        } catch (FileNotFoundException e) {
            Log.e("History", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
