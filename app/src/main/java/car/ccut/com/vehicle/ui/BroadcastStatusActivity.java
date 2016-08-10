package car.ccut.com.vehicle.ui;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.BaseActivity;
import car.ccut.com.vehicle.bean.TrafficJam;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import me.drakeet.materialdialog.MaterialDialog;

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
 * Created by WangXin on 2016/8/4 0004.
 */
public class BroadcastStatusActivity extends BaseActivity implements ActionSheet.ActionSheetListener{

    @Bind(R.id.traffic_jam_reason)
    TextView trafficJamReason;
    @Bind(R.id.traffic_jam_status)
    TextView trafficJamStatus;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.empty_text)
    TextView empty;
    private List<TrafficJam> dataList;
    private QuickAdapter<TrafficJam> mAdapter;
    private LocationClient locationClient;
    private MyLocationListener locationListener;
    private double myLatitude,myLontitude;
    private String myAddress,myCity,reason,status;
    private MaterialDialog dialog;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_broadcast_status;
    }

    @Override
    public void initView() {
        setTitle("路况播报");
        dialog = new MaterialDialog(this)
                .setTitle("温馨提醒")
                .setMessage("您发布的路况信息中包含部分个人信息,请勿发布虚假路况信息")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        showWaitDialog();
                        submit();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void initData() {
        showWaitDialog();
        locationClient = new LocationClient(this);
        locationListener = new MyLocationListener();
        locationClient.registerLocationListener(locationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        locationClient.setLocOption(option);
        String city = getIntent().getStringExtra("city");
        if (city!=null){
            Map params = new HashMap();
            params.put("city",city);
            JsonRequestWithAuth<AjaxResponse> getTrafficJamInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.GET_ALL_TRAFFIC_INFO_BY_CITY, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
                @Override
                public void onResponse(AjaxResponse response) {
                    Gson gson = new Gson();
                    dataList = gson.fromJson(gson.toJson(response.getResponseData().get("allTrafficJamInfo")),new TypeToken<List<TrafficJam>>(){}.getType());
                    if (dataList!=null&&!dataList.isEmpty()){
                        if (mAdapter==null) {
                            mAdapter = new QuickAdapter<TrafficJam>(BroadcastStatusActivity.this, R.layout.item_road_status, dataList) {
                                @Override
                                protected void convert(BaseAdapterHelper helper, TrafficJam item) {
                                    helper.setText(R.id.address, item.getAddress())
                                            .setText(R.id.reason, item.getJamReason())
                                            .setText(R.id.status, item.getJamStatus());
                                }
                            };
                        }else {
                            mAdapter.replaceAll(dataList);
                        }
                        listView.setAdapter(mAdapter);
                    }else {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            }, params, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(BroadcastStatusActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
            MyApplication.getHttpQueues().add(getTrafficJamInfo);
            MyApplication.getHttpQueues().start();
        }
    }

    @Override
    @OnClick({R.id.item1,R.id.item2,R.id.iv_title_back,R.id.submit})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.item1:
               actionSheet(getResources().getStringArray(R.array.traffic_jam_reason),"reason");
                break;
            case R.id.item2:
               actionSheet(getResources().getStringArray(R.array.traffic_jam_status),"status");
                break;
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.submit:
                if (check())
                    dialog.show();
                break;
        }
    }

    public void actionSheet(String []item,String tag){
        ActionSheet.createBuilder(this,getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(item)
                .setTag(tag)
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        actionSheet.dismiss();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        String tag = actionSheet.getTag();
        if ("reason".equals(tag)){
            trafficJamReason.setText(getResources().getStringArray(R.array.traffic_jam_reason)[index]);
        }else if ("status".equals(tag)){
            trafficJamStatus.setText(getResources().getStringArray(R.array.traffic_jam_status)[index]);
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            myLatitude = bdLocation.getLatitude();
            myLontitude = bdLocation.getLongitude();
            myAddress = bdLocation.getAddrStr();
            myCity = bdLocation.getCity();
        }
    }

    public boolean check(){
        reason = trafficJamReason.getText().toString().trim();
        status = trafficJamStatus.getText().toString().trim();
        if ("".equals(reason)){
            Toast.makeText(this,"请选择堵车原因",Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("".equals(status)){
            Toast.makeText(this,"请选择堵车状况",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (myCity==null){
            Toast.makeText(this,"定位失败,请检查定位功能",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void submit(){
        showWaitDialog();
        Map params = new HashMap();
        params.put("latitude",myLatitude+"");
        params.put("lontitude",myLontitude+"");
        params.put("address",myAddress);
        params.put("jamReason",reason);
        params.put("jamStatus",status);
        params.put("userId", MyApplication.getCurrentUser().getId());
        params.put("city",myCity);
        JsonRequestWithAuth<AjaxResponse> addTrafficJam = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.ADD_TRAFFIC_JAM_INFO, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
            @Override
            public void onResponse(AjaxResponse response) {
                hideWaitDialog();
                System.out.println(response.getResponseData().get("id")+"+++++++");
            }
        }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitDialog();
            }
        });
        MyApplication.getHttpQueues().add(addTrafficJam);
        MyApplication.getHttpQueues().start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!locationClient.isStarted()){
            locationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient!=null){
            locationClient.stop();
            locationClient=null;
        }
        MyApplication.getHttpQueues().cancelAll("addTrafficJam");
        MyApplication.getHttpQueues().cancelAll("getTrafficJamInfo");
    }
}