package gwicks.com.earsnokeyboard;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by gwicks on 11/05/2018.
 * Get the phone usage stats of the user. Requires permission
 */

public class UStats {

    public static final String TAG = UStats.class.getSimpleName();

    static String directoryName = "/videoDIARY/";
    static String time;

    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Log.d(TAG, "getUsageStatsList: SDF =  " + sdf);

        long endTime = calendar.getTimeInMillis();
        //calendar.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis() - 24*60*60*1000;
        Log.d(TAG, "getUsageStatsList: endtime: " + endTime + "starttime: " + startTime);

        Date one = new Date(startTime);
        Date two = new Date(endTime);

        Log.d(TAG, "getUsageStatsList: data start time: " + one);
        Log.d(TAG, "getUsageStatsList: date endtime:  " + two);
        time = two.toString();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  startTime,endTime);     // calendar.getTimeInMillis(), System.currentTimeMillis()); //(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);

        Map<String, UsageStats> stats = usm.queryAndAggregateUsageStats(startTime, endTime);

        for(String key : stats.keySet()){
            Log.d(TAG, "getUsageStatsList: KEYS: " + stats.get(key));
        }

        for(Map.Entry<String, UsageStats> entry : stats.entrySet()){
            Log.d(TAG, "getUsageStatsList: " + entry.getKey() + " " + entry.getValue().getTotalTimeInForeground());
        }



        Collections.sort(usageStatsList, new TotalTimeUsed());
        return usageStatsList;
    }

    public static String printUsageStats(List<UsageStats> usageStatsList, Context context){

        Log.d(TAG, "printUsageStats: in print");

        String uri = (context.getExternalFilesDir(null) + directoryName + "AppUsage_" + time + ".txt");

        Log.d(TAG, "printUsageStats: The string URI for file is: " + uri);


        File file = new File(uri);
        for (UsageStats u : usageStatsList){

            if(u.getTotalTimeInForeground() > 0){

                int minutes = (int)u.getTotalTimeInForeground()/60000;
                int seconds = (int)(u.getTotalTimeInForeground() % 60000) / 1000;
                Log.d(TAG, "printUsageStats: minutes: " + minutes + " seconds: " + seconds);
                //writeToFile(file, "UsageStats: minutes: " + minutes + " seconds: " + seconds +"\n");
                Log.d(TAG, "Pkg: " + u.getPackageName()  + "\n\tForegroundTime: "
                        + u.getTotalTimeInForeground()/1000 + " seconds " );// mDateFormat.format(u.getLastTimeUsed()) + " time last used") ;
                writeToFile(file, "Pkg: " + u.getPackageName() +   "\nForegroundTime: "
                        + u.getTotalTimeInForeground()/1000 + " seconds \n" );
                Date data = new Date(u.getLastTimeUsed());

                Log.d(TAG, "printUsageStats: DATE = " + data);
                //writeToFile(file, "printUsageStats: DATE = " + data + "\n");


                Log.d(TAG, "printUsageStats: Time last used: " +data);
                writeToFile(file, "Time last used: " +data +"\n");
                Log.d(TAG, "printUsageStats: ______________________________________________________");
                writeToFile(file, " ______________________________________________________\n\n");
            }
        }
        return uri;
    }

    public static String printCurrentUsageStatus(Context context){
        return printUsageStats(getUsageStatsList(context), context);
    }
    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    private static class TotalTimeUsed implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        System.out.println("The state of the media is: " + Environment.getExternalStorageState());

        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            Log.e("History", "In try");
            Log.d(TAG, "writeToFile: ");
            stream = new FileOutputStream(file, true);
            Log.d(TAG, "writeToFile: 2");
            stream.write(data.getBytes());
            Log.d(TAG, "writeToFile: 3");
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
