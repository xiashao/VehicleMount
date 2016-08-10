package car.ccut.com.vehicle.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by MingXia Shang on 2016/8/4.
 */
public class MyService extends Service {
    private ArrayList<PhoneContact> list = new ArrayList<PhoneContact>();//存放联系人对象的集合
    public static final String TAG = "MyService";

    @Override
    public void onCreate() {
        super.onCreate();
        list=(ArrayList<PhoneContact>) PhoneContactDao.getPhoneContacts(MyService.this);
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        list=(ArrayList<PhoneContact>) PhoneContactDao.getPhoneContacts(MyService.this);
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}