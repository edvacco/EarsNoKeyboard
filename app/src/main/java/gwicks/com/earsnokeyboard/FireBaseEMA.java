package gwicks.com.earsnokeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;

public class FireBaseEMA extends FragmentActivity {

    private static final String TAG = "PageViewActivity";

    MyPageAdapter pageAdapter;

    public ArrayList<String> questions;
    int numOfQuestions;
    TextView buttonTextView;

    public static int int1;
    List<Fragment> fragments;

    List<Fragment> fList;

    ArrayList<String> options;
    String formattedDate;

    long timeStarted;
    long timeFinished;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_layout_firebase);
        String path = getExternalFilesDir(null) + "/EMA/";
        File directory = new File(path);
        if(!directory.exists()){
            directory.mkdirs();
        }


        Calendar c = Calendar.getInstance();
        timeStarted =System.currentTimeMillis();

                //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmm");
        formattedDate = df.format(c.getTime());
        Log.d(TAG, "onCreate: formated date  = " + formattedDate);

        questions = new ArrayList<String>();
        questions.add("How confident do you feel right now?");
        questions.add("How happy do you feel right now?");
        questions.add("How calm do you feel right now?");
        questions.add("How anxious do you feel right now?");
        questions.add("How stressed do you feel right now?");
        questions.add("How sad do you feel right now?");
        questions.add("How angry do you feel right now?");
        questions.add("How supported do you feel right now?");
        questions.add("How included do you feel right now?");
        questions.add("How rejected do you feel right now?");
        questions.add("How lonely do you feel right now?");

        Collections.shuffle(questions);

        options = new ArrayList<>();

        options.add("Alone");
        options.add("Friends");
        options.add("Family");
        options.add("Spouse/Partner");
        options.add("Co-workers");
        options.add("Co-students");



        numOfQuestions = questions.size();
        Log.d(TAG, "onCreate: size of questiosting is: " + numOfQuestions);

        //List<Fragment>
        fragments = getFragments();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        final NonSwipeableViewPager pager = (NonSwipeableViewPager)findViewById(R.id.viewPager);

        pager.setAdapter(pageAdapter);
        pager.setOffscreenPageLimit(numOfQuestions);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //Log.d(TAG, "onPageScrolled: " + i + v + i1);
            }

            @Override
            public void onPageSelected(int i) {
                Log.d(TAG, "onPageSelected: "+ i);
                final int numberOfPages = pager.getAdapter().getCount()-1;
                Log.d(TAG, "onPageSelected: number of pages: " + numberOfPages);
                if(i == numberOfPages){
                    Button buttonNext = (Button)findViewById(R.id.buttonNext);
                    buttonTextView = (TextView)findViewById(R.id.buttonNext);
                    buttonTextView.setText("FINISH");
                    buttonNext.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: last page");
                            Fragment f = fList.get(numberOfPages);
                            if(((MultipleChoiceFragment) f).check()){
                                recordResults();
                                Toast.makeText(getBaseContext(), "Thank you for completing the questionnaire :)", Toast.LENGTH_LONG).show();
                                Intent returnToFinish = new Intent(FireBaseEMA.this, FinishInstallScreen.class);

                                startActivity(returnToFinish);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Please make sure at least one option is selected", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }else{
                    Button buttonNext = (Button)findViewById(R.id.buttonNext);
                    buttonTextView = (TextView)findViewById(R.id.buttonNext);
                    buttonTextView.setText("NEXT");
                    buttonNext.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            pager.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
                            Log.d(TAG, "onClick: position = " + pager.getCurrentItem());
                            Log.d(TAG, "onClick: the number in total is: " + numOfQuestions);
                            Log.d(TAG, "onClick: try another way: " + pager.getAdapter().getCount());
                        }

                        private int getItem(int i) {
                            return pager.getCurrentItem() + i;
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //Log.d(TAG, "onPageScrollStateChanged: " + i);

            }
        });

//
        Button yourButton = (Button)findViewById(R.id.buttonPrev);
        yourButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pager.setCurrentItem(getItem(-1), true); //getItem(-1) for previous
            }

            private int getItem(int i) {
                return pager.getCurrentItem() + i;
            }
        });
        Log.d(TAG, "onCreate: pager.getcurent: " + pager.getCurrentItem());


        Button buttonNext = (Button)findViewById(R.id.buttonNext);
        buttonTextView = (TextView)findViewById(R.id.buttonNext);
        buttonTextView.setText("NEXT");
        buttonNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pager.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
                //Log.d(TAG, "onClick: position = " + pager.getCurrentItem());
                //Log.d(TAG, "onClick: the number in total is: " + numOfQuestions);
                //Log.d(TAG, "onClick: try another way: " + pager.getAdapter().getCount());
            }
            private int getItem(int i) {
                return pager.getCurrentItem() + i;
            }
        });
    }

    public int getValue(int position){
        SeekBarFragment get = (SeekBarFragment) fList.get(position);
        int value = get.getSeekBarValue();
        return value;
    }

//    public void recordResults(){
//
//        timeFinished = System.currentTimeMillis();
//
//        String uri = (getExternalFilesDir(null) + "/EMA/"+ formattedDate + ".txt");
//        File file = new File(uri);
//        Log.d(TAG, "onClick: the file url is : " + uri);
//        long TS = System.currentTimeMillis();
//        String timeStampString = String.valueOf(TS);
//
//        Log.d(TAG, "recordResults: ");
//        JSONObject object = new JSONObject();
//        for(Fragment fragment : fList){
//            try {
//                object.put("Time", timeStampString);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
////            JSONObject object = new JSONObject();
//            if(fragment instanceof SeekBarFragment){
//                Log.d(TAG, "recordResults: the value is: " +((SeekBarFragment)fragment).getSeekBarValue());
//                Log.d(TAG, "recordResults: the string is: " + ((SeekBarFragment)fragment).getSeekBarString());
//                String s = ((SeekBarFragment)fragment).getSeekBarString();
//                int v = ((SeekBarFragment)fragment).getSeekBarValue();
//                try{
//                    object.put(s, v);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }else if(fragment instanceof MultipleChoiceFragment){
//                Log.d(TAG, "recordResults: multi choice frag");
//                Log.d(TAG, "recordResults: get selected: " + ((MultipleChoiceFragment) fragment).getSelection());
//                String s = ((MultipleChoiceFragment) fragment).getSelection();
//                try {
//                    object.put("TimeSpentWith", s);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        Log.d(TAG, "recordResults: " + object);
//        writeToFile(file, object.toString());
//    }

    public void recordResults(){

        timeFinished = System.currentTimeMillis();

        String uri = (getExternalFilesDir(null) + "/EMA/"+ formattedDate + ".txt");
        File file = new File(uri);
        Log.d(TAG, "onClick: the file url is : " + uri);
        long TS = System.currentTimeMillis();
        String timeStampString = String.valueOf(TS);

        Log.d(TAG, "recordResults: ");
        JSONObject object = new JSONObject();
        for(Fragment fragment : fList){
            try {
                object.put("TimeStarted", timeStarted);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            JSONObject object = new JSONObject();
            if(fragment instanceof SeekBarFragment){
                Log.d(TAG, "recordResults: the value is: " +((SeekBarFragment)fragment).getSeekBarValue());
                Log.d(TAG, "recordResults: the string is: " + ((SeekBarFragment)fragment).getSeekBarString());
                String s = ((SeekBarFragment)fragment).getSeekBarString();
                int v = ((SeekBarFragment)fragment).getSeekBarValue();
                try{
                    object.put(s, v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(fragment instanceof MultipleChoiceFragment){
                Log.d(TAG, "recordResults: multi choice frag");
                Log.d(TAG, "recordResults: get selected: " + ((MultipleChoiceFragment) fragment).getSelection());
                String s = ((MultipleChoiceFragment) fragment).getSelection();
                try {
                    object.put("TimeSpentWith", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try{
                object.put("TimeFinished",timeFinished);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "recordResults: " + object);
        Constants.writeHeaderToFile(file, Constants.secureID + "," + Constants.modelName + "," + Constants.modelNumber + ","+ Constants.androidVersion + "," + Constants.earsVersion + "\n");
        writeToFile(file, object.toString());
    }


    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        Log.d(TAG, "onPanelClosed: ");
        super.onPanelClosed(featureId, menu);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        //fragments.get(1).
        super.onPause();
    }

    private List<Fragment> getFragments(){
        fList = new ArrayList<Fragment>();
        Log.d(TAG, "getFragments: 1");

        for(int i=0;i<numOfQuestions;i++){
            fList.add(SeekBarFragment.newInstance(questions.get(i)));
            Log.d(TAG, "getFragments: question is: " + questions.get(i));
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", options);
        MultipleChoiceFragment mcf = new MultipleChoiceFragment();
        mcf.setArguments(bundle);
        fList.add(mcf);
        //fList.add(MultipleChoiceFragment.newInstance(options));
        Log.d(TAG, "getFragments: options size: " + options.size());
        return fList;
    }


    private class MyPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            //Log.d(TAG, "getItem: ");
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            //Log.d(TAG, "getCount: ");
            return this.fragments.size();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        //System.out.println("The state of the media is: " + Environment.getExternalStorageState());
        try {
            stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
        } catch (FileNotFoundException e) {
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
}
