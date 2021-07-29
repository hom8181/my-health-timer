package com.example.myhealth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

public class IntervalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button exerciseStartButton;
    private Button exerciseIngButton;
    private Button restIngButton;
    Button restTimeSettingButton;

    private int exerciseSet = 0;

    private TextView mExerciseTime;
    private TextView mRestTime;

    private int restMinutePick = 0;
    private int restSecondPick = 3;

    private int exerciseMinutePick = 0;
    private int exerciseSecondPick = 4;

    private long restMillisecond = (restMinutePick * 60 + restSecondPick) * 1000;
    private long exerciseMillisecond = (exerciseMinutePick * 60 + exerciseSecondPick) * 1000;

    private Toast originalToast;
    private Toast newToast;

    private CountDownTimer restCountDownTimer;
    private CountDownTimer exerciseCountDownTimer;

    private boolean firstToast = true;
    private boolean setTimeAble = true;

    private final List<ExerciseSetDto> exerciseSetList = new ArrayList<>();

    private ListView listView;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interval_activity);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("인터벌 트레이닝");
        actionBar.setDisplayHomeAsUpEnabled(true);


        exerciseStartButton = findViewById(R.id.exercise_start_btn);
        exerciseIngButton = findViewById(R.id.exercise_ing_btn);

        restIngButton = findViewById(R.id.rest_ing_btn);
        restTimeSettingButton = findViewById(R.id.rest_time_setting_btn);
        Button resetButton = findViewById(R.id.reset_btn);

        mExerciseTime = findViewById(R.id.exercise_time);

        mRestTime = findViewById(R.id.rest_time);

        listView = findViewById(R.id.list);

        exerciseStartButton.setOnClickListener(this);
        restTimeSettingButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        mExerciseTime.setText(Utils.viewTime(exerciseMillisecond / 1000));
        mExerciseTime.setOnClickListener(view -> {
            if (exerciseSet == 0 && setTimeAble) {
                setExerciseTime();
            }
        });

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

                exerciseStartButton.setVisibility(View.GONE);
                restIngButton.setVisibility(View.GONE);
                restTimeSettingButton.setVisibility(View.GONE);
                exerciseIngButton.setVisibility(View.VISIBLE);

                firstToast = true;

                exerciseCountDownTimer = new CountDownTimer(exerciseMillisecond, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisecond) {
                        long second = millisecond / 1000;
                        mExerciseTime.setText(Utils.viewTime(second));

                        // 초 후 운동이 시작 됩니다 && 부저
                        if (millisecond / 1000 <= 3 && millisecond / 1000 != 0) {
                            if (firstToast) {
                                // 처음 띄우는 toast 일 경우 cancel이 없기 때문에 처음 toast를 띄우기만 함
                                originalToast = Toast.makeText(IntervalActivity.this, millisecond / 1000 + "초 후 휴식이 시작됩니다.", Toast.LENGTH_SHORT);
                                originalToast.show();
                            } else {

                                // 원래의 toast를 cancel하고 새로 toast를 띄움
                                originalToast.cancel();
                                newToast = Toast.makeText(IntervalActivity.this, millisecond / 1000 + "초 후 휴식이 시작됩니다.", Toast.LENGTH_SHORT);
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
                        newToast = Toast.makeText(IntervalActivity.this, "휴식 시간입니다.", Toast.LENGTH_SHORT);
                        newToast.show();

                        restStart();
                    }
                }.start();

                break;
            case R.id.rest_time_setting_btn:
                setRestTime();

                break;
            case R.id.reset_btn:
                if (exerciseCountDownTimer != null) {
                    exerciseCountDownTimer.cancel();
                }

                if (restCountDownTimer != null) {
                    restCountDownTimer.cancel();
                }

                exerciseSet = 0;
                setTimeAble = true;
                exerciseStartButton.setText("인터벌 운동 시작");
                exerciseIngButton.setText("1세트 운동 완료 & 자동 휴식 시작");

                mExerciseTime.setText(Utils.viewTime(exerciseMillisecond / 1000));
                mRestTime.setText(Utils.viewTime(restMillisecond / 1000));

                exerciseStartButton.setVisibility(View.VISIBLE);
                restTimeSettingButton.setVisibility(View.VISIBLE);
                exerciseIngButton.setVisibility(View.GONE);
                restIngButton.setVisibility(View.GONE);

                listView.setAdapter(null);

                exerciseSetList.clear();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void restStart() {
        exerciseIngButton.setText((exerciseSet + 1) + "세트 운동 완료 & 휴식 중..");

        exerciseStartButton.setVisibility(View.GONE);
        restIngButton.setVisibility(View.VISIBLE);
        exerciseIngButton.setVisibility(View.GONE);

        mExerciseTime.setText(Utils.viewTime(restMillisecond / 1000));

        exerciseIngButton.setVisibility(View.VISIBLE);
        restIngButton.setVisibility(View.GONE);

        firstToast = true;

        restCountDownTimer = new CountDownTimer(restMillisecond, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisecond) {
                long second = millisecond / 1000;
                mRestTime.setText(Utils.viewTime(second));

                // 초 후 운동이 시작 됩니다 && 부저
                if (millisecond / 1000 <= 3 && millisecond / 1000 != 0) {
                    if (firstToast) {
                        // 처음 띄우는 toast 일 경우 cancel이 없기 때문에 처음 toast를 띄우기만 함
                        originalToast = Toast.makeText(IntervalActivity.this, millisecond / 1000 + "초 후 운동이 시작됩니다.", Toast.LENGTH_SHORT);
                        originalToast.show();
                    } else {


                        // 원래의 toast를 cancel하고 새로 toast를 띄움
                        originalToast.cancel();
                        newToast = Toast.makeText(IntervalActivity.this, millisecond / 1000 + "초 후 운동이 시작됩니다.", Toast.LENGTH_SHORT);
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
                newToast = Toast.makeText(IntervalActivity.this, "지금 바로 운동을 시작해주세요.", Toast.LENGTH_SHORT);
                newToast.show();

                exerciseAutoStart();
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void exerciseAutoStart() {
        exerciseSet++;

        exerciseIngButton.setText((exerciseSet + 1) + "세트 운동 중..");

        exerciseStartButton.setVisibility(View.GONE);
        restIngButton.setVisibility(View.GONE);
        restTimeSettingButton.setVisibility(View.GONE);
        exerciseIngButton.setVisibility(View.VISIBLE);

        firstToast = true;

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String nowTime = simpleDateFormat.format(date);

        String startTIme = Utils.viewTime(exerciseMillisecond / 1000);
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

        exerciseCountDownTimer = new CountDownTimer(exerciseMillisecond, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisecond) {
                long second = millisecond / 1000;
                mExerciseTime.setText(Utils.viewTime(second));

                // 초 후 운동이 시작 됩니다 && 부저
                if (millisecond / 1000 <= 3 && millisecond / 1000 != 0) {
                    if (firstToast) {
                        // 처음 띄우는 toast 일 경우 cancel이 없기 때문에 처음 toast를 띄우기만 함
                        originalToast = Toast.makeText(IntervalActivity.this, millisecond / 1000 + "초 후 휴식이 시작됩니다.", Toast.LENGTH_SHORT);
                        originalToast.show();
                    } else {

                        // 원래의 toast를 cancel하고 새로 toast를 띄움
                        originalToast.cancel();
                        newToast = Toast.makeText(IntervalActivity.this, millisecond / 1000 + "초 후 휴식이 시작됩니다.", Toast.LENGTH_SHORT);
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
                newToast = Toast.makeText(IntervalActivity.this, "휴식 시간입니다.", Toast.LENGTH_SHORT);
                newToast.show();

                restStart();
            }
        }.start();
    }

    private void setExerciseTime() {
        Dialog numberPickerDialog = new Dialog(this);
        numberPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        numberPickerDialog.setContentView(R.layout.dialog_timepicker);

        Button selectButton = numberPickerDialog.findViewById(R.id.select_btn);
        Button cancelButton = numberPickerDialog.findViewById(R.id.cancel_btn);

        NumberPicker exerciseMinutePicker = numberPickerDialog.findViewById(R.id.minute_picker);
        exerciseMinutePicker.setMinValue(0);
        exerciseMinutePicker.setMaxValue(59);
        exerciseMinutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        exerciseMinutePicker.setWrapSelectorWheel(false);
        exerciseMinutePicker.setValue(exerciseMinutePick);
        exerciseMinutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
        });

        NumberPicker exerciseSecondPicker = numberPickerDialog.findViewById(R.id.second_picker);
        exerciseSecondPicker.setMinValue(0);
        exerciseSecondPicker.setMaxValue(59);
        exerciseSecondPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        exerciseSecondPicker.setWrapSelectorWheel(false);
        exerciseSecondPicker.setValue(exerciseSecondPick);
        exerciseSecondPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
        });

        numberPickerDialog.show();

        selectButton.setOnClickListener(v -> {
            int minute = exerciseMinutePicker.getValue();
            int second = exerciseSecondPicker.getValue();

            exerciseMinutePick = minute;
            exerciseSecondPick = second;

            int totalSecond = minute * 60 + second;
            exerciseMillisecond = totalSecond * 1000;
            String viewTime = Utils.viewTime(totalSecond);

            TextView textView = findViewById(R.id.exercise_time);
            textView.setText(viewTime);

            numberPickerDialog.dismiss();
        });
        cancelButton.setOnClickListener(v -> numberPickerDialog.dismiss());
    }

    private void setRestTime() {
        Dialog numberPickerDialog = new Dialog(this);
        numberPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        numberPickerDialog.setContentView(R.layout.dialog_timepicker);

        Button selectButton = numberPickerDialog.findViewById(R.id.select_btn);
        Button cancelButton = numberPickerDialog.findViewById(R.id.cancel_btn);

        NumberPicker restMinutePicker = numberPickerDialog.findViewById(R.id.minute_picker);
        restMinutePicker.setMinValue(0);
        restMinutePicker.setMaxValue(59);
        restMinutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        restMinutePicker.setWrapSelectorWheel(false);
        restMinutePicker.setValue(restMinutePick);
        restMinutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
        });

        NumberPicker restSecondPicker = numberPickerDialog.findViewById(R.id.second_picker);
        restSecondPicker.setMinValue(0);
        restSecondPicker.setMaxValue(59);
        restSecondPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        restSecondPicker.setWrapSelectorWheel(false);
        restSecondPicker.setValue(restSecondPick);
        restSecondPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
        });

        numberPickerDialog.show();

        selectButton.setOnClickListener(v -> {
            int minute = restMinutePicker.getValue();
            int second = restSecondPicker.getValue();

            restMinutePick = minute;
            restSecondPick = second;

            int totalSecond = minute * 60 + second;
            restMillisecond = totalSecond * 1000;
            String viewTime = Utils.viewTime(totalSecond);

            TextView textView = findViewById(R.id.rest_time);
            textView.setText(viewTime);

            numberPickerDialog.dismiss();
        });
        cancelButton.setOnClickListener(v -> numberPickerDialog.dismiss());
    }

}