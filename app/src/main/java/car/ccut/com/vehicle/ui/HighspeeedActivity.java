package car.ccut.com.vehicle.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.speech.SynthesizerListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.bean.PoiInfos;
import car.ccut.com.vehicle.listener.MyOrientationListener;
import car.ccut.com.vehicle.service.FloatWindowService;
import car.ccut.com.vehicle.service.MusicService;
import car.ccut.com.vehicle.service.VoiceRecognition;
import car.ccut.com.vehicle.util.JsonParser;
import car.ccut.com.vehicle.util.MusicUtils;
import android.widget.Chronometer.OnChronometerTickListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HighspeeedActivity extends Activity implements OnClickListener {
    private com.iflytek.cloud.SynthesizerListener mTtsListener;
    // 默认云端发音人
    public static String voicerCloud="xiaoyan";
    private TextView state;
    private SpeechSynthesizer mTts;
    private int isSpeaking = 0;
    private ImageButton call;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isFristIn =true;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private float mCurrentAccracy;
    private MyOrientationListener myOrientationListener;
    private AutoCompleteTextView search;
    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener;
    private PoiResult mPoiResult;
    private boolean searchText;
    private  String [] searchResult;
    private ArrayAdapter<String> searchAdapter;
    List<PoiInfos> poiInfos = new ArrayList<PoiInfos>();
    //每页容量
    private static final int PAGE_CAPACITY=50;
    //第一页
    private static final int PAGE_NUM = 0;
    //搜索半径10km
    private static final int RADIUS = 10000;
    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;
    private Chronometer timer;
    final int RIGHT = 0;
    final int LEFT = 1;
    private boolean macControl=true;
    private GestureDetector gestureDetector;
    private boolean isBack;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isBack = false;
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highspeed);
        mTts= SpeechSynthesizer.createSynthesizer(this, null);
        time();
        AppManager.getAppManager().addActivity(this);
        initView();
        call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:10010"));
                startActivity(intent);
            }
        });
    }

    private void time(){
        timer = (Chronometer)this.findViewById(R.id.chronometer);
        timer.setOnChronometerTickListener(new OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String time = chronometer.getText().toString();
                if ("00:05".equals(time)) {
                    startTts("您已经进入疲劳驾驶状态");
                    state.setText("疲劳驾驶");
                }

            }
        });
        timer.setBase(SystemClock.elapsedRealtime());
        //开始计时
        timer.start();
    }
    private void initView() {
        call=(ImageButton)findViewById(R.id.call);
        mMapView = (MapView) findViewById(R.id.bmapView);
        state=(TextView)findViewById(R.id.state);
        search = (AutoCompleteTextView) findViewById(R.id.search);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setTrafficEnabled(true);
        //设置地图放缩比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        locationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(1000);
        initOritationListener();
        mPoiSearch = PoiSearch.newInstance();
        poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(final PoiResult poiResult) {
//                hideWaitDialog();
                if (poiResult==null||poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND){
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD){
                    Toast.makeText(HighspeeedActivity.this,"在附近未找到相关信息",Toast.LENGTH_SHORT).show();
                }
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR){
                    mPoiResult=poiResult;
                    if (searchText){
                        if (searchResult==null){
                            searchResult = new String[poiResult.getAllPoi().size()];
                        }
                        for (int i = 0;i<poiResult.getAllPoi().size();i++){
                            searchResult[i]=poiResult.getAllPoi().get(i).name;
                        }
                        searchAdapter = new ArrayAdapter<String>(HighspeeedActivity.this,android.R.layout.simple_list_item_1,searchResult){
                            private Filter f;
                            public Filter getF() {
                                if(f ==null){
                                    f = new Filter() {
                                        @Override
                                        protected FilterResults performFiltering(CharSequence charSequence) {
                                            ArrayList<Object> suggestions = new ArrayList<Object>();
                                            for(String adr:searchResult){
                                                suggestions.add(adr);
                                            }
                                            FilterResults filterResults = new FilterResults();
                                            filterResults.values = suggestions;
                                            filterResults.count=suggestions.size();
                                            return filterResults;
                                        }

                                        @Override
                                        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                                            if (filterResults.count > 0) {
                                                searchAdapter.notifyDataSetChanged();
                                            } else {
                                                searchAdapter.notifyDataSetInvalidated();
                                            }
                                        }
                                    };
                                }
                                return f;
                            }
                        };
                        search.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                        searchAdapter.notifyDataSetInvalidated();
                    }else {
                        poiInfos.clear();
                        for (int i=0;i<poiResult.getAllPoi().size();i++){
                            PoiInfos poiInfo= new PoiInfos();
                            poiInfo.setName(poiResult.getAllPoi().get(i).name);
                            poiInfo.setAddress(poiResult.getAllPoi().get(i).address);
                            poiInfo.setPhoneNum(poiResult.getAllPoi().get(i).phoneNum);
                            poiInfo.setCity(poiResult.getAllPoi().get(i).city);
                            poiInfo.setPostCode(poiResult.getAllPoi().get(i).postCode);
                            poiInfo.setLatitude(poiResult.getAllPoi().get(i).location.latitude);
                            poiInfo.setLontitude(poiResult.getAllPoi().get(i).location.longitude);
                            poiInfos.add(poiInfo);
                        }
                        Intent intent = new Intent(HighspeeedActivity.this,SearchInfosActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("poiInfos", (Serializable) poiInfos);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInfo(search.getText().toString());
                searchText=true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchAdapter != null) {
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.notifyDataSetInvalidated();
                }
            }
        });
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                poiInfos.clear();
                try {
                    PoiInfos poiInfo= new PoiInfos();
                    poiInfo.setName(mPoiResult.getAllPoi().get(i).name);
                    poiInfo.setAddress(mPoiResult.getAllPoi().get(i).address);
                    poiInfo.setPhoneNum(mPoiResult.getAllPoi().get(i).phoneNum);
                    poiInfo.setCity(mPoiResult.getAllPoi().get(i).city);
                    poiInfo.setPostCode(mPoiResult.getAllPoi().get(i).postCode);
                    poiInfo.setLatitude(mPoiResult.getAllPoi().get(i).location.latitude);
                    poiInfo.setLontitude(mPoiResult.getAllPoi().get(i).location.longitude);
                    poiInfos.add(poiInfo);
                    Intent intent = new Intent(HighspeeedActivity.this,SearchInfosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("poiInfos", (Serializable) poiInfos);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void searchInfo(String searchInfo) {
        mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(searchInfo)
                .location(new LatLng(mCurrentLantitude, mCurrentLongitude))
                .pageCapacity(PAGE_CAPACITY)
                .pageNum(PAGE_NUM)
                .radius(RADIUS));
    }

    private void initOritationListener()
    {
        myOrientationListener = new MyOrientationListener(
                getApplicationContext());
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



    public void setTitle (String title) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
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

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        myOrientationListener.stop();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView!=null){
            mMapView.onDestroy();
        }
        if (locationClient.isStarted()){
            locationClient.stop();
            locationClient=null;
        }
    }


    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation==null||mMapView==null){
                return;
            }
            MyLocationData data = new MyLocationData.Builder()//
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
    private void startTts(String text){
        int code = mTts.startSpeaking(text,mTtsListener);
        if (code!=ErrorCode.SUCCESS){
            System.out.println(code);
        }
    }
}
