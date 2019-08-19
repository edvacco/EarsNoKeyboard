package gwicks.com.earsnokeyboard;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;
import gwicks.com.earsnokeyboard.Setup.LaunchKeyboardDialog;
import research.ResearchEncoding;

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



        if(!isAccessibilityEnabled(this, "gwicks.com.earsnokeyboard/.KeyLogger")){
            // do the keyboard thing again.
            launchKeyboardDialog();

        }

        String path = getExternalFilesDir(null) + "/DAILY/";

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

                String uri = (getExternalFilesDir(null) + "/DAILY/"+ formattedDate + ".txt");
                File file = new File(uri);
                FileOutputStream fos = null;

                if(file.length() == 0){

                    WriteToFileHelper.writeHeader(file);

                }



                Log.d(TAG, "onClick: the file url is : " + uri);
                long TS = System.currentTimeMillis();
                String timeStampString = String.valueOf(TS);

                ResearchEncoding.EMAEvent event = null;

                try{
                    event = ResearchEncoding.EMAEvent.parseFrom(ResearchEncoding.EMAEvent.newBuilder()
                            .setTimeInitiated(TS)
                            .addQuestion(ResearchEncoding.EMAEvent.Question.newBuilder().setIntAnswer(seekBarValue)).build().toByteArray());
                }catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

//                ResearchEncoding.EMAEvent event = ResearchEncoding.EMAEvent.newBuilder()
//                        .setTimeInitiated(TS)
//                        .addQuestion(ResearchEncoding.EMAEvent.Question.newBuilder().setIntAnswer(seekBarValue)).build();

                try {
                    fos = new FileOutputStream(file, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try{
                    event.writeDelimitedTo(fos);
                }catch (IOException e) {
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



//                Constants.writeHeaderToFile(file, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + ","+ Constants.androidVersion + "," + Constants.earsVersion + "\n");
//
//                writeToFile(file, timeStampString + "\n");
//
//                writeToFile(file, Integer.toString(seekBarValue) + "\n");
//
//                Log.d(TAG, "onClick: 2");
//
//                Toast.makeText(getBaseContext(), "Thank you for completing the Daily EMA  :)", Toast.LENGTH_LONG).show();
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

    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }

    public void launchKeyboardDialog(){

        DialogFragment newFragment = new LaunchKeyboardDialog();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "keyboard");
    }
}
