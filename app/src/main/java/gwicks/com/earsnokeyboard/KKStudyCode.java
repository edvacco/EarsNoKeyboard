package gwicks.com.earsnokeyboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import gwicks.com.earsnokeyboard.Setup.SetupStepTwo;

import static gwicks.com.earsnokeyboard.Setup.Intro.writeToFile;

public class KKStudyCode  extends AppCompatActivity {

    private static final String TAG = "StudyCodeVerification";
    EditText studyCode;
    String code;
    String informedConsent;
    private String secureDeviceID;
    TransferUtility transferUtility;

    String Uri;
    String encryptedUri;
    Encryption mEncryption;
    File mFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_code_kk);
        updateStatusBarColor("#1281e8");
        secureDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        studyCode = (EditText)findViewById(R.id.studyCode);
        informedConsent = getString(R.string.informed_consent);
        mEncryption = new Encryption();
        transferUtility = Util.getTransferUtility(this);

        String path = getExternalFilesDir(null) + "/StudyCode/";
        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
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

    public void informedConsent(View v) {

        //Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
        code = studyCode.getText().toString();
        Log.d(TAG, "informedConsent: the study code is: " + code);
        Uri = (getExternalFilesDir(null) + "/StudyCode/"+ "StudyCode.txt");
        mFile = new File(Uri);
        writeToFile(mFile, secureDeviceID + "\n");
        writeToFile(mFile, code);
        //encryptedUri = Encrypt("StudyCode", Uri);
        encryptedUri = Encrypt(mFile.getName(), mFile.getAbsolutePath());
        beginUpload2("StudyCode",Uri);
        showDialog();
    }


    public void startInstall()

    {

        Log.d(TAG, "startInstall: ");
        Intent installIntent = new Intent(KKStudyCode.this, SetupStepTwo.class);
        KKStudyCode.this.startActivity(installIntent);
        finish();

    }

    public void showDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(KKStudyCode.this).create();
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
            
            if(mFileName==null){
                Log.d(TAG, "Encrypt: 11");
            }
            if(mFilePath == null){
                Log.d(TAG, "Encrypt: 22");
            }
            //com.anysoftkeyboard.utils.Log.d(TAG, "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
            path2 = mEncryption.encrypt(mFileName, mFilePath, "/StudyCode/");
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

    private void beginUpload2(String name, String filePath) {
        Log.d(TAG, "beginUpload2: start of beginupload2");
        Log.d(TAG, "beginUpload2: the filepath is: " + filePath);
        String key;
        if (filePath == null) {
            //Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            Log.d(TAG, "beginUpload2: no file path found");
            return;
        }



        Log.d(TAG, "beginUpload2: middle");

        File file = new File(filePath);
        key = secureDeviceID + "/StudyCode/" + name;
        //TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, name,
        transferUtility.upload(Constants.BUCKET_NAME, key,
                file);
        Log.d(TAG, "beginUpload2: end");

    }
}
