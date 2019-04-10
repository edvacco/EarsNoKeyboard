package gwicks.com.earsnokeyboard;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

/**
        * Created by gwicks on 6/10/2017.
        * This class provides a static context if need anywhere in the Application. Currently this Class is NOT used
        * anywhere
        */


public class AnyApplication extends Application {

    //private static Context context;
    private static final String TAG = "AnyApplication";
    private static AnyApplication instance;
    private SharedPreferences mSharedPreferences;

    //private String secureID;

    public void onCreate(){
        Log.d(TAG, "onCreate: anyapplication oncreate");



        super.onCreate();

//        secureID = Settings.Secure.getString(
//                AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        //context = this ;
        instance = this;
        Log.d(TAG, "onCreate: instance = " + instance);

        if(Constants.awsBucket == null){
            setBucketName();
        }
        if(Constants.deviceID == null){
            setDeviceID();
        }
    }

    public static AnyApplication getInstance() {
        Log.d(TAG, "getInstance: getting instance");
        Log.d(TAG, "getInstance: instance = " + instance);
        return instance;
    }

    public void setBucketName(){
        mSharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);
        String s = mSharedPreferences.getString("bucket", "default");
        Constants.awsBucket = s;

    }

    public void setDeviceID(){
        String secureID = Settings.Secure.getString(
                AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        Constants.deviceID = secureID;
    }

//    public String getSecureID(){
//        return secureID;
//    }
}
