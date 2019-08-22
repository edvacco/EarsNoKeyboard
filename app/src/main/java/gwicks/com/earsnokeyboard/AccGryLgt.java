package gwicks.com.earsnokeyboard;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import research.ResearchEncoding;

/**
 * Created by gwicks on 4/04/2018.
 * Class implements sensor listener in order to record data from Acc, Gryo and Light sensors
 * Data is stored in a String Builder buffer, and written to disk once a certain size limit is reached
 * Wake lock is used to ensure recording continues at all times
 * Battery life does not seem to be too badly affected
 */

public class AccGryLgt extends Service implements SensorEventListener {

    private static final String TAG = "AccGryLgt";

    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLight;
    //String path;
    String path2;

    private static long LAST_TS_ACC = 0;
    private static long LAST_TS_GYRO = 0;
    private static long LAST_SAVE = 0;

    private static PowerManager.WakeLock wakeLock = null;

    StringBuilder accelBuffer;
    StringBuilder gryoBuffer;
    StringBuilder lightBuffer;

    private boolean writingAccelToFile = false;
    private boolean writingGyroToFile = false;
    private boolean writingLightToFile = false;

    float previousLightReading;

    private static Float[] LAST_VALUES_ACC = null;
    private static Float[] LAST_VALUES_GRYO = null;

    double THRESHOLD = 0.01;
    double ACCEL_THRESHOLD = 0.05;
    float lightReading = 0;
    long timeStampLight = 0;

    File AccelFile;
    File GyroFile;
    File LightFile;
    File DestroyFile;

    int accelCount = 0;
    int gyroCount = 0;
    int lightCount = 0;

    ResearchEncoding.AccelGyroEvent AccelEvent[] = new ResearchEncoding.AccelGyroEvent[5010];
    ResearchEncoding.AccelGyroEvent GyroEvent[] = new ResearchEncoding.AccelGyroEvent[5010];
    ResearchEncoding.LightEvent LightEvent[] = new ResearchEncoding.LightEvent[1000];


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope =  sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //return super.onStartCommand(intent, flags, startId);
        wakeLock.acquire();

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            Log.d("Sensors", "" + sensor.getName());
        }

        accelBuffer = new StringBuilder();
        gryoBuffer = new StringBuilder();
        lightBuffer = new StringBuilder();

        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "AccelGyroLight: starting accelgyrolight constructor");

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AccGryLgt - Service");

        //path = Environment.getExternalStorageDirectory() +"/VIDEODIARY";
        path2 = (getExternalFilesDir(null) + "/");
        Log.d(TAG, "AccGryLgt:  the path to externalfilesdir is: " + path2);

        //File directory = new File(path);
        File directory2 = new File(path2);
//
//        if(!directory.exists()){
//            Log.d(TAG, "onCreate: making directory");
//            directory.mkdirs();
//        }

        if(!directory2.exists()){
            Log.d(TAG, "onCreate: making directory");
            directory2.mkdirs();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: the sensor service has been destroyed");
        Log.d(TAG, "onDestroy: sensor manager = null " + sensorManager);
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
       // sensorManager.unregisterListener(this);
        wakeLock.release();
        DestroyFile = new File(path2 +"ACCEL/Destroy_Service.txt");
        writeToFile(DestroyFile, "the file was destroyed at: ");
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long TS = System.currentTimeMillis();


            // Filter to remove readings that come too often
            if (TS < LAST_TS_ACC + 100) {
                return;
            }

            if(LAST_VALUES_ACC != null && Math.abs(event.values[0] - LAST_VALUES_ACC[0]) < ACCEL_THRESHOLD
                    && Math.abs(event.values[1] - LAST_VALUES_ACC[1]) < ACCEL_THRESHOLD
                    && Math.abs(event.values[2] - LAST_VALUES_ACC[2]) < ACCEL_THRESHOLD) {
                return;
            }

            LAST_VALUES_ACC = new Float[]{event.values[0], event.values[1], event.values[2]};

            LAST_TS_ACC = System.currentTimeMillis();


            // Protobuf begin


            ResearchEncoding.AccelGyroEvent eventNew = null;

            try{
                eventNew = ResearchEncoding.AccelGyroEvent.parseFrom(ResearchEncoding.AccelGyroEvent.newBuilder().setTimestamp(LAST_TS_ACC)
                    .setX(event.values[0])
                    .setY(event.values[1])
                    .setZ(event.values[2])
                    .build().toByteArray());
            }catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }


//            ResearchEncoding.AccelGyroEvent eventNew = ResearchEncoding.AccelGyroEvent.newBuilder()
//                    .setTimestamp(LAST_TS_ACC)
//                    .setX(event.values[0])
//                    .setY(event.values[1])
//                    .setZ(event.values[2])
//                    .build();

            if((accelCount < 5010) && writingAccelToFile == false){
                AccelEvent[accelCount] = eventNew;
                accelCount++;
            }



            //Log.d(TAG, "onSensorChanged: the length of accelcount is: " + accelCount);

            if((accelCount >= 5010) && (writingAccelToFile == false) ){
                Log.d(TAG, "onSensorChanged: starting loioop");


                writingAccelToFile = true;

                AccelFile = new File(path2 +"ACCEL/"  + LAST_TS_ACC +"_Service.txt");
                Log.d(TAG, "onSensorChanged: accelfile created at : " + AccelFile.getPath());

                Log.d(TAG, "onSensorChanged: file exitsts: " + AccelFile.exists());
                
                if(!AccelFile.exists()){
                    try {
                        Log.d(TAG, "onSensorChanged: try");
                        AccelFile.createNewFile();
                    } catch (IOException e) {
                        Log.d(TAG, "onSensorChanged: carch");
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "onSensorChanged: file exitsts: " + AccelFile.exists());


                File parent = AccelFile.getParentFile();
                if(!parent.exists() && !parent.mkdirs()){
                    Log.d(TAG, "onSensorChanged: hello");
                    throw new IllegalStateException("Couldn't create directory: " + parent);
                }

                if(AccelFile.length() == 0){

                    WriteToFileHelper.writeHeader(AccelFile);


                }

//                    FileOutputStream fos = null;
//                    Log.d(TAG, "onSensorChanged: New File");
//
//                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
//                            Locale.getDefault());
//                    Date currentLocalTime = calendar.getTime();
//                    SimpleDateFormat date = new SimpleDateFormat("Z");
//                    String localTime = date.format(currentLocalTime);
//
//
//                    ResearchEncoding.Header header = ResearchEncoding.Header.newBuilder()
//                            .setDeviceID(Constants.deviceID)
//                            .setModelName(Constants.modelName)
//                            .setModelNumber(Constants.modelNumber)
//                            .setOsVersion(Integer.toString(Constants.androidVersion))
//                            .setAppVersion(Constants.earsVersion)
//                            .setTimezone(localTime)
//                            .build();
//
//                    try {
//                        fos = new FileOutputStream(AccelFile);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    try{
//                        header.writeTo(fos);
//                    }catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }

                //Try threading to take of UI thread

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "run: starting write to filke");

                        // Protobuf
                        FileOutputStream fos = null;
                        try{
                            //Log.d(TAG, "run: trying");
                            fos = new FileOutputStream(AccelFile,true);

                        } catch (FileNotFoundException e) {
                            //Log.d(TAG, "run: catching");
                            e.printStackTrace();
                        }
                        //for(ResearchEncoding.AccelGyroEvent event : AccelEvent){
                        for(int a=0;a<AccelEvent.length;a++){
                            //Log.d(TAG, "run: event = " + AccelEvent[0]);

                            try {
                                //Log.d(TAG, "run: try 2");
                                AccelEvent[a].writeDelimitedTo(fos);
                            } catch (Exception e) {
                                //Log.d(TAG, "run: catch 3");
                                e.printStackTrace();
                            }
                        }

                        try{
                            fos.close();
                        }catch(Exception e){
                            Log.d(TAG, "run: " + e);
                        }
                        Log.d(TAG, "run: ending");



                        // Old way

//                        writeStringBuilderToFile(AccelFile, accelBuffer);
//                        accelBuffer.setLength(0);
                        writingAccelToFile = false;

                    }


                }).start();
                accelCount = 0;
                Log.d(TAG, "onSensorChanged: ending");
            }
            //Log.d(TAG, "onSensorChanged: the length of i is::::: " + accelCount);
        }

        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE ){
           // Log.d(TAG, "onSensorChanged: gyro");


            long TS = System.currentTimeMillis();
            if (TS < LAST_TS_GYRO + 100) {
                //Log.d(TAG, "onSensorChanged: skipping");
                return;
            }
            // Filter to remove readings that have too small a change from previous reading.
            if(LAST_VALUES_GRYO != null && Math.abs(event.values[0] - LAST_VALUES_GRYO[0]) < THRESHOLD
                    && Math.abs(event.values[1] - LAST_VALUES_GRYO[1]) < THRESHOLD
                    && Math.abs(event.values[2] - LAST_VALUES_GRYO[2]) < THRESHOLD) {
                return;
            }

            LAST_VALUES_GRYO = new Float[]{event.values[0], event.values[1], event.values[2]};


            LAST_TS_GYRO = System.currentTimeMillis();


            // proto start

            ResearchEncoding.AccelGyroEvent eventNew = null;

            try{
                eventNew = ResearchEncoding.AccelGyroEvent.parseFrom(ResearchEncoding.AccelGyroEvent.newBuilder().setTimestamp(LAST_TS_GYRO)
                        .setX(event.values[0])
                        .setY(event.values[1])
                        .setZ(event.values[2])
                        .build().toByteArray());
            }catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

//            ResearchEncoding.AccelGyroEvent eventNew = ResearchEncoding.AccelGyroEvent.newBuilder()
//                    .setTimestamp(LAST_TS_GYRO)
//                    .setX(event.values[0])
//                    .setY(event.values[1])
//                    .setZ(event.values[2])
//                    .build();


            if(gyroCount < 5010){
                GyroEvent[gyroCount] = eventNew;
                gyroCount++;
            }


            //Log.d(TAG, "onSensorChanged: the length of gyrocoutn sis: " + gyroCount);



            //gryoBuffer.append(eventNew);





            // proto end







            //gryoBuffer.append(LAST_TS_GYRO + "," + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n");
            //Log.d(TAG, "onSensorChanged: \n the buffer length is: " + gryoBuffer.length());
            //Log.d(TAG, "onSensorChanged: the buffer is: " + accelBuffer.toString());
            if((gyroCount >=5010) && (writingGyroToFile == false) ){
                writingGyroToFile = true;


                GyroFile = new File(path2 +"GYRO/"  + LAST_TS_GYRO +"_Service.txt");
                Log.d(TAG, "onSensorChanged: file created at: "+ GyroFile.getPath());

                File parent = GyroFile.getParentFile();
                if(!parent.exists() && !parent.mkdirs()){
                    throw new IllegalStateException("Couldn't create directory: " + parent);
                }

                if(GyroFile.length() == 0){

                    WriteToFileHelper.writeHeader(GyroFile);
                }

//                    FileOutputStream fos = null;
//                    Log.d(TAG, "onSensorChanged: New File");
//
//                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
//                            Locale.getDefault());
//                    Date currentLocalTime = calendar.getTime();
//                    SimpleDateFormat date = new SimpleDateFormat("Z");
//                    String localTime = date.format(currentLocalTime);
//
//
//                    ResearchEncoding.Header header = ResearchEncoding.Header.newBuilder()
//                            .setDeviceID(Constants.deviceID)
//                            .setModelName(Constants.modelName)
//                            .setModelNumber(Constants.modelNumber)
//                            .setOsVersion(Integer.toString(Constants.androidVersion))
//                            .setAppVersion(Constants.earsVersion)
//                            .setTimezone(localTime)
//                            .build();
//
//                    try {
//                        fos = new FileOutputStream(GyroFile);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    try{
//                        header.writeTo(fos);
//                    }catch (IOException e) {
//                        e.printStackTrace();
//                    }finally {
//                        if(fos!=null){
//                            try {
//                                fos.close();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }


                //Try threading to take of UI thread

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d(TAG, "onSensorChanged: in accelbuffer");
                        //Log.d(TAG, "run: in runnable");
                        //writeToStream(accelBuffer);
//                        writeStringBuilderToFile(GyroFile, gryoBuffer);
//                        gryoBuffer.setLength(0);



                        FileOutputStream fos = null;
                        try{
                            //Log.d(TAG, "run: trying");
                            fos = new FileOutputStream(GyroFile,true);

                        } catch (FileNotFoundException e) {
                            //Log.d(TAG, "run: catching");
                            e.printStackTrace();
                        }
                        for(int a=0;a<GyroEvent.length;a++){
                            //Log.d(TAG, "run: event = " + AccelEvent[0]);

                            try {
                                //Log.d(TAG, "run: try 2");
                                GyroEvent[a].writeDelimitedTo(fos);
                            } catch (Exception e) {
                                //Log.d(TAG, "run: catch 3");
                                e.printStackTrace();
                            }
                        }

                        writingGyroToFile = false;
                        try{
                            fos.close();
                        }catch(Exception e){
                            Log.d(TAG, "run: " + e);
                        }
                    }
                }).start();
                gyroCount = 0;
            }

        }


        else if(event.sensor.getType() == Sensor.TYPE_LIGHT){

            //Log.d(TAG, "onSensorChanged: light");
            long lightTime = System.currentTimeMillis();

            lightReading = event.values[0];
            //Log.d(TAG, "onSensorChanged: \n the buffer length is: " + lightBuffer.length());

            if(lightReading > (previousLightReading + 3) || lightReading < (previousLightReading -3)){
                timeStampLight = System.currentTimeMillis();

                ResearchEncoding.LightEvent eventNew = null;

                try{
                    eventNew = ResearchEncoding.LightEvent.parseFrom(ResearchEncoding.LightEvent.newBuilder()
                            .setTimestamp(lightTime)
                            .setLevel(event.values[0])
                            .build().toByteArray());
                }catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }




//                ResearchEncoding.LightEvent eventNew = ResearchEncoding.LightEvent.newBuilder()
//                        .setTimestamp(lightTime)
//                        .setLevel(event.values[0])
//                        .build();

                if(lightCount < 1000){
                    LightEvent[lightCount] = eventNew;
                    gyroCount++;
                }


//                if(gyroCount < 5010){
//                    GyroEvent[gyroCount] = eventNew;
//                    gyroCount++;
//                }
//



//                LightEvent[lightCount] = eventNew;
                lightCount++;
                //Log.d(TAG, "onSensorChanged: the length of light count is: " + lightCount);
                previousLightReading = lightReading;
            }

            if((lightCount >=1000) && (writingLightToFile == false) ){

                timeStampLight = System.currentTimeMillis();
                LightFile = new File(path2 +"LIGHT/"  + timeStampLight +"_Service.txt");
                Log.d(TAG, "onSensorChanged: ligtfile created at: " + LightFile.getPath());
                File parent = LightFile.getParentFile();
                if(!parent.exists() && !parent.mkdirs()){
                    throw new IllegalStateException("Couldn't create directory: " + parent);
                }

                if(LightFile.length() == 0){

                    WriteToFileHelper.writeHeader(LightFile);
//
//                    FileOutputStream fos = null;
//                    Log.d(TAG, "onSensorChanged: New File");
//
//                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
//                            Locale.getDefault());
//                    Date currentLocalTime = calendar.getTime();
//                    SimpleDateFormat date = new SimpleDateFormat("Z");
//                    String localTime = date.format(currentLocalTime);
//
//
//                    ResearchEncoding.Header header = ResearchEncoding.Header.newBuilder()
//                            .setDeviceID(Constants.deviceID)
//                            .setModelName(Constants.modelName)
//                            .setModelNumber(Constants.modelNumber)
//                            .setOsVersion(Integer.toString(Constants.androidVersion))
//                            .setAppVersion(Constants.earsVersion)
//                            .setTimezone(localTime)
//                            .build();
//
//                    try {
//                        fos = new FileOutputStream(LightFile);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    try{
//                       header.writeTo(fos);
//                    }catch (IOException e) {
//                        e.printStackTrace();
//                    }finally {
//                        if(fos!=null){
//                            try {
//                                fos.close();
//                                Log.d(TAG, "onSensorChanged: close 1");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }




                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos = null;
                        try{
                            fos = new FileOutputStream(LightFile,true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        for(int a=0;a<LightEvent.length;a++){
                            try {
                                LightEvent[a].writeDelimitedTo(fos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        writingLightToFile = false;
                        //lightCount =0;

                        try{
                            fos.close();
                        }catch(Exception e){
                            Log.d(TAG, "run: " + e);
                        }
                    }
                }).start();
                lightCount = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void writeStringBuilderToFile(File file, StringBuilder builder){
        Log.d(TAG, "writeStringBuilderToFile: in stringbuilder to file");
        BufferedWriter writer = null;


        try {
            writer = new BufferedWriter(new java.io.FileWriter((file)));
            Log.d(TAG, "writeStringBuilderToFile: writiting");
            writer.append(builder);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        //System.out.println("The state of the media is: " + Environment.getExternalStorageState());
        Log.d(TAG, "writeToFile: file location is:" + file.getAbsolutePath());

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
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }
}

