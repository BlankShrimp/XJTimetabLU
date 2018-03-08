package com.blankshrimp.xjtimetablu.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blankshrimp.xjtimetablu.R;

import java.util.List;

/**
 * Created by Flabbergast13 on 18/2/25.
 */

public class PeopleAdapter extends ArrayAdapter<People> {

    private int resourceId;

    public PeopleAdapter(Context context, int textViewResourceId, List<People> obj ) {
        super(context, textViewResourceId, obj);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        People people = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView nameText = (TextView) view.findViewById(R.id.person_name);
        TextView remarkText = (TextView) view.findViewById(R.id.person_remark);
        nameText.setText(people.getName());
        remarkText.setText(people.getRemark());
        return view;
    }
}