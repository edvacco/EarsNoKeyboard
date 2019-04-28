package gwicks.com.earsnokeyboard;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gwicks on 7/08/2016.
 * Class exists to provide AWS details accross the program
 */

public class Constants {

    private static final String TAG = "Constants";

    private SharedPreferences mSharedPreferences;
    /*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     */
    public static final String COGNITO_POOL_ID = "us-west-2:41d228a6-292a-4ebb-9f37-cf96c33063a2";

    public static String deviceID;
    public static String modelName;
    public static String modelNumber;
    public static int androidVersion;
    public static String earsVersion;

    public static String studyName = "default";
    public static String study;
    public static String emaDailyEnd;
    public static String emaDailyStart;
    public static int emHoursBetween;
    public static int emaPhaseBreak;
    public static int emaPhaseFrequency;
    public static Boolean emaVariesDuringWeek = null;
    public static Boolean phaseAutoScheduled = null;
    public static final String awsBucket = "columbia-study";
    public static String[] emaMoodIdentifiers;
    public static String[] emaWeekDay;
    public static String[] emaWeekDays;
    public static String[] includedSensors;

    public static String secureID;

    static String getCognitoPoolId() {
        return COGNITO_POOL_ID;
    }

    static String getStudyName() {
        return studyName;
    }

    static String getStudy() {
        return study;
    }

    static String getEmaDailyEnd() {
        return emaDailyEnd;
    }

    static String getEmaDailyStart() {
        return emaDailyStart;
    }

    static int getEmHoursBetween() {
        return emHoursBetween;
    }

    static int getEmaPhaseBreak() {
        return emaPhaseBreak;
    }

    static int getEmaPhaseFrequency() {
        return emaPhaseFrequency;
    }

    static Boolean getEmaVariesDuringWeek() {
        return emaVariesDuringWeek;
    }

    static Boolean getPhaseAutoScheduled() {
        return phaseAutoScheduled;
    }

//    static String getAwsBucket() {
//        if(awsBucket == null){
//            Log.d(TAG, "getAwsBucket: null, getting from shared prefers ");
//
//           // getPref("bucket");
//
//        }
//        return awsBucket;
//    }


    public static void writeHeaderToFile(File file, String data){

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static String[] getEmaMoodIdentifiers() {
        return emaMoodIdentifiers;
    }

    static String[] getEmaWeekDay() {
        return emaWeekDay;
    }

    static String[] getEmaWeekDays() {
        return emaWeekDays;
    }

    static String[] getIncludedSensors() {
        return includedSensors;
    }

    static String getBucketName() {
        return BUCKET_NAME;
    }

//    public String getPref(String key){
//        mSharedPreferences =PreferenceManager.getDefaultSharedPreferences(AnyApplication.getInstance());
//        String s = mSharedPreferences.getString(key, "default");
//        return s;
//
//    }

    /*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     */
    //public static final String BUCKET_NAME = "adaptvideos";
    //public static final String BUCKET_NAME = "earsnorman";
    //public static final String BUCKET_NAME = "sevencups";
    //public static final String BUCKET_NAME = "katemills";
    //public static final String BUCKET_NAME = "earsrandy";
   //public static final String BUCKET_NAME = "earspitts";
    //public static final String BUCKET_NAME = "adaptvideostest";
//    public static final String BUCKET_NAME = "pitts-study";
   // public static final String BUCKET_NAME = "kki-study";
   public static final String BUCKET_NAME = "columbia-study";







}

