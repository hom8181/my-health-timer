package com.example.myhealth;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class SetAdapter extends BaseAdapter {

    private ArrayList<Set> items = new ArrayList<>();


    public void addItem(Set set) {
        items.add(set);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {


        return null;
    }
}
