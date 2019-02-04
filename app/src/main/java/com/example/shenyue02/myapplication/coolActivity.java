package com.example.shenyue02.myapplication;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class coolActivity extends AppCompatActivity {
    Timer timer;
    TimerTask mTimerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        timer = new Timer();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cool);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final String old_path="/data/data/com.tencent.mm/MicroMsg/bfbd9324472ccd2b2a21cc210b2848bd/EnMicroMsg.db";
        final String new_path="/data/data/com.example.shenyue02.myapplication/EnMicroMsg.db";

        mTimerTask =new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataHelp.copyFile(coolActivity.this,old_path,new_path);
                    }
                });
            }
        };
        timer.schedule(mTimerTask, 0, 10000);//10秒一获取
    }

    public void onClickedD(View e){

        String a = getUin("/data/data/com.tencent.mm/shared_prefs/system_config_prefs.xml");

    }




    public static String getUin(String path) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(path));
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();// 产生第一个事件
            while (eventType != XmlPullParser.END_DOCUMENT) { //处理事件，不碰到文档结束就一直处理
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // 不做任何操作或初开始化数据
                        break;
                    case XmlPullParser.START_TAG:
                        // 解析XML节点数据
                        // 获取当前标签名字
                        String tagName = parser.getName();
                        if ("int".equals(parser.getName())) {
                            String name = parser.getAttributeValue(0);
                            String value = parser.getAttributeValue(1);
                            Log.e("int", "name:" + name + ",value:" + value);
                            return value;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        // 单节点完成，可往集合里边添加新的数据
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                // 别忘了进入下一个元素并触发相应事件 ，不然就会死循环
                eventType = parser.next();
            }
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException:", e.toString());
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException:", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
