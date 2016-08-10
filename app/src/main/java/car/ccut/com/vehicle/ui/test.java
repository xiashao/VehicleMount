package car.ccut.com.vehicle.ui;

/**
 * Created by MingXia Shang on 2016/8/10.
 */
import  android.app.Activity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
        import com.baidu.location.LocationClientOption;
        import com.baidu.location.LocationClientOption.LocationMode;

import java.util.HashMap;

import car.ccut.com.vehicle.R;

public class test extends Activity {
    private TextView locationInfoTextView = null;
    private Button startButton = null;
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;
    private double a,b,c,d;
    private String aa;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        locationInfoTextView = (TextView) this.findViewById(R.id.tv_loc_info);
        startButton = (Button) this.findViewById(R.id.btn_start);


        locationClient = new LocationClient(this);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);		//是否打开GPS
        option.setScanSpan(10000);
        option.setCoorType("bd09ll");		//设置返回值的坐标类型。
        option.setProdName("LocationDemo");	//设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
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
                    aa="ting";
                    locationInfoTextView.setText(aa);
                } else {
                    locationInfoTextView.setText("kai");
                }
            }

        });
        locationClient.start();
        locationClient.requestLocation();
       /* startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (locationClient == null) {
                    return;
                }
                if (locationClient.isStarted()) {
                    startButton.setText("Start");
                    locationClient.stop();
                } else {
                    startButton.setText("Stop");
                    locationClient.start();
					*//*
					 *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
					 *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
					 *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
					 *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
					 *定时定位时，调用一次requestLocation，会定时监听到定位结果。
					 *//*
                    locationClient.requestLocation();
                }
            }
        });
*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }


}