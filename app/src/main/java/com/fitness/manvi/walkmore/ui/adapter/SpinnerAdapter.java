package com.fitness.manvi.walkmore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by manvi on 5/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SpinnerAdapter extends ArrayAdapter<String> {


    public SpinnerAdapter(@NonNull Context context, @NonNull String[] objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        return view;
    }
}
