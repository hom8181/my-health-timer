package com.example.myhealth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoRestActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer mExerciseTime, mRestTime, mStopTime;

    private Button exerciseStartButton;
    private Button restStartButton;

    private int exerciseSet = 0;
    private TextView textView;
    private TextView stopMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.auto_rest_activity);

        exerciseStartButton = findViewById(R.id.exercise_start_btn);
        restStartButton = findViewById(R.id.rest_start_btn);
        Button resetButton = findViewById(R.id.reset_btn);

        mExerciseTime = findViewById(R.id.exercise_time);
        mRestTime = findViewById(R.id.rest_time);
        mStopTime = findViewById(R.id.calculate_stop_time);

        textView = findViewById(R.id.scroll_text);
        stopMessage = findViewById(R.id.stop_message);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exercise_start_btn:
                if (exerciseSet == 0) {
                    restStartButton.setText("1세트 운동 완료 & 휴식 시작");
                }

                exerciseSet++;

                if (exerciseSet > 1) {
                    String record = "";

                    long restTime = mathFloorTime(SystemClock.elapsedRealtime() - mRestTime.getBase());
                    long exerTime;

                    exerTime = mathFloorTime((SystemClock.elapsedRealtime() - mExerciseTime.getBase()));
                    exerTime = exerTime - restTime;

                    Date date = new Date();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    String nowTime = simpleDateFormat.format(date);

                    String startTIme = viewTime(exerTime);
                    String endTIme = viewTime(restTime);

                    record += "             " + (exerciseSet - 1) + "                        "
                            + startTIme + "                       "
                            + endTIme + "                        " + nowTime + "\n";
                    textView.setText(textView.getText() + "\n" + record);
                    textView.setMovementMethod(new ScrollingMovementMethod());

                    restStartButton.setText(exerciseSet + "세트 운동 완료 & 휴식 시작");
                }

                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.start();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.stop();

                exerciseStartButton.setVisibility(View.GONE);
                restStartButton.setVisibility(View.VISIBLE);
                break;
            case R.id.rest_start_btn:
                if (exerciseSet > 0) {
                    exerciseStartButton.setText("휴식 종료  " + (exerciseSet + 1) + "세트 운동 시작");
                }

                mExerciseTime.stop();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.start();

                restStartButton.setVisibility(View.GONE);
                exerciseStartButton.setVisibility(View.VISIBLE);
                break;

            case R.id.reset_btn:
                mStopTime.setBase(SystemClock.elapsedRealtime());
                mStopTime.stop();

                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.stop();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.stop();

                exerciseSet = 0;
                exerciseStartButton.setText("1세트 운동 시작");

                exerciseStartButton.setVisibility(View.VISIBLE);
                restStartButton.setVisibility(View.GONE);
                stopMessage.setVisibility(View.GONE);
                textView.setText("");
                break;
        }
    }

    /**
     * 측정시간을 HH:mm 형태의 String으로 나타내 주는 method
     *
     * @param time 측정시간
     * @return String
     */
    private String viewTime(long time) {
        String record = "";
        long minute = time / 60;
        long second = time % 60;

        if (minute >= 10 && second >= 10) {
            record = minute + ":" + second;
        }
        if (minute < 10 && second >= 10) {
            record = "0" + minute + ":" + second;
        }
        if (minute >= 10 && second < 10) {
            record = minute + ":" + "0" + second;
        }
        if (minute < 10 && second < 10) {
            record = "0" + minute + ":" + "0" + second;
        }
        return record;
    }


    /**
     * 측정시간을 내림 후 second로 바꾸어주는 method
     *
     * @param time 측정시간
     * @return long
     */
    private long mathFloorTime(long time) {
        final double millisecondToSecond = 1000.0;
        return (long) Math.floor(time / millisecondToSecond);
    }

    // 앱 종료시 Chronometer stop
    public void onDestroy() {
        super.onDestroy();
        mExerciseTime.stop();
        mRestTime.stop();
    }


}