package com.example.myhealth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SetListItem extends LinearLayout {


    private Set mCarpool;
    private View view;


    public SetListItem(Context context, ViewGroup root, Set carpool) {
        super(context);

        this.mCarpool = carpool;

        detailInfoInit(context, root, carpool);

    }

    private void detailInfoInit(Context context, ViewGroup root, Set carpool) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.set_item, root, true);

        TextView set = findViewById(R.id.item_set);
        TextView exerciseTime = findViewById(R.id.item_exercise_time);
        TextView restTime = findViewById(R.id.item_rest_time);

        set.setText(carpool.getSet());
        set.setText(carpool.getSet());
        set.setText(carpool.getSet());
    }

}
