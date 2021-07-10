package com.example.myhealth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AutoRestActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer mExerciseTime;

    private Button exerciseStartButton;
    private Button restStartButton;
    private Button restIngButton;

    private int exerciseSet = 0;

    private TextView textView;
    private TextView stopMessage;

    private TextView mRestTime;

    private int minutePick = 0;
    private int secondPick = 5;

    private int restMillisecond = (minutePick * 60 + secondPick) * 1000;

    private Toast originalToast;
    private Toast newToast;

    private boolean firstToast = true;

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
        Button restTimeSettingButton = findViewById(R.id.rest_time_setting_btn);
        Button resetButton = findViewById(R.id.reset_btn);

        mExerciseTime = findViewById(R.id.exercise_time);

        textView = findViewById(R.id.scroll_text);
        mRestTime = findViewById(R.id.rest_time);
        stopMessage = findViewById(R.id.stop_message);

        exerciseStartButton.setOnClickListener(this);
        restStartButton.setOnClickListener(this);
        restTimeSettingButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        mRestTime.setText(viewTime(restMillisecond / 1000));

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

                exerciseSet++;
                break;
            case R.id.rest_start_btn:
                mExerciseTime.stop();

                restStartButton.setVisibility(View.GONE);
                restIngButton.setVisibility(View.VISIBLE);

                firstToast = true;

                new CountDownTimer(restMillisecond, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisecond) {
                        long second = millisecond / 1000;
                        mRestTime.setText(viewTime(second));

                        // 초 후 운동이 시작 됩니다 && 부저
                        if (millisecond / 1000 <= 3 && millisecond / 1000 != 0) {
                            System.out.println(firstToast);

                            if (firstToast) {
                                // 처음 띄우는 toast 일 경우 cancel이 없기 때문에 처음 toast를 띄우기만 함
                                originalToast = Toast.makeText(AutoRestActivity.this, millisecond / 1000 +"초 후 운동이 시작됩니다.", Toast.LENGTH_SHORT);
                                originalToast.show();
                            } else {
                                // 원래의 toast를 cancel하고 새로 toast를 띄움
                                originalToast.cancel();
                                newToast = Toast.makeText(AutoRestActivity.this, millisecond / 1000 +"초 후 운동이 시작됩니다.", Toast.LENGTH_SHORT);
                                newToast.show();

                                originalToast = newToast;
                            }
                            firstToast = false;
                        }
                    }

                    @Override
                    public void onFinish() {
                        // 자동으로 exercise 시작
                        originalToast.cancel();
                        newToast = Toast.makeText(AutoRestActivity.this, "지금 바로 운동을 시작해주세요.", Toast.LENGTH_SHORT);
                        newToast.show();

                    }
                }.start();

                break;
            case R.id.rest_time_setting_btn:
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

                break;
            case R.id.reset_btn:
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