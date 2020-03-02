package com.gisicisky.smasterFitment.data;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;

public class MyRunableTimer implements Runnable {

    private Intent intent;
    private Handler handler;
    private int iWhat;
    private int iCount = 0;

    public MyRunableTimer( int iWhat, Handler handler, Intent intent) {
        this.iCount = 0;
        this.intent = intent;
        this.handler = handler;
        this.iWhat = iWhat;


    }

    @Override
    public void run() {
        if (iCount >= 5) {
            return;
        }
        Log.e("MyTimerTask", "MyTimerTask,定时发送数据！ ");
        ++iCount;
        Message msg = new Message();
        msg.what = iWhat;
        msg.obj = intent;
        handler.sendMessage(msg);

        handler.postDelayed(this,1000);

    }
}
