package com.example.notetaker2;

import android.content.Context;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ListView;


public class MyGridLayout extends GridLayout {
    public MyGridLayout(final Context context) {
        super(context);

        //Setting Column Count to 1 column
        setColumnCount(1);

        //This is where I create the ListView in which the created notes go.
        ListView notesList = new ListView(context);
        notesList.setId(R.id.listView);
        GridLayout.Spec rowSpec2 = GridLayout.spec(0, 1, 1);
        GridLayout.Spec colSpec2 = GridLayout.spec(0, 1, 1);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec2, colSpec2);
        layoutParams.setGravity(Gravity.TOP);
        notesList.setLayoutParams(layoutParams);
        this.addView(notesList);
    }
}
