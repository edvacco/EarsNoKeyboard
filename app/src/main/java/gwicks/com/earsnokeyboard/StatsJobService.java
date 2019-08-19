package gwicks.com.earsnokeyboard;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
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
 */
public class StatsJobService extends JobService {

    Context myContext;
    static String folder = "/videoDIARY/";
    private static final String TAG = "StatsJobService";
    @Override
    public boolean onStartJob(JobParameters params) {
        GPSTracker mGPSTracker = new GPSTracker(this);


        myContext = this.getApplication().getApplicationContext();
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        SimpleDateFormat df2 = new SimpleDateFormat("ddMMyyyy");
        String currentDate = df2.format(c.getTime());

        String path = this.getExternalFilesDir(null) + "/videoDIARY/Location/";

        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.
                    mkdirs();
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
//
//
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







        double latitude = mGPSTracker.getLatitude();
        double longitude = mGPSTracker.getLongitude();

        long TS = System.currentTimeMillis();


        // Start of Protobuf implementation 1/7/19

        FileOutputStream fos = null;

        ResearchEncoding.GPSEvent event = null;

        try{
            event = ResearchEncoding.GPSEvent.parseFrom(ResearchEncoding.GPSEvent.newBuilder()
                    .setTimestamp(TS)
                    .setLat(latitude)
                    .setLon(longitude)
                    .build().toByteArray());
        }catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        try {
            fos = new FileOutputStream(location,true);
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

        // End protobuf





        Log.d(TAG, "onStartJob: Time: " + formattedDate + "  Latitude: " + latitude + "  Longitude: " + longitude);
//        Constants.writeHeaderToFile(location, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + ","+ Constants.androidVersion + "," + Constants.earsVersion +"\n");
//        writeToFile(location, "Time: " + formattedDate + "  Latitude: " + latitude + "  Longitude: " + longitude +"\n");

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: in on stop job");
        return false;
    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        //Log.d(TAG, "The state of the media is: " + Environment.getExternalStorageState());
        //Log.d(TAG, "writeToFile: file location is:" + file.getAbsolutePath());

        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            //Log.e("History", "In try");
            //Log.d(TAG, "writeToFile: ");
            stream = new FileOutputStream(file, true);
            //Log.d(TAG, "writeToFile: 2");
            stream.write(data.getBytes());
            //Log.d(TAG, "writeToFile: 3");
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
