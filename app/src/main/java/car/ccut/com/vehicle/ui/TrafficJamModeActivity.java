package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.TrafficJam;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.listener.MyOrientationListener;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.service.FloatWindowService;

/**
 * *
 * へ　　　　　／|
 * 　　/＼7　　　 ∠＿/
 * 　 /　│　　 ／　／
 * 　│　Z ＿,＜　／　　 /`ヽ
 * 　│　　　　　ヽ　　 /　　〉
 * 　 Y　　　　　`　 /　　/
 * 　ｲ●　､　●　　⊂⊃〈　　/
 * 　()　 へ　　　　|　＼〈
 * 　　>ｰ ､_　 ィ　 │ ／／      去吧！
 * 　 / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　 ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　7　　　　　　　|／
 * 　　＞―r￣￣`ｰ―＿
 * Created by WangXin on 2016/7/28 0028.
 */
public class TrafficJamModeActivity extends BaseActivity{

    private boolean isBack;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isBack = false;
        }
    };
    private LocationClient locationClient;
    private BaiduMap mBaiduMap;
    private List<TrafficJam>dataList;
    private Map params = new HashMap();
    private MyLocationListener myLocationListener;
    private boolean isFristIn =true;
    private BitmapDescriptor mMarker;
    private String city;
    private Gson gson = new Gson();
    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    /**
     * 当前的精度
     */
    private float mCurrentAccracy;
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;
    @Bind(R.id.mapView)
    MapView mapView;
    @Bind(R.id.title)
    RelativeLayout title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_traffic_jam;
    }


    @Override
    public void initView() {
        title.setVisibility(View.GONE);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setTrafficEnabled(true);;
        //设置地图放缩比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(13.0f);
        mBaiduMap.setMapStatus(msu);
        locationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
        initOritationListener();
    }

    @Override
    public void initData() {

    }

    private void initOritationListener()
    {
        myOrientationListener = new MyOrientationListener(
                this);
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener()
                {
                    @Override
                    public void onOrientationChanged(float x)
                    {
                        mXDirection = (int) x;

                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLantitude)
                                .longitude(mCurrentLongitude).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        // 设置自定义图标
                        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                .fromResource(R.mipmap.navi_map_gps_locked);
                        MyLocationConfiguration config = new MyLocationConfiguration(
                                MyLocationConfiguration.LocationMode.COMPASS, true, mCurrentMarker);
                        mBaiduMap.setMyLocationConfigeration(config);
                    }
                });
    }


    @Override
    @OnClick({R.id.plugging,R.id.broadcast})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.plugging:
                updateTrafficJamInfo();
                break;
            case R.id.broadcast:
                Intent intent = new Intent(this,BroadcastStatusActivity.class);
                intent.putExtra("city",city);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // 双击返回桌面，默认返回true，调用finish()
                if (!isBack) {
                    isBack = true;
                    Toast.makeText(this, "再按一次返回键回到桌面", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }else {
                    AppManager.getAppManager().finishAllActivity();
                    Intent intent = new Intent(this, FloatWindowService.class);
                    stopService(intent);
                    finish();
                    System.exit(0);
                }
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation==null||mapView==null){
                return;
            }
            final MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(bdLocation.getRadius())//
                    .direction(mXDirection).latitude(bdLocation.getLatitude())//
                    .longitude(bdLocation.getLongitude()).//
                    build();
            mBaiduMap.setMyLocationData(data);
            mCurrentLantitude = bdLocation.getLatitude();
            mCurrentLongitude = bdLocation.getLongitude();
            mCurrentAccracy = bdLocation.getRadius();
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.mipmap.navi_map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.COMPASS, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(config);
            if (isFristIn){
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFristIn = false;
            }
            city = bdLocation.getCity();
            params.clear();
            params.put("city",city);
            JsonRequestWithAuth<AjaxResponse> getTrafficJamInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.GET_ALL_TRAFFIC_INFO_BY_CITY, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
                @Override
                public void onResponse(AjaxResponse response) {
                    dataList = gson.fromJson(gson.toJson(response.getResponseData().get("allTrafficJamInfo")),new TypeToken<List<TrafficJam>>(){}.getType());
                    if (dataList!=null&&!dataList.isEmpty()){
                        addOverlays(dataList);
                        markerOnclick();
                    }
                }
            }, params, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(TrafficJamModeActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
            MyApplication.getHttpQueues().add(getTrafficJamInfo);
            MyApplication.getHttpQueues().start();
        }
    }

    //添加覆盖物
    private void addOverlays(List<TrafficJam> trafficJams){
        mBaiduMap.clear();
        OverlayOptions options;
        for (TrafficJam trafficJam : trafficJams){
            double lan = Double.valueOf(trafficJam.getLatitude()).doubleValue();
            double lon = Double.valueOf(trafficJam.getLontitude()).doubleValue();
            LatLng latLng = new LatLng(lan,lon);
            if ("严重拥堵".equals(trafficJam.getJamStatus())){
                mMarker = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),R.mipmap.heavy_jam));
            }else if ("轻度拥堵".equals(trafficJam.getJamStatus())){
                mMarker = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),R.mipmap.light_jam));
            }
            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            Marker marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("trafficJam",trafficJam);
            marker.setExtraInfo(bundle);
        }
    }

    //覆盖物点击事件
    private void markerOnclick(){
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final TrafficJam trafficJam = (TrafficJam) marker.getExtraInfo().getSerializable("trafficJam");
                Toast.makeText(TrafficJamModeActivity.this,trafficJam.getAddress(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()){
            locationClient.start();
        }
        myOrientationListener.start();
    }

    public void updateTrafficJamInfo(){
        if (MyApplication.getUpdateTrafficJamInfo().getId().equals("")){
            Toast.makeText(this,"您还未发布堵车信息",Toast.LENGTH_SHORT).show();
            return;
        }
        if (MyApplication.getUpdateTrafficJamInfo().isEndTrafficJam()){
            Toast.makeText(this,"已发布解堵信息",Toast.LENGTH_SHORT).show();
            return;
        }
        params.clear();
        showWaitDialog();
        params.put("id",MyApplication.getUpdateTrafficJamInfo().getId());
        JsonRequestWithAuth<AjaxResponse> updateTrafficJamInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.UPDATE_TRAFFIC_JAM_INFO, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
            @Override
            public void onResponse(AjaxResponse response) {
                hideWaitDialog();
                Toast.makeText(TrafficJamModeActivity.this,response.getReturnMsg(),Toast.LENGTH_SHORT).show();
                MyApplication.getUpdateTrafficJamInfo().setEndTrafficJam(true);
                MyApplication.getUpdateTrafficJamInfo().setId("");
            }
        }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitDialog();
            }
        });
        MyApplication.getHttpQueues().add(updateTrafficJamInfo);
        MyApplication.getHttpQueues().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mapView!=null){
            mapView.onDestroy();
        }
        if(mBaiduMap!=null){
            mBaiduMap= null;
        }
        if (locationClient.isStarted()){
            locationClient.stop();
            locationClient=null;
        }
        MyApplication.getHttpQueues().cancelAll("getTrafficJamInfo");
        MyApplication.getHttpQueues().cancelAll("updateTrafficJamInfo");
    }
}
