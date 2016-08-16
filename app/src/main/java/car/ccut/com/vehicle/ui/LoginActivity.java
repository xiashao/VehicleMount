package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tasomaniac.android.widget.DelayedProgressDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import car.ccut.com.vehicle.MyApplication;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.bean.User;
import car.ccut.com.vehicle.bean.car.CarInfo;
import car.ccut.com.vehicle.bean.net.AjaxResponse;
import car.ccut.com.vehicle.interf.ConstantValue;
import car.ccut.com.vehicle.network.JsonRequestWithAuth;
import car.ccut.com.vehicle.util.MD5;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

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
 * Created by WangXin on 2016/3/10 0010.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, android.os.Handler.Callback {
    @Bind(R.id.user_name)
    EditText userName;
    @Bind(R.id.password)
    EditText password;
    private SharedPreferences myPreferences;
    private String useName;
    private String psd;
    private List<CarInfo> carInfos;
    private Intent it;
    public DelayedProgressDialog dialog;
    private User user;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private boolean isSetTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Intent i=getIntent();
        String phString=i.getStringExtra("phone");
        userName.setText(phString);
        myPreferences = getSharedPreferences("login", LoginActivity.MODE_PRIVATE);
        dialog = DelayedProgressDialog.make(this,null,"登陆中,请稍后..",true,true);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
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

    private void initData() {
        useName = myPreferences.getString("username", "");
        psd = myPreferences.getString("password","");
        if (!useName.isEmpty()&&!psd.isEmpty()){
            dialog.show();
        }
        locationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(0);
        locationClient.setLocOption(option);
    }

    private void requestCarInfo(String userId){
        Map params = new HashMap();
        params.put("userId",userId);
        JsonRequestWithAuth<AjaxResponse> requestCarInfo = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.REQUEST_ALL_CAR_BASE_INFO, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        if (response.isOk()){
                            dialog.hide();
                            Gson gson = new Gson();
                            carInfos = gson.fromJson(gson.toJson(response.getResponseData().get("carInfoList")),new TypeToken<List<CarInfo>>(){}.getType());
                            intent();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(requestCarInfo);
        MyApplication.getHttpQueues().start();
    }

    private void initView() {

    }

    @Override
    @OnClick({R.id.login_btn,R.id.login_register,R.id.login_forget_password})
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.login_btn:
                if (!isSetTag){
                    return;
                }
                useName=userName.getText().toString().trim();
                psd= password.getText().toString().trim();
                if (TextUtils.isEmpty(useName)||TextUtils.isEmpty(psd)){
                    Toast.makeText(this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
                }else {
                    login(useName,psd);
                }
            /*    Intent it10=new Intent(this,MapActivity.class);
                startActivity(it10);*/
                break;
            case R.id.login_register:
                it = new Intent(this,VerifyTelActivity.class);
            /*    it = new Intent(this,RegisterBaseInfoActivity.class);*/
                it.putExtra("identification","register");
                startActivity(it);
                break;
            case R.id.login_forget_password:
                Intent it1=new Intent(this,VerifyTelActivity.class);
                it1.putExtra("identification","forget");
                startActivity(it1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (locationClient.isStarted()){
            locationClient.stop();
        }
        locationClient=null;
        MyApplication.getHttpQueues().cancelAll("loginRequest");
        MyApplication.getHttpQueues().cancelAll("requestCarInfo");
    }

    private void login(final String username, final String password){
        if (!dialog.isShowing()){
            dialog.show();
        }
        Map params = new HashMap();
        params.put("username",username);
        System.out.println(username);
        params.put("password",MD5.getMd5(password));
        System.out.println(MD5.getMd5(password));
        JsonRequestWithAuth<AjaxResponse> loginRequest = new JsonRequestWithAuth<AjaxResponse>(ConstantValue.REQUEST_LOGIN, AjaxResponse.class,
                new Response.Listener<AjaxResponse>() {
                    @Override
                    public void onResponse(AjaxResponse response) {
                        if (response.isOk()){
                            Gson gson = new Gson();
                            user = gson.fromJson(response.getResponseData().get("user").toString(),User.class);
                            if (user!=null){
                                MyApplication.setCurrentUser(user);
                                requestCarInfo(user.getId());
                            }
                            SharedPreferences.Editor editor = myPreferences.edit();
                            editor.putString("username",username);
                            editor.putString("password",password);
                            editor.commit();
                        }else {
                            System.out.println(response.getResponseStatus());
                            Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }, params, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getHttpQueues().add(loginRequest);
        MyApplication.getHttpQueues().start();
    }

    private void intent(){
        if (carInfos!=null&&carInfos.size()>0){
            it = new Intent(this,MapActivity.class);
            it.putExtra("type","type");
            MyApplication.setCurrentServerCar(carInfos.get(0));
        }else {
            it = new Intent(this,AddCarActivity.class);
        }
        dialog.dismiss();
        startActivity(it);
        finish();
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String tag = bdLocation.getCity();
            if (tag==null||tag.equals("")){
                return;
            }
            Set<String> tags = new HashSet<String>();
            tags.add(tag);
            JPushInterface.setTags(LoginActivity.this, tags, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> tags) {
                    System.out.println("标签是:"+tags.toString());
                    isSetTag = true;
                    if (!useName.isEmpty()&&!psd.isEmpty()){
                        userName.setText(useName);
                        password.setText(psd);
                        login(useName,psd);
                    }
                }
            });
        }
    }
}
