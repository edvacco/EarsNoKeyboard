package gwicks.com.earsnokeyboard;

import android.app.Application;
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

    public void onCreate(){
        Log.d(TAG, "onCreate: anyapplication oncreate");

        super.onCreate();
        //context = this ;
        instance = this;
        Log.d(TAG, "onCreate: instance = " + instance);
    }

    public static AnyApplication getInstance() {
        Log.d(TAG, "getInstance: getting instance");
        Log.d(TAG, "getInstance: instance = " + instance);
        return instance;
    }
}
