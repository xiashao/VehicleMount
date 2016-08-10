package car.ccut.com.vehicle.service;

/**
 * Created by MingXia Shang on 2016/8/7.
 */

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import car.ccut.com.vehicle.receiver.UITimeReceiver;
import car.ccut.com.vehicle.ui.DrivingActivity;
import car.ccut.com.vehicle.ui.HighspeeedActivity;

public class TimeService extends Service {
    private String TAG = "TimeService";
    private Timer timer = null;
    private SimpleDateFormat sdf = null;
    private Intent timeIntent = null;
    private Bundle bundle = null;
    private double beginLo;
    private double beginLa;
    private double ELo;
    private double ELa;
    public long recordingTime=0;// 记录下来的总时间
   /* private UITimeReceiver rord=new UITimeReceiver();*/
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "TimeService->onCreate");

        //初始化
        this.init();
        //定时器发送广播
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //发送广播
                sendTimeChangedBroadcast();
                recordingTime = recordingTime + 1000;
            }
        }, 1000, 1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "TimeService->onBind");
        return null;
    }

    /**
     * 相关变量初始化
     */
    private void init(){
        timer = new Timer();
        sdf = new SimpleDateFormat("hh:mm:ss");
        timeIntent = new Intent();
        bundle = new Bundle();
    }

    /**
     * 发送广播，通知UI层时间已改变
     */
    private void sendTimeChangedBroadcast(){
        bundle.putLong("s",recordingTime);
        timeIntent.putExtras(bundle);
        timeIntent.setAction(HighspeeedActivity.TIME_CHANGED_ACTION);
        //发送广播，通知UI层时间改变了
        sendBroadcast(timeIntent);
    }

    /**
     * 获取最新系统时间
     * @return
     */
    private String getTime(){
        return sdf.format(new Date());
    }

    @Override
    public ComponentName startService(Intent service) {
        Log.i(TAG, "TimeService->startService");
        return super.startService(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "TimeService->onDestroy");
    }
    public void onLocationChanged(Location location) {
        if (location != null) {
            // 显示定位结果
            beginLo=location.getLongitude();
            beginLa=location.getLatitude();
        }
    }
}