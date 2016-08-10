package car.ccut.com.vehicle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.A;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tasomaniac.android.widget.DelayedProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.adapter.BaseAdapterHelper;
import car.ccut.com.vehicle.adapter.QuickAdapter;
import car.ccut.com.vehicle.base.BaseFragment;
import car.ccut.com.vehicle.bean.TrafficJam;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.view.MultiLineRadioGroup;
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
 * Created by WangXin on 2016/7/28 0028.
 */
public class BroadcastFragment extends BaseFragment{
    private List<TrafficJam> dataList;
    private QuickAdapter<TrafficJam> mAdapter;
    private LocationClient locationClient;
    private MyLocationListener locationListener;
    private double myLatitude,myLontitude;
    private String myAddress,myCity,reason,status;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.traffic_jam_reason)
    MultiLineRadioGroup trafficJamReason;
    @Bind(R.id.traffic_jam_status)
    MultiLineRadioGroup trafficJamStatus;
    private MaterialDialog dialog;
    private Gson gson = new Gson();
    private JsonRequestWithAuth<AjaxResponse> getTrafficJamInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWaitDialog();
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_broadcast,container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {
        super.initView(view);
        dialog = new MaterialDialog(getActivity())
                .setTitle("温馨提醒")
                .setMessage("您发布的路况信息中包含部分个人信息,请勿发布虚假路况信息")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
        locationClient = new LocationClient(getActivity());
        locationListener = new MyLocationListener();
        locationClient.registerLocationListener(locationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        locationClient.setLocOption(option);
    }

    @Override
    @OnClick({R.id.submit})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.submit:
                if (check())
                    dialog.show();
                break;
        }
    }

    public boolean check(){
        if (trafficJamReason.getCheckedValues().isEmpty()){
            Toast.makeText(getActivity(),"请选择拥堵原因",Toast.LENGTH_SHORT).show();
            return false;
        }else
            reason = trafficJamReason.getCheckedValues().get(0);
        if (trafficJamStatus.getCheckedValues().isEmpty()){
            Toast.makeText(getContext(),"请选择拥堵状况",Toast.LENGTH_SHORT).show();
            return false;
        }else
            status = trafficJamStatus.getCheckedValues().get(0);
        return true;
    }

    public void submit(){
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
//                hideWaitDialog();
                dialog.dismiss();
                System.out.println(response.getReturnMsg());
            }
        }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                hideWaitDialog();
            }
        });
        MyApplication.getHttpQueues().add(addTrafficJam);
        MyApplication.getHttpQueues().start();
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            myLatitude = bdLocation.getLatitude();
            myLontitude = bdLocation.getLongitude();
            myAddress = bdLocation.getAddrStr();
            myCity = bdLocation.getCity();
            Map params = new HashMap();
            params.put("city",myCity);
            getTrafficJamInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.GET_ALL_TRAFFIC_INFO_BY_CITY, AjaxResponse.class, new Response.Listener<AjaxResponse>() {
                @Override
                public void onResponse(AjaxResponse response) {
                    dataList = gson.fromJson(gson.toJson(response.getResponseData().get("allTrafficJamInfo")),new TypeToken<List<TrafficJam>>(){}.getType());
                    if (dataList!=null&&!dataList.isEmpty()){
                        if (mAdapter==null) {
                            mAdapter = new QuickAdapter<TrafficJam>(getActivity(), R.layout.item_road_status, dataList) {
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
                    }
                    listView.setAdapter(mAdapter);
                }
            }, params, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
            MyApplication.getHttpQueues().add(getTrafficJamInfo);
            MyApplication.getHttpQueues().start();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!locationClient.isStarted()){
            locationClient.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        locationClient.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationClient.isStarted()){
            locationClient.stop();
            locationClient = null;
            MyApplication.getHttpQueues().cancelAll("getTrafficJamInfo");
            MyApplication.getHttpQueues().cancelAll("addTrafficJam");
        }
    }
}
