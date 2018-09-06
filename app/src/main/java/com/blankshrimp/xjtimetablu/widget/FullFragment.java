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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankshrimp.xjtimetablu.R;
import com.blankshrimp.xjtimetablu.util.NewListDAO;

import java.util.List;
import java.util.Map;

/**
 * Created by Flabbergast13 on 18/2/27.
 */

public class FullFragment extends Fragment {
    private SyncedHorizontalScrollView titleView;
    private SyncedHorizontalScrollView contentView;
    private FrameLayout frameLayout;
    private LinearLayout weekContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_fragment, container, false);

        titleView = (SyncedHorizontalScrollView) view.findViewById(R.id.FtitleScrollView);
        contentView = (SyncedHorizontalScrollView) view.findViewById(R.id.FcontentScrollView);
        frameLayout = (FrameLayout) view.findViewById(R.id.FclassContainer);
        weekContainer = (LinearLayout) view.findViewById(R.id.Fright_title_container);
        titleView.setScrollView(contentView);
        contentView.setScrollView(titleView);

        Bundle bundle = getArguments();
        String name = new String();
        int[] weekLength = new int[7];
        if (bundle != null) {
            name = bundle.getString("name");
        }
        NewListDAO newListDAO = new NewListDAO(view.getContext());
        displayData(newListDAO.queryGeneralTable(name), view);
        drawWeeks(newListDAO.getWeekLength(name, view.getContext()), view);

        return view;
    }

    private void displayData(List<List<Map<String, String>>> list, View view) {
        int classNumber = 0;
        for (int wk = 0; wk < list.size(); wk++) {
            for (int clas = 0; clas < list.get(wk).size(); clas++) {
                Map<String, String> map = list.get(wk).get(clas);
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
                textView.setBackgroundTintList(classColor(classNumber));
                textView.setGravity(Gravity.CENTER);
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

    private void drawWeeks(int[] list, View view) {
        for (int i = 0; i < 7; i++) {
            TextView textView = new TextView(this.getContext());
            textView.setWidth(dip2px(this.getContext(), 75*list[i]));
            textView.setHeight(dip2px(this.getContext(), 40));
            textView.setGravity(Gravity.CENTER);
            if (i == 0)
                textView.setText(this.getContext().getString(R.string.Monday));
            else if (i == 1){
                textView.setText(this.getContext().getString(R.string.Tuesday));
                textView.setBackgroundColor(this.getContext().getColor(R.color.gray));
            } else if (i == 2)
                textView.setText(this.getContext().getString(R.string.Wednesday));
            else if (i == 3){
                textView.setText(this.getContext().getString(R.string.Thursday));
                textView.setBackgroundColor(this.getContext().getColor(R.color.gray));
            } else if (i == 4)
                textView.setText(this.getContext().getString(R.string.Friday));
            else if (i == 5){
                textView.setText(this.getContext().getString(R.string.Saturday));
                textView.setBackgroundColor(this.getContext().getColor(R.color.gray));
            } else if (i == 6)
                textView.setText(this.getContext().getString(R.string.Sunday));
            weekContainer.addView(textView);
        }
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

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
