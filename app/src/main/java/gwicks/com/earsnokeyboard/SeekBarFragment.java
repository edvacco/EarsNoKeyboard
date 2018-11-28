package gwicks.com.earsnokeyboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


    public static SeekBarFragment newInstance(String message)

    {
        Log.d(TAG, "newInstance: ");
        SeekBarFragment f = new SeekBarFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
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
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        messageTextView.setText(messageString);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged: " + progress);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekBarValue = seekBar.getProgress();

    }

    public int getSeekBarValue(){

        return seekBarValue;

    }
    public String getSeekBarString(){
        return messageString;
    }
}
