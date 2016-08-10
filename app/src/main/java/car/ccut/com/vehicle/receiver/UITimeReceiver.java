package car.ccut.com.vehicle.receiver;

/**
 * Created by MingXia Shang on 2016/8/7.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;

import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.ui.DrivingActivity;
import car.ccut.com.vehicle.ui.HighspeeedActivity;

/**
 * 自定义的UI层BroadcastReceiver，负责监听从后台Service发送过来的广播，根据广播数据更新UI
 * @author zhangyg
 */
public class UITimeReceiver extends BroadcastReceiver{
    private HighspeeedActivity dUIActivity = new HighspeeedActivity();
    private DrivingActivity UIActivity = new DrivingActivity();
    public long recordingTime;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        recordingTime = bundle.getLong("s");
        String action = intent.getAction();
        if(HighspeeedActivity.TIME_CHANGED_ACTION.equals(action)){

            dUIActivity.timer.setBase(SystemClock.elapsedRealtime() - recordingTime);
            //开始计时
            dUIActivity.timer.start();
        }

    }

}