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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer mExerciseTime, mRestTime, mStopTime;
    private ExerciseStatus currentExerciseStatus;

    private Button exerciseStartButton;
    private Button restStartButton;
    private Button stopButton;
    private Button stopEndButton;

    private int exerciseSet = 0;
    private TextView textView;
    private TextView stopMessage;

    private long exerciseStopTime;
    private long restStopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.activity_main);

        exerciseStartButton = findViewById(R.id.exercise_start_btn);
        restStartButton = findViewById(R.id.rest_start_btn);
        stopButton = findViewById(R.id.stop_btn);
        stopEndButton = findViewById(R.id.stop_end_btn);
        Button resetButton = findViewById(R.id.reset_btn);

        mExerciseTime = findViewById(R.id.exercise_time);
        mRestTime = findViewById(R.id.rest_time);
        mStopTime = findViewById(R.id.calculate_stop_time);

        textView = findViewById(R.id.scroll_text);
        stopMessage = findViewById(R.id.stop_message);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        stopEndButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        StopType stopType = StopType.NORMAL;
        switch (view.getId()) {
            case R.id.exercise_start_btn:
                currentExerciseStatus = ExerciseStatus.EXERCISE;
                if (exerciseSet == 0) {
                    stopButton.setVisibility(View.VISIBLE);
                    restStartButton.setText("1세트 운동 완료 & 휴식 시작");
                }

                exerciseSet++;

                if (exerciseSet > 1) {
                    String record = "";

                    long restTime = (SystemClock.elapsedRealtime() - mRestTime.getBase()) / 1000;

                    long exerTime;

                    if (restStopTime > 0) {
                        System.out.println("운동시간 - 쉬는 시간" + (((SystemClock.elapsedRealtime() - mExerciseTime.getBase()) / 1000) - restTime));
                        System.out.println("운동 스탑 시간" + exerciseStopTime / 1000);
                        System.out.println("휴식 스탑 시간" + restStopTime / 1000);
                        exerTime = ((SystemClock.elapsedRealtime() - mExerciseTime.getBase()) / 1000) - restTime - (restStopTime / 1000);
                    } else {
                        exerTime = ((SystemClock.elapsedRealtime() - mExerciseTime.getBase()) / 1000) - restTime;
                    }


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
                currentExerciseStatus = ExerciseStatus.REST;

                if (exerciseSet > 0) {
                    exerciseStartButton.setText("휴식 종료  " + (exerciseSet + 1) + "세트 운동 시작");
                }

                mExerciseTime.stop();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.start();

                restStartButton.setVisibility(View.GONE);
                exerciseStartButton.setVisibility(View.VISIBLE);
                break;

            case R.id.stop_btn:
                long stopTime = 0;

                mStopTime.setBase(SystemClock.elapsedRealtime());
                mStopTime.start();

                stopMessage.setVisibility(View.VISIBLE);

                mExerciseTime.stop();
                mRestTime.stop();
                stopButton.setVisibility(View.GONE);
                stopEndButton.setVisibility(View.VISIBLE);

                if (currentExerciseStatus == ExerciseStatus.EXERCISE) {
                    restStartButton.setVisibility(View.GONE);
                } else {
                    exerciseStartButton.setVisibility(View.GONE);
                }
                break;

            case R.id.stop_end_btn:
                stopTime = SystemClock.elapsedRealtime() - mStopTime.getBase();

                if (currentExerciseStatus == ExerciseStatus.EXERCISE) {
                    exerciseStopTime = stopTime;
                    stopType = StopType.EXERCISE;

                    long exerTime = ((SystemClock.elapsedRealtime() - stopTime) - mExerciseTime.getBase()) / 1000;
                    mExerciseTime.setBase(SystemClock.elapsedRealtime() - (exerTime * 1000));

                    mExerciseTime.start();
                    restStartButton.setVisibility(View.VISIBLE);
                } else {
                    restStopTime = stopTime;
                    stopType = StopType.REST;

                    long restTime = ((SystemClock.elapsedRealtime() - stopTime) - mRestTime.getBase()) / 1000;
                    mRestTime.setBase(SystemClock.elapsedRealtime() - (restTime * 1000));

                    mRestTime.start();
                    exerciseStartButton.setVisibility(View.VISIBLE);
                }
                stopButton.setVisibility(View.VISIBLE);
                stopEndButton.setVisibility(View.GONE);
                stopMessage.setVisibility(View.GONE);
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
                stopButton.setVisibility(View.GONE);
                stopEndButton.setVisibility(View.GONE);
                stopMessage.setVisibility(View.GONE);
                textView.setText("");
                break;
        }
    }

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

    public void onDestroy() {
        super.onDestroy();
        mExerciseTime.stop();
        mRestTime.stop();
    }


}