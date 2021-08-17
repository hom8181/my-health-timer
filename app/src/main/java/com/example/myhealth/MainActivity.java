package com.example.myhealth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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

    private TextView startText;

    private int exerciseSet = 0;
    private final List<ExerciseSetDto> exerciseSetList = new ArrayList<>();

    private ListView listView;

    private boolean exerciseIng = false;

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

        startText = findViewById(R.id.start_text);

        mExerciseTime = findViewById(R.id.exercise_time);
        mRestTime = findViewById(R.id.rest_time);

        listView = findViewById(R.id.list);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        LinearLayout modeMain = findViewById(R.id.mode_main);
        LinearLayout modeAutoRest = findViewById(R.id.mode_auto_rest);
        LinearLayout modeInterval = findViewById(R.id.mode_interval);

        modeMain.setOnClickListener(this);
        modeAutoRest.setOnClickListener(this);
        modeInterval.setOnClickListener(this);

    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exercise_start_btn:
                exerciseIng = true;

                if (exerciseSet == 0) {
                    restStartButton.setText("1세트 운동 완료 & 휴식 시작");

                    Date date = new Date();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    String nowTime = simpleDateFormat.format(date);
                    startText.setText("운동 시작/진행 시간 : " + nowTime);
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
                exerciseIng = false;

                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.stop();

                mRestTime.setBase(SystemClock.elapsedRealtime());
                mRestTime.stop();

                startText.setText("운동 시작/진행 시간 : ");

                exerciseSet = 0;
                exerciseStartButton.setText("1세트 운동 시작");

                exerciseStartButton.setVisibility(View.VISIBLE);
                restStartButton.setVisibility(View.GONE);

                listView.setAdapter(null);

                exerciseSetList.clear();
                break;

            case R.id.mode_main:
                Toast.makeText(this, "현재 메인모드 입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.mode_auto_rest:
                if (exerciseIng) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("안내");
                    builder.setMessage("운동중 이시라면 운동 기록이 날라가게 됩니다. \n이동하시겠습니까?");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);

                    builder.setPositiveButton("이동", (dialogInterface, i) -> {
                        Intent autoRestIntent = new Intent(MainActivity.this, AutoRestActivity.class);
                        startActivity(autoRestIntent);
                        finish();
                    });

                    builder.setNegativeButton("취소", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Intent autoRestIntent = new Intent(this, AutoRestActivity.class);
                    startActivity(autoRestIntent);
                    finish();
                }
                break;

            case R.id.mode_interval:
                if (exerciseIng) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("안내");
                    builder.setMessage("운동중 이시라면 운동 기록이 날라가게 됩니다. \n이동하시겠습니까?");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);

                    builder.setPositiveButton("이동", (dialogInterface, i) -> {
                        Intent intervalIntent = new Intent(MainActivity.this, IntervalActivity.class);
                        startActivity(intervalIntent);
                        finish();
                    });

                    builder.setNegativeButton("취소", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Intent intervalIntent = new Intent(this, IntervalActivity.class);
                    startActivity(intervalIntent);
                    finish();
                }
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