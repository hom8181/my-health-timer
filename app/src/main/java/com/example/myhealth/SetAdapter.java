package com.example.myhealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myhealth.domain.ExerciseSetDto;

import java.util.List;

public class SetAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<ExerciseSetDto> setList;

    public SetAdapter(Context context, List<ExerciseSetDto> exerciseSetList) {
        mContext = context;
        setList = exerciseSetList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return setList.size();
    }

    @Override
    public Object getItem(int i) {
        return setList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View viewAdapter = mLayoutInflater.inflate(R.layout.set_item, null);

        TextView itemSetText = viewAdapter.findViewById(R.id.item_set);
        TextView exerciseTimeText = viewAdapter.findViewById(R.id.item_exercise_time);
        TextView restTimeText = viewAdapter.findViewById(R.id.item_rest_time);
        TextView nowTimeText = viewAdapter.findViewById(R.id.item_now_time);

        ExerciseSetDto exerciseSetDto = setList.get(position);

        itemSetText.setText(Integer.toString(exerciseSetDto.getSet()));
        exerciseTimeText.setText(exerciseSetDto.getExerciseTime());
        restTimeText.setText(exerciseSetDto.getRestTime());
        nowTimeText.setText(exerciseSetDto.getNowTime());

        return viewAdapter;

    }
}
