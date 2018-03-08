package com.blankshrimp.xjtimetablu.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankshrimp.xjtimetablu.R;
import com.blankshrimp.xjtimetablu.util.DatabaseOperater;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Flabbergast13 on 18/2/25.
 */

public class TableFragment extends Fragment {
    private SyncedHorizontalScrollView titleView;
    private SyncedHorizontalScrollView contentView;
    private FrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.table_fragment, container, false);

        titleView = (SyncedHorizontalScrollView) view.findViewById(R.id.titleScrollView);
        contentView = (SyncedHorizontalScrollView) view.findViewById(R.id.contentScrollView);
        titleView.setScrollView(contentView);
        contentView.setScrollView(titleView);
        frameLayout = (FrameLayout) view.findViewById(R.id.classContainer);

        Bundle bundle = getArguments();
        String name = new String();
        if (bundle != null) {
            name = bundle.getString("name");
        }
        DatabaseOperater databaseOperater = new DatabaseOperater();
            tintWeekAndTime(view);
            displayData(databaseOperater.getDataForWeekly(name, view.getContext()), view);
        return view;
    }

    private void displayData(List<List<Map<String, String>>> list, View view) {
        int classNumber = 0;
        for (int wk = 0; wk < list.size(); wk++) {
            for (int clas = 0; clas < list.get(wk).size(); clas++) {
                Map<String, String> map = list.get(wk).get(clas);
                if (weekCheck(map)) {
                    final String code = map.get("code").toString();
                    String type = map.get("type").toString();
                    String location = getLocation(map.get("location").toString());

                    SpannableStringBuilder sb1 = new SpannableStringBuilder(code);
                    SpannableStringBuilder sb2 = new SpannableStringBuilder(type);
                    sb2.append("\n" + location);
                    sb1.append("\n" + sb2);

                    ForegroundColorSpan codeColor = new ForegroundColorSpan(ContextCompat.getColor(this.getContext(), R.color.white));
                    StyleSpan codeStyle1 = new StyleSpan(android.graphics.Typeface.BOLD);
                    StyleSpan codeStyle2 = new StyleSpan(Typeface.ITALIC);
                    AbsoluteSizeSpan codeSize = new AbsoluteSizeSpan(40);
                    sb1.setSpan(codeColor, 0, code.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    sb1.setSpan(codeStyle1, 0, code.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    sb1.setSpan(codeStyle2, 0, code.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    sb1.setSpan(codeColor, 0, code.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    sb1.setSpan(codeSize, 0, code.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    ForegroundColorSpan restColor = new ForegroundColorSpan(ContextCompat.getColor(this.getContext(), R.color.gray));
                    AbsoluteSizeSpan restSize = new AbsoluteSizeSpan(30);
                    sb1.setSpan(restColor, code.length(), sb1.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    sb1.setSpan(restSize, code.length(), sb1.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);


                    TextView textView = new TextView(view.getContext());
                    frameLayout.addView(textView);
                    int startime = Integer.parseInt(list.get(wk).get(clas).get("startime").toString());
                    int endtime = Integer.parseInt(list.get(wk).get(clas).get("endtime").toString());
                    textView.setHeight(dip2px(this.getContext(), 40 * (endtime - startime + 1)));
                    textView.setWidth(dip2px(this.getContext(), 75));textView.setText(sb1);
                    textView.setBackgroundResource(R.drawable.ripple_item_normal);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundTintList(classColor(classNumber));
                    FrameLayout.LayoutParams lp =  (FrameLayout.LayoutParams) textView.getLayoutParams();
                    lp.setMargins(dip2px(this.getContext(), 75*wk), dip2px(this.getContext(), 40*startime), dip2px(this.getContext(), 75*(list.size()-wk-1)), dip2px(this.getContext(), 40 * (21-endtime)));
                    classNumber++;


                    final String message = this.getContext().getString(R.string.leader) + "\n" +
                            list.get(wk).get(clas).get("leader") + "\n" +
                            "\n" + this.getContext().getString(R.string.type) + "\n" +
                            list.get(wk).get(clas).get("type") + "\n" +
                            "\n" + this.getContext().getString(R.string.location) + "\n" +
                            list.get(wk).get(clas).get("location") + "\n" +
                            "\n" + this.getContext().getString(R.string.classNo) + "\n" +
                            list.get(wk).get(clas).get("class") + "\n" +
                            "\n" + this.getContext().getString(R.string.weeks) + "\n" +
                            list.get(wk).get(clas).get("weeks");

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                            dialog.setTitle(code);
                            dialog.setMessage(message);
                            dialog.setCancelable(true);
                            dialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                        }
                    });


                }
            }
        }
    }

    /**
     * This function will be used in writeDailyDataToTable to
     * highlight corresponding header background for the day of the week and time of the day
     *
     * @param view
     */
    private void tintWeekAndTime(View view) {
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        TextView textView = new TextView(this.getContext());
        if (day == 1)
            textView = (TextView) view.findViewById(R.id.Sun);
        else if (day == 2)
            textView = (TextView) view.findViewById(R.id.Mon);
        else if (day == 3)
            textView = (TextView) view.findViewById(R.id.Tue);
        else if (day == 4)
            textView = (TextView) view.findViewById(R.id.Wed);
        else if (day == 5)
            textView = (TextView) view.findViewById(R.id.Thu);
        else if (day == 6)
            textView = (TextView) view.findViewById(R.id.Fri);
        else if (day == 7)
            textView = (TextView) view.findViewById(R.id.Sat);
        textView.setBackgroundColor(this.getContext().getColor(R.color.colorAccent));
        textView.setTextColor(this.getContext().getColor(R.color.white));

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TextView timeView = new TextView(this.getContext());
        if (hour == 9 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t0900);
        else if (hour == 9 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t0930);
        else if (hour == 10 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1000);
        else if (hour == 10 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1030);
        else if (hour == 11 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1100);
        else if (hour == 11 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1130);
        else if (hour == 12 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1200);
        else if (hour == 12 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1230);
        else if (hour == 13 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1300);
        else if (hour == 13 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1330);
        else if (hour == 14 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1400);
        else if (hour == 14 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1430);
        else if (hour == 15 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1500);
        else if (hour == 15 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1530);
        else if (hour == 16 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1600);
        else if (hour == 16 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1630);
        else if (hour == 17 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1700);
        else if (hour == 17 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1730);
        else if (hour == 18 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1800);
        else if (hour == 18 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1830);
        else if (hour == 19 && minute < 30)
            timeView = (TextView) view.findViewById(R.id.t1900);
        else if (hour == 19 && minute >= 30)
            timeView = (TextView) view.findViewById(R.id.t1930);
        timeView.setBackgroundColor(this.getContext().getColor(R.color.colorAccent));
        timeView.setTextColor(this.getContext().getColor(R.color.white));
    }

    /**
     * This function will be used in writeDailyDataToTable to
     * check whether the class is in this week
     *
     * @param map
     * @return
     */
    private boolean weekCheck(Map<String, String> map) {
        DateTime dateTime = new DateTime();
        DateTime dt = new DateTime(2018, 2, 19, 0, 0, 0, 0);
        dateTime = dateTime.dayOfWeek().withMinimumValue();
        int currentWeek = Weeks.weeksBetween(dt, dateTime).getWeeks();

        String input = map.get("weeks");
        int[] weeks = new int[14];
        for (int i = 0; i < 14; i++) {
            weeks[i] = 0;
        }

        for (int i = 0; i < input.length(); i++) {
            if (input.substring(i, i + 1).equals(" ")) {
                int j = i + 1;
                int record = j;
                String startWeek = new String();
                while (j < input.length() && !input.substring(j, j + 1).equals("-") && !input.substring(j, j + 1).equals(",")) {
                    startWeek += input.substring(j, j + 1);
                    j++;
                    record++;
                }
                if (record < input.length() && input.substring(record, record + 1).equals("-")) {
                    j = record + 1;
                    String endWeek = new String();
                    while (j < input.length() && !input.substring(j, j + 1).equals("-") && !input.substring(j, j + 1).equals(",")) {
                        endWeek += input.substring(j, j + 1);
                        j++;
                    }
                    int startInt = Integer.parseInt(startWeek);
                    int endInt = Integer.parseInt(endWeek);
                    while (startInt < endInt) {
                        weeks[startInt - 1] = 1;
                        startInt++;
                    }
                } else {
                    weeks[Integer.parseInt(startWeek) - 1] = 1;
                }
            }
        }

        boolean result;
        if (currentWeek > 0 && currentWeek < 14) {
            result = (1 == weeks[currentWeek - 1]);
            return result;
        }
        return false;
    }

    /**
     * This function gets the short version of location.
     * For example: it gets "SC176" from "Science Building-SC176"
     *
     * @param string
     * @return
     */
    private String getLocation(String string) {
        String result = new String();
        for (int i = 0; i < string.length(); i++) {
            int j = i + 1;
            if (string.substring(i, i + 1).equals("-")) {
                while (j < string.length() && !string.substring(j, j + 1).equals(",")) {
                    result += string.substring(j, j + 1);
                    j++;
                }
                if (j < string.length() && string.substring(j, j + 1).equals(","))
                    result += ", ";
            }
        }

        return result;
    }

    /**
     * This is a stupid function, and I will change it someday
     * it provides color for classes
     * @param classNumer
     * @return
     */
    private ColorStateList classColor(int classNumer) {
        ColorStateList result;
        result = this.getContext().getColorStateList(R.color.class1);
        if (classNumer%14 == 0)
            result = this.getContext().getColorStateList(R.color.class1);
        if (classNumer%14 == 1)
            result = this.getContext().getColorStateList(R.color.class2);
        if (classNumer%14 == 2)
            result = this.getContext().getColorStateList(R.color.class3);
        if (classNumer%14 == 3)
            result = this.getContext().getColorStateList(R.color.class4);
        if (classNumer%14 == 4)
            result = this.getContext().getColorStateList(R.color.class5);
        if (classNumer%14 == 5)
            result = this.getContext().getColorStateList(R.color.class6);
        if (classNumer%14 == 6)
            result = this.getContext().getColorStateList(R.color.class7);
        if (classNumer%14 == 7)
            result = this.getContext().getColorStateList(R.color.class8);
        if (classNumer%14 == 8)
            result = this.getContext().getColorStateList(R.color.class9);
        if (classNumer%14 == 9)
            result = this.getContext().getColorStateList(R.color.class10);
        if (classNumer%14 == 10)
            result = this.getContext().getColorStateList(R.color.class11);
        if (classNumer%14 == 11)
            result = this.getContext().getColorStateList(R.color.class12);
        if (classNumer%14 == 12)
            result = this.getContext().getColorStateList(R.color.class13);
        if (classNumer%14 == 13)
            result = this.getContext().getColorStateList(R.color.class14);
        return result;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
