package gwicks.com.earsnokeyboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String TAG = "SeekBarFragment";

    public int seekBarValue;
    public String messageString;
    TextView value;
    static String startString = "How ";
    static String endString = " do you feel right now?";
    public boolean moved = false;


    public static SeekBarFragment newInstance(String message)

    {
        Log.d(TAG, "newInstance: ");
        Log.d(TAG, "the message is: " + message);
        SeekBarFragment f = new SeekBarFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, startString + message + endString);
        f.setArguments(bdl);
        return f;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        String message = getArguments().getString(EXTRA_MESSAGE);
        messageString = message;
        View v = inflater.inflate(R.layout.seekbar_one, container, false);



        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView messageTextView = (TextView)view.findViewById(R.id.question1);
        value = (TextView)view.findViewById(R.id.textValue);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        messageTextView.setText(messageString);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged: " + progress);
        moved = true;
        String valueNew = String.valueOf(progress);
        value.setText(valueNew);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekBarValue = seekBar.getProgress();

    }

    public int getSeekBarValue(){

        if(moved){
            return seekBarValue;
        }else{
            return -1;
        }



    }
    public String getSeekBarString(){
        return messageString;
    }
}
