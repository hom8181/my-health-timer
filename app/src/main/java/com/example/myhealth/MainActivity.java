package com.example.myhealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer mExerciseStart, mRestTime;
    private ExerciseStatus currentExerciseStatus;

    private Button exerciseStartButton;
    private Button restStartButton;
    private Button stopButton;
    private Button stopEndButton;

    private int exerciseSet = 0;
    private TextView textView;

    ArrayList<Set> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exerciseStartButton = findViewById(R.id.exercise_start_btn);
        restStartButton = findViewById(R.id.rest_start_btn);
        stopButton = findViewById(R.id.stop_btn);
        stopEndButton = findViewById(R.id.stop_end_btn);
        Button resetButton = findViewById(R.id.reset_btn);

        mExerciseStart = findViewById(R.id.exercise_time);
        mRestTime = findViewById(R.id.rest_time);

        ScrollView listView = findViewById(R.id.set_list);

        textView = findViewById(R.id.scroll_text);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        stopEndButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exercise_start_btn:
                currentExerciseStatus = ExerciseStatus.EXERCISE;
                if (exerciseSet == 0) {
                    stopButton.setVisibility(View.VISIBLE);
                    restStartButton.setText( "1세트 운동 완료 & 휴식 시작");
                }

                exerciseSet++;

                if (exerciseSet > 1) {
                    String record = "";

                    long restTime = (SystemClock.elapsedRealtime() - mRestTime.getBase()) / 1000;
                    long exerTime = ((SystemClock.elapsedRealtime() - mExerciseStart.getBase()) / 1000) - restTime;

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

                mExerciseStart.setBase(SystemClock.elapsedRealtime());
                mExerciseStart.start();

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

                mExerciseStart.stop();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.start();

                restStartButton.setVisibility(View.GONE);
                exerciseStartButton.setVisibility(View.VISIBLE);
                break;

            case R.id.stop_btn:
                mExerciseStart.stop();
                mRestTime.stop();
                stopButton.setVisibility(View.GONE);
                stopEndButton.setVisibility(View.VISIBLE);
                break;

            case R.id.stop_end_btn:
                if (currentExerciseStatus == ExerciseStatus.EXERCISE) {
                    mExerciseStart.start();
                }
                else {
                    mRestTime.start();
                }
                stopButton.setVisibility(View.VISIBLE);
                stopEndButton.setVisibility(View.GONE);
                break;

            case R.id.reset_btn:
                mExerciseStart.setBase(SystemClock.elapsedRealtime());
                mExerciseStart.stop();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.stop();

                exerciseSet = 0;
                exerciseStartButton.setText("1세트 운동 시작");

                restStartButton.setVisibility(View.GONE);
                exerciseStartButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                stopEndButton.setVisibility(View.GONE);
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

    public Context getContext() {
        return this;
    }

    public void onDestroy() {
        super.onDestroy();
        mExerciseStart.stop();
        mRestTime.stop();
    }


}