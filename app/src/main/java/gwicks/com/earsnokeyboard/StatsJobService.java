package gwicks.com.earsnokeyboard;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        double latitude = mGPSTracker.getLatitude();
        double longitude = mGPSTracker.getLongitude();

        Log.d(TAG, "onStartJob: Time: " + formattedDate + "  Latitude: " + latitude + "  Longitude: " + longitude);
        Constants.writeHeaderToFile(location, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + ","+ Constants.androidVersion + "," + Constants.earsVersion +"\n");
        writeToFile(location, "Time: " + formattedDate + "  Latitude: " + latitude + "  Longitude: " + longitude +"\n");

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
