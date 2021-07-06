package com.example.myhealth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AutoRestActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer mExerciseTime, mStopTime;

    private Button exerciseStartButton;
    private Button restStartButton;

    private int exerciseSet = 0;
    private TextView textView;
    private TextView stopMessage;

    private TextView mRestTime;

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
        mStopTime = findViewById(R.id.calculate_stop_time);

        textView = findViewById(R.id.scroll_text);
        mRestTime = findViewById(R.id.rest_time);
        stopMessage = findViewById(R.id.stop_message);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        new CountDownTimer(20000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisecond) {
                long second = millisecond / 1000;
                mRestTime.setText(viewTime(second));

                // 초 후 운동이 시작 됩니다 && 부저
                if (millisecond / 1000 <= 3) {

                }
            }

            @Override
            public void onFinish() {
                // 자동으로 exercise 시작

            }
        }.start();

    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exercise_start_btn:
                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.start();

                exerciseStartButton.setVisibility(View.GONE);
                restStartButton.setVisibility(View.VISIBLE);
                break;
            case R.id.rest_start_btn:

                break;

            case R.id.reset_btn:
                mStopTime.setBase(SystemClock.elapsedRealtime());
                mStopTime.stop();

                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.stop();

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
     * @param seconds 측정시간
     * @return String
     */
    private String viewTime(long seconds) {
        String record = "";
        long minute = seconds / 60;
        long second = seconds % 60;

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
    }


}