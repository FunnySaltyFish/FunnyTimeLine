package com.test.funnytimeline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.funny.funnytimeline.FunnyTimeLine;
import com.funny.funnytimeline.ScreenUtil;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    FunnyTimeLine funnyTimeLine;
    View.OnClickListener listener;
    RadioButton b1,b2,b3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int screenWidth = ScreenUtil.getScreenRealWidth(this);
        int screenHeight = ScreenUtil.getScreenRealHeight(this);
        Log.i(TAG,String.format("screenW,H:(%d,%d)",screenWidth,screenHeight));
        setContentView(R.layout.main);
        funnyTimeLine=findViewById(R.id.timeline);
        listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.radioButton:
                        funnyTimeLine.setTimeKind((short) 0);
                        break;
                    case R.id.radioButton2:
                        funnyTimeLine.setTimeKind((short) 1);
                        break;
                    case R.id.radioButton3:
                        funnyTimeLine.setTimeKind((short) 2);
                        break;
                }
            }
        };
        b1=findViewById(R.id.radioButton);
        b2=findViewById(R.id.radioButton2);
        b3=findViewById(R.id.radioButton3);
        b1.setOnClickListener(listener);
        b2.setOnClickListener(listener);
        b3.setOnClickListener(listener);
        //setContentView(view);
    }
}