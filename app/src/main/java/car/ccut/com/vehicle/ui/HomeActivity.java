package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.fragment.HomeFragment1;
import car.ccut.com.vehicle.fragment.HomeFragment2;
import car.ccut.com.vehicle.service.FloatWindowService;
import car.ccut.com.vehicle.service.MusicService;
import car.ccut.com.vehicle.view.DragLayout;

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
 * Created by WangXin on 2016/5/11 0011.
 */
public class HomeActivity extends FragmentActivity {
    @Bind(R.id.drag_layout)
    DragLayout dragLayout;

    private HomeFragment1 fragment1;
    private HomeFragment2 fragment2;
    private boolean isBack;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isBack = false;
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.first, fragment1).add(R.id.second, fragment2)
                .commit();
        DragLayout.ShowNextPageNotifier nextPageNotifier = new DragLayout.ShowNextPageNotifier() {
            @Override
            public void onDragNext() {
                fragment2.initView();
            }
        };
        dragLayout.setNextPageListener(nextPageNotifier);

    }

    private void initData() {
        fragment1 = new HomeFragment1();
        fragment2 = new HomeFragment2();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
                    Intent intent1 = new Intent(this, MusicService.class);
                    stopService(intent1);
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

}
