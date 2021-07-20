package com.example.myhealth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhealth.domain.ExerciseSetDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoRestActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer mExerciseTime;

    private Button exerciseStartButton;
    private Button restStartButton;
    private Button restIngButton;
    Button restTimeSettingButton;

    private int exerciseSet = 0;

    private TextView mRestTime;

    private int minutePick = 0;
    private int secondPick = 5;

    private long restMillisecond = (minutePick * 60 + secondPick) * 1000;

    private Toast originalToast;
    private Toast newToast;

    private CountDownTimer countDownTimer;

    private boolean firstToast = true;
    private boolean setTimeAble = true;

    private final List<ExerciseSetDto> exerciseSetList = new ArrayList<>();

    private ListView listView;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.auto_rest_activity);

        exerciseStartButton = findViewById(R.id.exercise_start_btn);
        restStartButton = findViewById(R.id.rest_start_btn);
        restIngButton = findViewById(R.id.rest_ing_btn);
        restTimeSettingButton = findViewById(R.id.rest_time_setting_btn);
        Button resetButton = findViewById(R.id.reset_btn);

        mExerciseTime = findViewById(R.id.exercise_time);

        mRestTime = findViewById(R.id.rest_time);

        listView = findViewById(R.id.list);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        restTimeSettingButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        mRestTime.setText(Utils.viewTime(restMillisecond / 1000));

        mRestTime.setOnClickListener(view -> {
            if (exerciseSet == 0 && setTimeAble) {
                setRestTime();
            }
        });

    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exercise_start_btn:
                setTimeAble = false;

                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.start();

                exerciseStartButton.setVisibility(View.GONE);
                restIngButton.setVisibility(View.GONE);
                restTimeSettingButton.setVisibility(View.GONE);
                restStartButton.setVisibility(View.VISIBLE);

                break;
            case R.id.rest_start_btn:
                mExerciseTime.stop();

                restStartButton.setVisibility(View.GONE);
                restIngButton.setVisibility(View.VISIBLE);

                firstToast = true;

                countDownTimer = new CountDownTimer(restMillisecond, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisecond) {
                        long second = millisecond / 1000;
                        mRestTime.setText(Utils.viewTime(second));

                        // 초 후 운동이 시작 됩니다 && 부저
                        if (millisecond / 1000 <= 3 && millisecond / 1000 != 0) {
                            if (firstToast) {
                                // 처음 띄우는 toast 일 경우 cancel이 없기 때문에 처음 toast를 띄우기만 함
                                originalToast = Toast.makeText(AutoRestActivity.this, millisecond / 1000 + "초 후 운동이 시작됩니다.", Toast.LENGTH_SHORT);
                                originalToast.show();
                            } else {


                                // 원래의 toast를 cancel하고 새로 toast를 띄움
                                originalToast.cancel();
                                newToast = Toast.makeText(AutoRestActivity.this, millisecond / 1000 + "초 후 운동이 시작됩니다.", Toast.LENGTH_SHORT);
                                newToast.show();

                                originalToast = newToast;
                            }
                            firstToast = false;
                        }
                    }

                    @Override
                    public void onFinish() {
                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                        ringtone.play();

                        // 자동으로 exercise 시작
                        originalToast.cancel();
                        newToast = Toast.makeText(AutoRestActivity.this, "지금 바로 운동을 시작해주세요.", Toast.LENGTH_SHORT);
                        newToast.show();

                        exerciseAutoStart();
                    }
                }.start();

                break;
            case R.id.rest_time_setting_btn:
                setRestTime();

                break;
            case R.id.reset_btn:
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                mExerciseTime.setBase(SystemClock.elapsedRealtime());
                mExerciseTime.stop();

                exerciseSet = 0;
                setTimeAble = true;
                exerciseStartButton.setText("1세트 운동 시작");
                restStartButton.setText("1세트 운동 완료 & 자동 휴식 시작");

                mRestTime.setText(Utils.viewTime(restMillisecond / 1000));

                exerciseStartButton.setVisibility(View.VISIBLE);
                restTimeSettingButton.setVisibility(View.VISIBLE);
                restStartButton.setVisibility(View.GONE);
                restIngButton.setVisibility(View.GONE);

                listView.setAdapter(null);

                exerciseSetList.clear();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void exerciseAutoStart() {
        exerciseSet++;

        long exerTime;

        exerTime = Utils.mathFloorTime((SystemClock.elapsedRealtime() - mExerciseTime.getBase() - restMillisecond));

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String nowTime = simpleDateFormat.format(date);

        String startTIme = Utils.viewTime(exerTime);
        String endTIme = Utils.viewTime(restMillisecond / 1000);

        // 해당 세트 운동 정보 저장
        ExerciseSetDto exerciseSetDto = new ExerciseSetDto();
        exerciseSetDto.setSet(exerciseSet);
        exerciseSetDto.setExerciseTime(startTIme);
        exerciseSetDto.setRestTime(endTIme);
        exerciseSetDto.setNowTime(nowTime);
        exerciseSetList.add(exerciseSetDto);

        SetAdapter setAdapter = new SetAdapter(this, exerciseSetList);
        listView.setAdapter(setAdapter);

        restStartButton.setText((exerciseSet + 1) + "세트 운동 완료 & 자동 휴식 시작");

        mExerciseTime.setBase(SystemClock.elapsedRealtime());
        mExerciseTime.start();

        exerciseStartButton.setVisibility(View.GONE);
        restIngButton.setVisibility(View.GONE);
        restStartButton.setVisibility(View.VISIBLE);

        mRestTime.setText(Utils.viewTime(restMillisecond / 1000));
    }

    private void setRestTime() {
        Dialog numberPickerDialog = new Dialog(this);
        numberPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        numberPickerDialog.setContentView(R.layout.dialog_timepicker);

        Button selectButton = numberPickerDialog.findViewById(R.id.select_btn);
        Button cancelButton = numberPickerDialog.findViewById(R.id.cancel_btn);

        NumberPicker minutePicker = numberPickerDialog.findViewById(R.id.minute_picker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setWrapSelectorWheel(false);
        minutePicker.setValue(minutePick);
        minutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
        });

        NumberPicker secondPicker = numberPickerDialog.findViewById(R.id.second_picker);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        secondPicker.setWrapSelectorWheel(false);
        secondPicker.setValue(secondPick);
        secondPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
        });

        numberPickerDialog.show();

        selectButton.setOnClickListener(v -> {
            int minute = minutePicker.getValue();
            int second = secondPicker.getValue();

            minutePick = minute;
            secondPick = second;

            int totalSecond = minute * 60 + second;
            restMillisecond = totalSecond * 1000;
            String viewTime = Utils.viewTime(totalSecond);

            TextView textView = findViewById(R.id.rest_time);
            textView.setText(viewTime);

            numberPickerDialog.dismiss();
        });
        cancelButton.setOnClickListener(v -> numberPickerDialog.dismiss());
    }


    // 앱 종료시 Chronometer stop
    public void onDestroy() {
        super.onDestroy();
        mExerciseTime.stop();
    }


}