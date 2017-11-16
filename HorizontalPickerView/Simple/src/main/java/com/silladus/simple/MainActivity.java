package com.silladus.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.silladus.horizontalpickerview.HorizontalPickerViewFromDraw;
import com.silladus.horizontalpickerview.HorizontalPickerViewFromLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] strings = new String[31];
        for (int i = 15; i < 46; i++) {
            strings[i - 15] = i + "";
        }
        HorizontalPickerViewFromDraw horizontalPickerView = (HorizontalPickerViewFromDraw) findViewById(R.id.mHorizontalPickerView);
        horizontalPickerView.setData(strings);
        HorizontalPickerViewFromLayout horizontalPickerView1 = (HorizontalPickerViewFromLayout) findViewById(R.id.mHorizontalPickerView1);
        horizontalPickerView1.setData(strings);
        horizontalPickerView1.setSelectListener(new HorizontalPickerViewFromLayout.SelectListener() {
            @Override
            public void currentItem(String currentObject) {
                String select = currentObject;
                Log.e("currentItem: ", select);
            }
        });
        horizontalPickerView.setSelectListener(new HorizontalPickerViewFromDraw.SelectListener() {
            @Override
            public void currentItem(String currentObject) {
                String select = currentObject;
                Log.e("currentItem0: ", select);
            }
        });
    }

}
