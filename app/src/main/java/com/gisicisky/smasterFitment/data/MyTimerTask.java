package com.gisicisky.smasterFitment.data;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.smartouwei.DeviceListActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    private String deviceMac;
    private Intent intent;
    private Handler handler;
    private int iWhat;
    private int iCount = 0;
    private Timer selfTimer;
    private boolean isRun;

    public MyTimerTask(String strM,Timer selfTimer,int iWhat,Handler handler,Intent intent) {
        this.iCount = 0;
        this.isRun = true;
        this.selfTimer = selfTimer;
        this.deviceMac = strM;
        this.intent = intent;
        this.handler = handler;
        this.iWhat = iWhat;


    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    @Override
    public void run() {

//        if (iCount >= 5) {
//
//            Timer selfT = DeviceListActivity.timerMap.get(deviceMac.toLowerCase());
//            if (selfT != null) {
//                selfT.cancel();
//                DeviceListActivity.timerMap.remove(deviceMac.toLowerCase());
//            }
//            TimerTask selfTask = DeviceListActivity.timerTaskMap.get(deviceMac.toLowerCase());
//            if (selfTask != null) {
//                selfTask.cancel();
//                DeviceListActivity.timerTaskMap.remove(deviceMac.toLowerCase());
//            }
//            return;
//        }
//
//        ++iCount;
//        Log.e("MyTimerTask", "MyTimerTask,定时发送数据！ ");
//        Message msg = new Message();
//        msg.what = iWhat;
//        msg.obj = intent;
//        handler.sendMessage(msg);


        while (isRun && iCount < 5) {
            try {
                ++iCount;
                Log.e("MyTimerTask", "MyTimerTask,定时发送数据！ ");
                Message msg = new Message();
                msg.what = iWhat;
                msg.obj = intent;
                handler.sendMessage(msg);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        Log.e("MyTimerTask", "MyTimerTask,取消发送数据！ ");
        Timer selfT = DeviceListActivity.timerMap.get(deviceMac.toLowerCase());
        if (selfT != null) {
            selfT.cancel();
            DeviceListActivity.timerMap.remove(deviceMac.toLowerCase());
        }
        TimerTask selfTask = DeviceListActivity.timerTaskMap.get(deviceMac.toLowerCase());
        if (selfTask != null) {
            selfTask.cancel();
            DeviceListActivity.timerTaskMap.remove(deviceMac.toLowerCase());
        }
    }
}
