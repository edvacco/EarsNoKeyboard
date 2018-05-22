package gwicks.com.earsnokeyboard;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static gwicks.com.earsnokeyboard.Setup.SetupStepThree.isAccessibilityEnabled;

/**
 * Created by gwicks on 11/05/2018.
 */

public class KeyLogger extends AccessibilityService {

    private static final String TAG = "KeyLogger";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {


        Calendar c = Calendar.getInstance();


        SimpleDateFormat df2 = new SimpleDateFormat("yyyy:MM:dd");
        String currentDate = df2.format(c.getTime());
        String path = this.getExternalFilesDir(null) + "/videoDIARY/KeyLogger/";

        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.mkdirs();
        }

        File location = new File(directory, currentDate +".txt");





        //Log.d(TAG, "onAccessibilityEvent: WE ARE IN accessibility Event");
        //boolean onOrOff = MainActivity.isAccessibilityServiceEnabled(this, MainActivity.class);
        boolean enabled2 = isAccessibilityEnabled(this, "gwicks.com.keylogger/.KeyLogger");
        //Log.d(TAG, "onAccessibilityEvent: on or off enalbed 2 = :" + enabled2);
        //Log.d(TAG, "onAccessibilityEvent: on or off : " + onOrOff);
        long unixTime = System.currentTimeMillis();
        if(accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
            String data = accessibilityEvent.getText().toString();
            String data2 = accessibilityEvent.getBeforeText().toString();
            String data3 =  accessibilityEvent.getPackageName().toString();

            Log.d(TAG, "TIME: " + unixTime + ": The data is: " + data);
            //Log.d(TAG, "onAccessibilityEvent: the before text is: " + data2);
            //Log.d(TAG, "onAccessibilityEvent: the time is: " + unixTime);
            Log.d(TAG,unixTime + "," + data3 +"," + data);

            writeToFile(location, unixTime + "," + data3 +"," + data +"\n");
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
