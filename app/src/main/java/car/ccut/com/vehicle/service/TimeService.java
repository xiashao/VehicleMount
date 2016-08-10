package car.ccut.com.vehicle.service;

/**
 * Created by MingXia Shang on 2016/8/7.
 */

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import car.ccut.com.vehicle.ui.HighspeeedActivity;

public class TimeService extends Service {
    private String TAG = "TimeService";
    private Timer timer = null;
    private SimpleDateFormat sdf = null;
    private Intent timeIntent = null;
    private Bundle bundle = null;
    private double a,b,c,d;
    public long recordingTime=1000;// 记录下来的总时间
    private LocationClient locationClient = null;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "TimeService->onCreate");

        //初始化
        this.init();
        JW();
        //定时器发送广播
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //发送广播
                sendTimeChangedBroadcast();
                recordingTime = recordingTime + 1000;
            }
        }, 0, 1000);

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
    private void JW()
    {
        locationClient = new LocationClient(this);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);		//是否打开GPS
        option.setScanSpan(30000);
        option.setCoorType("bd09ll");		//设置返回值的坐标类型。
        option.setProdName("LocationDemo");	//设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        locationClient.setLocOption(option);

        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                // TODO Auto-generated method stub
                if (location == null) {
                    return;
                }
                c = a;
                d = b;
                a = location.getLatitude();
                b = location.getLongitude();
                if (a == c) {
                    recordingTime = 0;
                }
            }

        });
        locationClient.start();
        locationClient.requestLocation();
    }
}