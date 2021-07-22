package com.example.myhealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhealth.domain.ExerciseSetDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer mExerciseTime, mRestTime;

    private Button exerciseStartButton;
    private Button restStartButton;

    private int exerciseSet = 0;
    private final List<ExerciseSetDto> exerciseSetList = new ArrayList<>();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("세트 타이머");

        exerciseStartButton = findViewById(R.id.exercise_start_btn);
        restStartButton = findViewById(R.id.rest_start_btn);
        Button resetButton = findViewById(R.id.reset_btn);
        Button moveRestActivityButton = findViewById(R.id.move_auto_rest_btn);

        mExerciseTime = findViewById(R.id.exercise_time);
        mRestTime = findViewById(R.id.rest_time);

        listView = findViewById(R.id.list);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        // 자동 휴식 activity로 이동
        moveRestActivityButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AutoRestActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exercise_start_btn:
                if (exerciseSet == 0) {
                    restStartButton.setText("1세트 운동 완료 & 휴식 시작");
                }
                if (exerciseSet > 0) {
                    long restTime = Utils.mathFloorTime(SystemClock.elapsedRealtime() - mRestTime.getBase());
                    long exerTime;

                    exerTime = Utils.mathFloorTime((SystemClock.elapsedRealtime() - mExerciseTime.getBase()));
                    exerTime = exerTime - restTime;

                    Date date = new Date();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    String nowTime = simpleDateFormat.format(date);

                    String startTIme = Utils.viewTime(exerTime);
                    String endTIme = Utils.viewTime(restTime);

                    // 해당 세트 운동 정보 저장
                    ExerciseSetDto exerciseSetDto = new ExerciseSetDto();
                    exerciseSetDto.setSet(exerciseSet);
                    exerciseSetDto.setExerciseTime(startTIme);
                    exerciseSetDto.setRestTime(endTIme);
                    exerciseSetDto.setNowTime(nowTime);
                    exerciseSetList.add(exerciseSetDto);

                    SetAdapter setAdapter = new SetAdapter(this, exerciseSetList);
                    listView.setAdapter(setAdapter);

                    restStartButton.setText((exerciseSet + 1) + "세트 운동 완료 & 휴식 시작");
                }
                exerciseSet++;

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
                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.stop();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.stop();

                exerciseSet = 0;
                exerciseStartButton.setText("1세트 운동 시작");

                exerciseStartButton.setVisibility(View.VISIBLE);
                restStartButton.setVisibility(View.GONE);

                listView.setAdapter(null);

                exerciseSetList.clear();
                break;
        }
    }

    // 앱 종료시 Chronometer stop
    public void onDestroy() {
        super.onDestroy();
        mExerciseTime.stop();
        mRestTime.stop();
    }


}