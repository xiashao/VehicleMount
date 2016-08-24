package car.ccut.com.vehicle.receiver;

/**
 * Created by MingXia Shang on 2016/8/24.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.service.FloatWindowService;
import car.ccut.com.vehicle.service.MusicService;

public class HomeWatcherReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "HomeReceiver";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
    private MusicService mService;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(LOG_TAG, "onReceive: action: " + action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            // android.intent.action.CLOSE_SYSTEM_DIALOGS
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            Log.i(LOG_TAG, "reason: " + reason);

            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                // 短按Home键
                Log.i(LOG_TAG, "homekey");
               mService.pausePlay();
                System.out.println("++++++++++++++++++");

            }
            else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                // 长按Home键 或者 activity切换键
                Log.i(LOG_TAG, "long press home key or activity switch");
                mService.pausePlay();
            }
            else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                // 锁屏
                Log.i(LOG_TAG, "lock");
                mService.pausePlay();
            }
            else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                // samsung 长按Home键
                Log.i(LOG_TAG, "assist");
                mService.pausePlay();
            }

        }
    }

}