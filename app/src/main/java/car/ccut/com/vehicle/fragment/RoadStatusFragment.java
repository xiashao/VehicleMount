package car.ccut.com.vehicle.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.ButterKnife;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.BaseFragment;
import car.ccut.com.vehicle.bean.Refuel.RefuelStationInfo;
import car.ccut.com.vehicle.bean.TrafficJam;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.listener.MyOrientationListener;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;

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
public class RoadStatusFragment extends BaseFragment{

    private List<TrafficJam> dataList;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private boolean isFristIn =true;
    private BitmapDescriptor mMarker;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_road_status,container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setTrafficEnabled(true);
        //设置地图放缩比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        locationClient = new LocationClient(getActivity());
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        locationClient.setLocOption(option);
        initOritationListener();
    }

    @Override
    public void initData() {
        super.initData();
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
            Map params = new HashMap();
            params.put("city",bdLocation.getCity());
            JsonRequestWithAuth<AjaxResponse> getAllTrafficJamInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.GET_ALL_TRAFFIC_INFO_BY_CITY, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
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

                }
            });
            if (isFristIn){
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFristIn = false;
            }
        }
    }

    private void initOritationListener()
    {
        myOrientationListener = new MyOrientationListener(
                getContext());
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
    public void onDestroy() {
        super.onDestroy();
        if(mapView!=null){
            mapView.onDestroy();
        }
        if(mapView!=null){
            mapView.onDestroy();
        }
        if (locationClient.isStarted()){
            locationClient.stop();
            locationClient=null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()){
            locationClient.start();
        }
        myOrientationListener.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        myOrientationListener.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    //添加覆盖物
    private void addOverlays(List<TrafficJam> trafficJams){
        mBaiduMap.clear();
        OverlayOptions options;
        for (TrafficJam trafficJam : trafficJams){
            LatLng latLng = new LatLng(Double.valueOf(trafficJam.getLatitude()),Double.valueOf(trafficJam.getLatitude()));
            if ("严重拥堵".equals(trafficJam.getJamStatus())){
                mMarker = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),R.mipmap.gas_station_icon));
            }else if ("轻度拥堵".equals(trafficJam.getJamStatus())){
                mMarker = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),R.mipmap.gas_station_icon));
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
                Toast.makeText(getActivity(),trafficJam.getAddress(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
