package car.ccut.com.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.base.AppManager;
import car.ccut.com.vehicle.fragment.NewsFragment;
import car.ccut.com.vehicle.fragment.ShareTrafficFragment;
import car.ccut.com.vehicle.fragment.SootheMusicFragment;
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
public class TrafficJamModeActivity extends FragmentActivity{

    private FragmentTabHost mTabHost;
    private Class[] mFragments = new Class[] { NewsFragment.class,SootheMusicFragment.class,
            ShareTrafficFragment.class};
    private int[] mTabSelectors = new int[] { R.drawable.tab_news_bg, R.drawable.tab_music_bg,
            R.drawable.tab_share_bg };
    private String[] mTabText = new String[] { "新闻资讯","舒缓音乐","分享路况"};
    private String[] mTabSpecs = new String[] { "news", "music", "share" };

    private boolean isBack;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_traffic_jam);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        addTab();
    }

    private void addTab() {
        for (int i = 0; i < mFragments.length; i++) {
            View tab = getLayoutInflater().inflate(R.layout.tab_indicator, null);
            ImageView imageView = (ImageView) tab.findViewById(R.id.imageView1);
            imageView.setImageResource(mTabSelectors[i]);
            TextView textView = (TextView) tab.findViewById(R.id.textView1);
            textView.setText(mTabText[i]);
            mTabHost.addTab(mTabHost.newTabSpec(mTabSpecs[i]).setIndicator(tab), mFragments[i], null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //若当前不在主页，则先返回主页
                if (mTabHost.getCurrentTab() != 0) {
                    mTabHost.setCurrentTab(0);
                    return false;
                }
                // 双击返回桌面，默认返回true，调用finish()
                if (!isBack) {
                    isBack = true;
                    Toast.makeText(this, "再按一次返回键回到桌面", Toast.LENGTH_SHORT).show();
                    mTabHost.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            isBack = false;
                        }
                    }, 2000);
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
}
