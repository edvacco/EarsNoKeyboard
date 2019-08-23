package gwicks.com.earsnokeyboard;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HttpsURLConnection;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;
import research.ResearchEncoding;

/**
 * Created by gwicks on 14/05/2018.
 * Different EMA for Columbia and Pitts. Sends alert SMS and email (through AWS), if a participant triggers threshold
 */

public class SecondEMA extends Activity {

    private static final String TAG = "SecondEMA";

    private RadioGroup radio1;
    private RadioGroup radio2;
    private RadioGroup radio3;

    private Button finishedButton;

    int firstQuestion;
    int secondQuestion;
    int thirdQuestion;

    private String secureDeviceID;
    TransferUtility transferUtility;
    String uri;
    String encryptedUri;
    Encryption mEncryption;
    File mFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_ema);


        secureDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Calendar c = Calendar.getInstance();
        String path = getExternalFilesDir(null) + "/EMA/";
        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
        }

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmm");
        final String formattedDate = df.format(c.getTime());
        Log.d(TAG, "onCreate: formated date  = " + formattedDate);
        mEncryption = new Encryption();
        transferUtility = Util.getTransferUtility(this);

        radio1 = findViewById(R.id.RGroup);

        radio1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.Never){
                    firstQuestion = 1;
                } else if(i == R.id.Rarely){
                    firstQuestion = 2;
                } else if(i == R.id.Sometime){
                    firstQuestion = 3;
                } else if(i == R.id.Often){
                    firstQuestion = 4;
                }else if(i == R.id.All){
                    firstQuestion = 5;
                }
            }
        });

        radio2 = findViewById(R.id.RGroup2);
        radio2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.Yes){
                    secondQuestion = 2;
                }else if(i == R.id.No){
                    secondQuestion = 1;
                }
            }
        });

        radio3 = findViewById(R.id.RGroup3);
        radio3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.Yes2){
                    thirdQuestion = 2;
                }else if(i == R.id.No2){
                    thirdQuestion = 1;
                }
            }
        });

        finishedButton = (Button)findViewById(R.id.button);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uri = (getExternalFilesDir(null) + "/EMA/"+ formattedDate + ".txt");
                mFile = new File(uri);
                Log.d(TAG, "onClick: the file url is : " + uri);
                long TS = System.currentTimeMillis();
                String timeStampString = String.valueOf(TS);
                FileOutputStream fos = null;

                ResearchEncoding.EMAEvent.Builder b = ResearchEncoding.EMAEvent.newBuilder();
                b.setTimeInitiated(TS);
                try{
                    b.addQuestion(ResearchEncoding.EMAEvent.Question.parseFrom(ResearchEncoding.EMAEvent.Question.newBuilder().setIntAnswer(firstQuestion).build().toByteArray()));
                    b.addQuestion(ResearchEncoding.EMAEvent.Question.parseFrom(ResearchEncoding.EMAEvent.Question.newBuilder().setIntAnswer(secondQuestion).build().toByteArray()));
                    b.addQuestion(ResearchEncoding.EMAEvent.Question.parseFrom(ResearchEncoding.EMAEvent.Question.newBuilder().setIntAnswer(thirdQuestion).build().toByteArray()));
                }catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                try {
                    fos = new FileOutputStream(mFile, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try{
                    b.build().writeDelimitedTo(fos);
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



//
//                writeToFile(mFile, timeStampString + "\n");
//                writeToFile(mFile, secureDeviceID + "\n");
//                writeToFile(mFile, firstQuestion + "," + secondQuestion + "," + thirdQuestion + "\n");

                encryptedUri = Encrypt(timeStampString, uri);

                // Check if any of the participants answers had trigger threshold

                if(healthCheck()){
                    //TODO: send special upload to AWS
                    Log.d(TAG, "onClick: in health check as postive");

                    // OLD SMS VERSION
                    beginUpload2(timeStampString, encryptedUri, true);
                    Log.d(TAG, "onClick: afer begin upload");

//                    sendSMS(Constants.phoneNumberOne, secureDeviceID + ", q1: " + firstQuestion + ", q2: " + secondQuestion + ", q3: " + thirdQuestion);
//                    sendSMS(Constants.phoneNumberTwo, secureDeviceID + ", q1: " + firstQuestion + ", q2: " + secondQuestion + ", q3: " + thirdQuestion);
//


                    // Randy SMS below
                   // sendSMS("9179814866", secureDeviceID + ", q1: " + firstQuestion + ", q2: " + secondQuestion + ", q3: " + thirdQuestion);
                    //sendSMS("3868820636", secureDeviceID + ", q1: " + firstQuestion + ", q2: " + secondQuestion + ", q3: " + thirdQuestion);

                    // University of Pittsburgh Medical Center below
//
//                    sendSMS("4125232034", secureDeviceID + ", q1: " + firstQuestion + ", q2: " + secondQuestion + ", q3: " + thirdQuestion);
//                    sendSMS("7245136376", secureDeviceID + ", q1: " + firstQuestion + ", q2: " + secondQuestion + ", q3: " + thirdQuestion);
//                    sendSMS("4125256737", secureDeviceID + ", q1: " + firstQuestion + ", q2: " + secondQuestion + ", q3: " + thirdQuestion);




                    //Testing new Dialog Frag, commented out the above to avoid spamming randy

                    // END OLD VERSION BELOW IS THE API GATEWAY SMS METHOD



                    Log.d(TAG, "onClick: send AWS SMS");

                    SecondEMA.PostAWS postAWS = new SecondEMA.PostAWS();
                    postAWS.execute();
                    Log.d(TAG, "onClick: After AWS send sms");





                    launchPhoneNumberDialog();

                }else{
                    beginUpload2(timeStampString, encryptedUri, false);
                    Intent returnToFinish = new Intent(SecondEMA.this, FinishInstallScreen.class);

                    startActivity(returnToFinish);
                }

                Toast.makeText(getBaseContext(), "Thank you for completing the questionnaire, they will appear once a week on Wednesday", Toast.LENGTH_LONG).show();
//                Intent returnToFinish = new Intent(SecondEMA.this, FinishInstallScreen.class);
//
//                startActivity(returnToFinish);
            }
        });
    }

    private static void writeToFile(File file, String data) {

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean healthCheck(){
        if( firstQuestion == 4 || firstQuestion == 5 || secondQuestion == 2 || thirdQuestion == 2 ){
            return true;
        }
        return false;
    }


    public String Encrypt(String name, String path){
        Log.d(TAG, "Encrypt: 1");
        Log.d(TAG, "Encrypt: name = " + name);
        Log.d(TAG, "Encrypt: path = " + path);
        String mFileName = name;
        Log.d(TAG, "Encrypt: mFIleName is: " + mFileName);
        String mFilePath = path;
        Log.d(TAG, "Encrypt: mFileName is: " + mFileName);
        Log.d(TAG, "Encrypt: mFilePath is: " + mFilePath);

        String path2 = null;
        try {
            //com.anysoftkeyboard.utils.Log.d(TAG, "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
            path2 = mEncryption.encrypt(mFileName, mFilePath, "/videoDIARY/");
            Log.d(TAG, "Encrypt: the path me get is: " + path2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Encrypt: 2");
        Log.d(TAG, "Encrypt: path2 is: " + path2);
        //beginUpload2("STATS", path2);
        return path2;



    }

    private void beginUpload2(String name, String filePath, Boolean suicide) {
        Log.d(TAG, "beginUpload2: start of beginupload2");
        Log.d(TAG, "beginUpload2: the filepath is: " + filePath);
        String key;
        if (filePath == null) {
            //Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            Log.d(TAG, "beginUpload2: no file path found");
            return;
        }

        if(suicide){
            key = secureDeviceID + "/EMAALERT/" + name +"_" + firstQuestion + "_" + secondQuestion + "_" + thirdQuestion + ".alert";
        } else{
            key = secureDeviceID + "/RISK/" + name;
        }


        Log.d(TAG, "beginUpload2: middle");

        File file = new File(filePath);
        //TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, name,
        transferUtility.upload(Constants.awsBucket, key,
                file);
        Log.d(TAG, "beginUpload2: end");

    }

    public void sendSMS(String phoneNo, String msg) {
        Log.d(TAG, "sendSMS: in send message via sms");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }



    private class PostAWS extends AsyncTask<String, String, String> {

        private static final String TAG = "PostAWS";

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: 3");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: 4");
            String postData = "";
            HttpsURLConnection urlConnection = null;

            try{

                String stringUrl = createUri();
                URL finalURL = new URL(stringUrl);
                urlConnection = (HttpsURLConnection)finalURL.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);



                urlConnection.setRequestProperty("x-api-key","43b93m3VYH4odlxAoQuJq6e4hwV4EX1E9phmgsn8" );



                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int inputStreamData = inputStreamReader.read();
                while(inputStreamData != -1){
                    char currentData = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    postData += currentData;
                    Log.d(TAG, "doInBackground: postdata: " + postData + "\n");
                }



                Log.i("STATUS", String.valueOf(urlConnection.getResponseCode()));
                Log.i("MSG" , urlConnection.getResponseMessage());
                Log.d(TAG, "doInBackground: " + urlConnection.getContentEncoding());
                Log.d(TAG, "doInBackground:  " + urlConnection.getContentType());
                Log.d(TAG, "doInBackground: " + urlConnection.getHeaderFields());
                Log.d(TAG, "doInBackground: " + urlConnection.getOutputStream());

                urlConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: error");
            }catch(Exception e){
                Log.d(TAG, "doInBackground: error");
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return postData;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d(TAG, "onPostExecute: SMS sent with string return: " + s);

            super.onPostExecute(s);
        }
    }

    private String createUri(){

        Log.d(TAG, "createUri: DeviceID: " + Constants.deviceID);
        String baseURL = "https://pws4gvrjvb.execute-api.us-west-2.amazonaws.com/default/sendSMSAlert";
        Log.d(TAG, "createUri: uri builder");

        Log.d(TAG, "createUri: constants.study: " + Constants.study);

        String myUri =  Uri.parse(baseURL).buildUpon()
                .appendQueryParameter("deviceID", Constants.deviceID)
//                .appendQueryParameter("study", Constants.study)
                .appendQueryParameter("studyName", "maps")

                .appendQueryParameter("riskOne",String.valueOf(firstQuestion))
                .appendQueryParameter("riskTwo", String.valueOf(secondQuestion))
                .appendQueryParameter("riskThree",String.valueOf(thirdQuestion))
                .build().toString();

        Log.d(TAG, "createUri: string is: " + myUri);
        return myUri;
    }


    public void launchPhoneNumberDialog(){
        DialogFragment phoneFragment = new HealthCheckDialog();
        phoneFragment.show(getFragmentManager(), "phone");
    }






}
