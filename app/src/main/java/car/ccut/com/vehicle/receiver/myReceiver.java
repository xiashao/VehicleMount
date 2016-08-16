package car.ccut.com.vehicle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import car.ccut.com.vehicle.service.FloatWindowService;

/**
 * Created by MingXia Shang on 2016/8/17.
 */
public class myReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.startService(new Intent(context,FloatWindowService.class));
    }
}
