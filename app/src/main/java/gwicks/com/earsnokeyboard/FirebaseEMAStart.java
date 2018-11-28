package gwicks.com.earsnokeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class FirebaseEMAStart extends AppCompatActivity {
    private static final String TAG = "FirebaseEMAStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        //FirebaseMessaging.getInstance().subscribeToTopic("TEST");

        Intent intent = new Intent(this, FireBaseEMA.class);
        startActivity(intent);


    }
}
