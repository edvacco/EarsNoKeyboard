package gwicks.com.earsnokeyboard.Setup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import gwicks.com.earsnokeyboard.Constants;
import gwicks.com.earsnokeyboard.R;


public class StudyCodeVerification extends AppCompatActivity implements GetRawData.OnDownloadComplete, GetRawDataTwo.OnDownloadCompleteTwo {

    private static final String TAG = "StudyCodeVerification";
    EditText studyCode;
    String finalCode;
    String creationDate = "";

    String informedConsent;
    SharedPreferences mSharedPreferences;

    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;

    private BarcodeDetector mBarcodeDetector;
    private TextView scanResults;
    private Uri imageUri;

    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_code);
        updateStatusBarColor("#1281e8");
        studyCode = (EditText)findViewById(R.id.studyCode);
        informedConsent = getString(R.string.informed_consent);


        // Check for camera permissions to use Camera
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    this, Manifest.permission.CAMERA)) {


            } else {
                ActivityCompat.requestPermissions((Activity) this,
                        new String[]{Manifest.permission.CAMERA},
                        11);
            }
        }
    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public void qrcode(View v) {

        Log.d(TAG, "qrcode: qrcode clicked");
        ActivityCompat.requestPermissions(StudyCodeVerification.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);

        mBarcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!mBarcodeDetector.isOperational()) {
            Log.d(TAG, "onCreate: not working");
            return;
        }


    }

    public void informedConsent(View v) {
//
//        //Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
//        //code = studyCode.getText().toString();
//        code = code2;

        finalCode = studyCode.getText().toString().trim();
        Log.d(TAG, "informedConsent: finalCode: " + finalCode);
        validateStudyCode(studyCode.getText().toString());


    }

    public void validateStudyCode(String code){
//        String url = "http://date.jsontest.com";
//        new MyAsyncTask().execute(url);
        Log.d(TAG, "validateStudyCode: ");
        Log.d(TAG, "validateStudyCode: code entered: " + code);

        GetRawData getRawData = new GetRawData(this);
        //getRawData.execute("https://u8x53zf4i5.execute-api.us-east-1.amazonaws.com/default/EARS-study-code-verification?code=" + code);
        Log.d(TAG, "validateStudyCode: EXECUTE!!!!!!!!!!!!!!!!");
        getRawData.execute("https://aufpk43i29.execute-api.us-west-2.amazonaws.com/default/EARS-study-code-verification?code=" + code);


        Log.d(TAG, "validateStudyCode: returning false");

    }


    public void onDownloadComplete(String data, DownloadStatus status){

            Log.d(TAG, "onDownloadComplete: step2 == false");
            if(status == DownloadStatus.OK){
                Log.d(TAG, "onDownloadComplete: data is : " + data);

                if(data.contains("study code has already been claimed!")){
                    Toast.makeText(this, "That study code has already been claimed, please contact your study coordinator",Toast.LENGTH_LONG).show();
                    return;
                }

                Boolean claimed = null;
                String study = "";

                // 29th March 2019 JSON attempt
                try{
                    JSONObject jsonData = new JSONObject(data);
                    claimed = jsonData.getBoolean("claimed");
                    study = jsonData.getString("study");
                    creationDate = jsonData.getString("studyCodeCreationDate");
                    Log.d(TAG, "onDownloadComplete: claimed: " + claimed + ", study: " + study + ", creationDate: " + creationDate);
                }catch(JSONException e){
                    Log.d(TAG, "onDownloadComplete:  error processing json: " + e.getMessage());
                }
                Log.d(TAG, "onDownloadComplete: 3");

                //TODO add a check here in case of error to prevent crash

                if(claimed == null){
                    Toast.makeText(this, "That study code does not exist, please contact your study coordinator",Toast.LENGTH_LONG).show();
                    return;
                }

                if(claimed.equals(false)){
                    showDialog();
                    createStudy(study);

                }else{
                    Toast.makeText(this, "You have entered an incorrect study code. This app is only for approved participants", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "You have entered an incorrect study code. This app is only for approved participants", Toast.LENGTH_LONG).show();

                }
            }else{
                Log.d(TAG, "onDownloadComplete: failed with status: " + status);
                Toast.makeText(this, "Your study code is incorrect",Toast.LENGTH_LONG).show();
            }
        }


    @Override
    public void onDownloadCompleteTwo(String data, DownloadStatus2 status) {
        if(status == DownloadStatus2.OK2){

            Log.d(TAG, "onDownloadComplete: step 2 == true");
            String study = "";
            String emaDailyEnd;
            String emaDailyStart;
            int emHoursBetween;
            //String[] emaMoodIdentifiers;
            int emaPhaseBreak;
            int emaPhaseFrequency;
            Boolean emaVariesDuringWeek = null;
            //String[] emaWeekDay;
//            String[] emaWeekDays;
//            String[] includedSensors;
            Boolean phaseAutoScheduled = null;
            String awsBucket;

            Log.d(TAG, "onDownloadComplete: in the step2 = true part");
            Log.d(TAG, "onDownloadComplete: the study complete data is : " + data);

            try{
                JSONObject studyJsonObject = new JSONObject(data);
                study = studyJsonObject.getString("studyName");
                //emaPhaseFrequency = studyJsonObject.getInt("emaPhaseFrequency");
                emaDailyEnd = studyJsonObject.getString("emaDailyEnd");
                emaDailyStart = studyJsonObject.getString("emaDailyStart");
                emHoursBetween = studyJsonObject.getInt("emaHoursBetween");

                JSONArray moodIdentifers = studyJsonObject.getJSONArray("emaMoodIdentifiers");
                String[] emaMoodIdentifiers = new String[moodIdentifers.length()];
                for(int i = 0; i<moodIdentifers.length(); i++){
                    emaMoodIdentifiers[i] = moodIdentifers.getString(i);
                }
                emaPhaseBreak = studyJsonObject.getInt("emaPhaseBreak");
                emaPhaseFrequency = studyJsonObject.getInt("emaPhaseFrequency");
                emaVariesDuringWeek = studyJsonObject.getBoolean("emaVariesDuringWeek");
                JSONArray weekDay = studyJsonObject.getJSONArray("emaWeekDay");
                String[] emaWeekDay = new String[weekDay.length()];
                for(int i =0;i<weekDay.length();i++){
                    emaWeekDay[i] = weekDay.getString(i);
                }
                JSONArray weekDays = studyJsonObject.getJSONArray("emaWeekDays");
                String[] emaWeekDays = new String[weekDays.length()];
                for(int i =0;i<weekDays.length();i++){
                    emaWeekDays[i] = weekDays.getString(i);
                }
                JSONArray sensors = studyJsonObject.getJSONArray("includedSensors");
                String[] includedSensors = new String[sensors.length()];
                for(int i =0; i<sensors.length();i++){
                    includedSensors[i] = sensors.getString(i);
                }
                phaseAutoScheduled = studyJsonObject.getBoolean("phaseAutoScheduled");
                awsBucket = studyJsonObject.getString("s3BucketName");

                Log.d(TAG, "onDownloadComplete: study: " + study + " emaPhasa: " + emaPhaseFrequency);

                Study thisStudy = new Study(study, emaDailyEnd, emaDailyStart, emHoursBetween, emaMoodIdentifiers, emaPhaseBreak, emaPhaseFrequency, emaVariesDuringWeek,emaWeekDay,emaWeekDays, includedSensors,phaseAutoScheduled,awsBucket);

                // Set all the constants, may not need
                Constants.studyName = study;
                Constants.study = study;
                Constants.emaDailyEnd = emaDailyEnd;
                Constants.emaDailyStart = emaDailyStart;
                Constants.emHoursBetween = emHoursBetween;
                Constants.emaPhaseBreak = emaPhaseBreak;
                Constants.emaPhaseFrequency = emaPhaseFrequency;
                Constants.emaVariesDuringWeek = emaVariesDuringWeek;
                Constants.phaseAutoScheduled = phaseAutoScheduled;
                Constants.awsBucket = awsBucket;
                Constants.emaMoodIdentifiers = emaMoodIdentifiers;
                Constants.emaWeekDay = emaWeekDay;
                Constants.emaWeekDays = emaWeekDays;
                Constants.includedSensors = includedSensors;


                mSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("bucket", awsBucket);
                editor.putString("studyName", study);
                editor.putString("emaDailyStart", awsBucket);
                editor.putString("emaDailyEnd", study);
                editor.putInt("emaHoursBetween", emHoursBetween);
                editor.putInt("emaPhaseBreak", emaPhaseBreak);
                editor.putInt("emaPhaseFrequency", emaPhaseFrequency);

                editor.apply();

                Log.d(TAG, "onDownloadComplete: toString: " + thisStudy.toString());

            }catch(JSONException e){
                Log.d(TAG, "onDownloadComplete: error processing study JSON: " + e.getMessage());
            }
        }
    }


    public void startInstall()

    {

        updateStudyCode();
        Log.d(TAG, "startInstall: ");
//        Intent installIntent = new Intent(StudyCodeVerification.this, SetupStepTwo.class);
//        StudyCodeVerification.this.startActivity(installIntent);
//        finish();

    }

    public void showDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(StudyCodeVerification.this).create();
        //alertDialog.setTitle("7 Cups EARS: Informed Consent & Terms of Service Agreement");
        alertDialog.setTitle("EARS: Informed Consent & Terms of Service Agreement");
        alertDialog.setMessage(Html.fromHtml(informedConsent));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "I Disagree",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"I Agree",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startInstall();
            }
        });
        alertDialog.show();

    }

    public void createStudy(String study){

        Log.d(TAG, "createStudy: begin");

        GetRawDataTwo createStudyGetRawData = new GetRawDataTwo(this);
        Log.d(TAG, "createStudy: EXECUTE@!@!@!@!@!@!@!@@!@!@");
        createStudyGetRawData.execute("https://7ocx4sxhze.execute-api.us-west-2.amazonaws.com/default/get-study-variables?study=" + study);
        Log.d(TAG, "createStudy: end");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: 11");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(StudyCodeVerification.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: 1");
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                Log.d(TAG, "onActivityResult: 2");
                if (mBarcodeDetector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = mBarcodeDetector.detect(frame);
                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        studyCode.setText(code.displayValue);

                        //Required only if you need to extract the type of barcode
                        int type = barcodes.valueAt(index).valueFormat;
                        switch (type) {
                            case Barcode.CONTACT_INFO:
                                Log.i(LOG_TAG, code.contactInfo.title);
                                break;
                            case Barcode.EMAIL:
                                Log.i(LOG_TAG, code.email.address);
                                break;
                            case Barcode.ISBN:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.PHONE:
                                Log.i(LOG_TAG, code.phone.number);
                                break;
                            case Barcode.PRODUCT:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.SMS:
                                Log.i(LOG_TAG, code.sms.message);
                                break;
                            case Barcode.TEXT:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.URL:
                                Log.i(LOG_TAG, "url: " + code.url.url);
                                break;
                            case Barcode.WIFI:
                                Log.i(LOG_TAG, code.wifi.ssid);
                                break;
                            case Barcode.GEO:
                                Log.i(LOG_TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                                break;
                            case Barcode.CALENDAR_EVENT:
                                Log.i(LOG_TAG, code.calendarEvent.description);
                                break;
                            case Barcode.DRIVER_LICENSE:
                                Log.i(LOG_TAG, code.driverLicense.licenseNumber);
                                break;
                            default:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                        }
                    }
                    if (barcodes.size() == 0) {
                        scanResults.setText("Scan Failed: Found nothing to scan");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void takePicture() {
        Log.d(TAG, "takePicture: 4");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(getExternalFilesDir(null), "/photo.jpg");

        try
        {
            if(photo.exists() == false)
                Log.d(TAG, "startCamera:2 ");
            {
                photo.getParentFile().mkdirs();
                Log.d(TAG, "startCamera: 1");
                photo.createNewFile();
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not create file.", e);
        }

        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(intent,PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            Log.d(TAG, "onSaveInstanceState: RESULTS: " + imageUri.toString() + " " + SAVED_INSTANCE_RESULT.toString());

        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Log.d(TAG, "launchMediaScanIntent: 5");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        Log.d(TAG, "decodeBitmapUri: 6");
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    public void updateStudyCode(){

        Log.d(TAG, "updateStudyCode: 1");
        //nextTry();

        PostAWS postAWS = new PostAWS();
        postAWS.execute();
        Log.d(TAG, "updateStudyCode: 2");

    }


    private class PostAWS extends AsyncTask<String, String, String>{

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
                //URL url = new URL("https://aufpk43i29.execute-api.us-west-2.amazonaws.com/default/EARS-study-code-verification");
                String stringUrl = createUri();
                URL finalURL = new URL(stringUrl);
                //URL url = new URL("https://aufpk43i29.execute-api.us-west-2.amazonaws.com/default/EARS-study-code-verification?code=3719026908f5412f" + "&study=test"  + "&studyCodeCreationDate=1554720022297" + "&OS=android&deviceID=" +Constants.deviceID);
                //Log.d(TAG, "doInBackground: url: " + url.toString());
                urlConnection = (HttpsURLConnection)finalURL.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);


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

            Boolean success = false;
            Log.d(TAG, "onPostExecute: string s = " + s);
            try{
                JSONObject studyJsonObject = new JSONObject(s);
                success = studyJsonObject.getBoolean("success");
                Log.d(TAG, "onPostExecute: success: " + success);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(success){
                Intent installIntent = new Intent(StudyCodeVerification.this, SetupStepTwo.class);
                StudyCodeVerification.this.startActivity(installIntent);
                finish();

            }else{
                Toast.makeText(StudyCodeVerification.this, "Error connecting to server, please try again", Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(s);
        }
    }

    private String createUri(){
        String baseURL = "https://aufpk43i29.execute-api.us-west-2.amazonaws.com/default/EARS-study-code-verification";
        Log.d(TAG, "createUri: uri builder");

        Log.d(TAG, "createUri: constants.study: " + Constants.study);
        Log.d(TAG, "createUri: finalCode: " + finalCode + " helloworld");


        String myUri =  Uri.parse(baseURL).buildUpon()
                .appendQueryParameter("code", finalCode)
                .appendQueryParameter("study", Constants.study)
                .appendQueryParameter("studyCodeCreationDate",creationDate)
                .appendQueryParameter("OS","android")
                .appendQueryParameter("deviceID", Constants.deviceID)
                .build().toString();

        Log.d(TAG, "createUri: string is: " + myUri);
        return myUri;
    }
}
