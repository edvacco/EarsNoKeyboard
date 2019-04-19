package gwicks.com.earsnokeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;

public class DailyEMA extends Activity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "DailyEMA";

    public TextView question;
    public SeekBar s1;
    public int seekBarValue;
    public Button finishButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_ema);

        String path = getExternalFilesDir(null) + "/DailyEMA/";

        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
        }

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmm");
        final String formattedDate = df.format(c.getTime());

        s1 = findViewById(R.id.seekBar);
        s1.setOnSeekBarChangeListener(this);
        finishButton = (Button)findViewById(R.id.buttonNext);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uri = (getExternalFilesDir(null) + "/DailyEMA/"+ formattedDate + ".txt");
                File file = new File(uri);
                Log.d(TAG, "onClick: the file url is : " + uri);
                long TS = System.currentTimeMillis();
                String timeStampString = String.valueOf(TS);

                Constants.writeHeaderToFile(file, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + ","+ Constants.androidVersion + "," + Constants.earsVersion + "\n");

                writeToFile(file, timeStampString + "\n");

                writeToFile(file, Integer.toString(seekBarValue) + "\n");

                Log.d(TAG, "onClick: 2");

                Toast.makeText(getBaseContext(), "Thank you for completing the Daily EMA  :)", Toast.LENGTH_LONG).show();
                Intent returnToFinish = new Intent(DailyEMA.this, FinishInstallScreen.class);

                startActivity(returnToFinish);
            }
        });



    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Log.d(TAG,  " the progress is: " + i);
        seekBarValue = i;

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        //System.out.println("The state of the media is: " + Environment.getExternalStorageState());

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
