package car.ccut.com.vehicle.service;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import car.ccut.com.vehicle.R;
import car.ccut.com.vehicle.ui.DrivingActivity;
import car.ccut.com.vehicle.ui.HighspeeedActivity;
import car.ccut.com.vehicle.ui.HomeActivity;
import car.ccut.com.vehicle.ui.TrafficJamModeActivity;


public class FloatWindowService extends Service implements View.OnClickListener {

    //定义浮动窗口布局
    public LinearLayout float_window_small;
    private RelativeLayout float_window_menu;
    WindowManager.LayoutParams wmParams;
    WindowManager.LayoutParams menuParams;
    private LinearLayout top,left,right,bottom;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Button floatwindowbutton;
    private boolean clickflag;
    public int system_version;
    public int sdk_version;



    Handler handler = new Handler(){
      @Override
    public void handleMessage(Message msg){
          switch (msg.what){
              case 14:
                  float_window_menu.setVisibility(View.VISIBLE);
                  float_window_small.setVisibility(View.GONE);
                  break;
              case 15:
                  float_window_small.setVisibility(View.VISIBLE);
                  float_window_menu.setVisibility(View.GONE);
                  break;
              case 111:
                  floatwindowbutton.setAlpha(0.3f);
                  break;
              case 112:
                  floatwindowbutton.setAlpha(1.0f);
              default:
                  //Toast.makeText(getApplication(),"Mistake happened!",Toast.LENGTH_SHORT).show();
                  break;
          }
          //Log.i("msg.!!!!what", String.valueOf(msg.what));
      }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        //Log.i("system",android.os.Build.MODEL+","+android.os.Build.VERSION.SDK+","+android.os.Build.VERSION.RELEASE);
        String temp[] = Build.VERSION.RELEASE.split("\\.");//.是转义字符，要加上//
        int system = Integer.parseInt(temp[0]);
        system_version = system;
        sdk_version = Build.VERSION.SDK_INT;
    }

    public void setbutton2view(){
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        float_window_menu = (RelativeLayout)inflater.inflate(R.layout.float_window_menu, null);
        float_window_small = (LinearLayout) inflater.inflate(R.layout.float_window, null);
        floatwindowbutton = (Button)float_window_small.findViewById(R.id.floatwindowbutton);
        top = (LinearLayout) float_window_menu.findViewById(R.id.top);
        top.setOnClickListener(this);
        left = (LinearLayout) float_window_menu.findViewById(R.id.left);
        left.setOnClickListener(this);
        right = (LinearLayout) float_window_menu.findViewById(R.id.right);
        right.setOnClickListener(this);
        bottom = (LinearLayout) float_window_menu.findViewById(R.id.bottom);
        bottom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.top:
                intent = new Intent(getBaseContext(), HighspeeedActivity.class);
                break;
            case R.id.left:
                intent = new Intent(getBaseContext(), TrafficJamModeActivity.class);
                break;
            case R.id.right:
                intent = new Intent(getBaseContext(), HomeActivity.class);
                break;
            case R.id.bottom:
                intent = new Intent(getBaseContext(), DrivingActivity.class);
                break;
            default:
                break;
        }
        try {
            float_window_menu_animation(-1);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId){
        flags = START_STICKY;
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //访问service提供的服务
        setbutton2view();//设置button绑定view
        float_window_menu.setVisibility(View.GONE);
        create_float_window();
        create_float_windowmenu();

        float_window_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    float_window_menu_animation(-1);
                    //stopinitButtonSetting(timer,timerTask);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        floatwindowbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickflag == true) {//不是onTouch事件
                    try {
                        float_window_menu_animation(1);
                        //timer = initButtonSetting(timer,timerTask);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        floatwindowbutton.setOnTouchListener(new View.OnTouchListener() {
            int lastx, lasty;
            int downx, downy;

            @Override
            public boolean onTouch(View v, final MotionEvent event) {

                ObjectAnimator objectAnimator = new ObjectAnimator();
                objectAnimator = ObjectAnimator.ofFloat(floatwindowbutton,"Alpha",1.0f,1.0f);
                objectAnimator.setDuration(2000);
                objectAnimator.start();
                Message message = new Message();
                message.what = 111;
                handler.sendMessageDelayed(message,2100);

                // TODO Auto-generated method stub
                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        lastx = (int) event.getRawX();
                        lasty = (int) event.getRawY();
                        downx = lastx;
                        downy = lasty;
                        clickflag=true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs((int) (event.getRawX() - downx)) > 3 || Math.abs((int) (event.getRawY() - downy)) > 3) {
                            clickflag = false;
                        } else {
                            clickflag = true;
                        }
                        if (clickflag == false) {
                            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            wmParams.x = (int) event.getRawX() - floatwindowbutton.getMeasuredWidth() / 2;
                            //减25为状态栏的高度
                            wmParams.y = (int) event.getRawY() - floatwindowbutton.getMeasuredHeight() / 2 - 25;
                            //刷新
                            mWindowManager.updateViewLayout(float_window_small, wmParams);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        return super.onStartCommand(intent,flags,startId);
    }


    private int dp2pix(int dp){
       float scale = getResources().getDisplayMetrics().density;
        int pix = (int) (dp * scale + 0.5f);
        return pix;
    }


    public void create_float_window(){
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.TRANSLUCENT;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        wmParams.x = screenWidth / 2-dp2pix(48) / 2;
        wmParams.y = dp2pix(117)-dp2pix(48) / 2;
        //设置悬浮窗口长宽数据
        //wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.width = dp2pix(48);
        wmParams.height = dp2pix(48);
        smallfloatbutton_animation();
        mWindowManager.addView(float_window_small, wmParams);
    }

    public void create_float_windowmenu(){
        menuParams = new WindowManager.LayoutParams();
        menuParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        menuParams.format = PixelFormat.TRANSLUCENT;
        menuParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        menuParams.gravity = Gravity.LEFT | Gravity.TOP;
        menuParams.x = 0;
        menuParams.y = 0;
        menuParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        menuParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowManager.addView(float_window_menu, menuParams);
    }


    private void smallfloatbutton_animation(){
        ObjectAnimator objectAnimator;
        objectAnimator = ObjectAnimator.ofFloat(floatwindowbutton, "alpha", 0.0f, 1.0f);
        objectAnimator.setDuration(1500);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        ObjectAnimator objectAnimator1;
        objectAnimator1 = ObjectAnimator.ofFloat(floatwindowbutton,"scaleX",2.0f,1.0f);
        objectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator1.setDuration(1500);
        objectAnimator1.start();
        ObjectAnimator objectAnimator2;
        objectAnimator2 = ObjectAnimator.ofFloat(floatwindowbutton, "scaleY", 2.0f, 1.0f);
        objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator2.setDuration(1500);
        objectAnimator2.start();
        Message msg = new Message();
        msg.what = 111;
        handler.sendMessageDelayed(msg, 4500);
    }

    public void float_window_menu_animation(int i) throws InterruptedException {
        if(i == 1){//出现
            Message msg = new Message();
            msg.what = 14;
            handler.sendMessage(msg);
            ObjectAnimator objectAnimator1;
            LinearLayout testlaout = (LinearLayout)float_window_menu.findViewById(R.id.float_window_menu_smallmenu);
            objectAnimator1 = ObjectAnimator.ofFloat(testlaout,"scaleX",0.0f,1.0f);
            objectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator1.setDuration(100);
            objectAnimator1.start();
            ObjectAnimator objectAnimator2;
            objectAnimator2 = ObjectAnimator.ofFloat(testlaout, "scaleY", 0.0f, 1.0f);
            objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator2.setDuration(100);
            objectAnimator2.start();
            int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
            int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
            ObjectAnimator objectAnimator3;
            int[] location = new int[2];
            float_window_small.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            objectAnimator3 = ObjectAnimator.ofFloat(testlaout,"X",wmParams.x-dp2pix(279)/2+dp2pix(24),(screenWidth-dp2pix(279))/2);
            Log.i("wmParamsX", String.valueOf(wmParams.x));
            objectAnimator3.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator3.start();
            objectAnimator3.setDuration(100);
            ObjectAnimator objectAnimator4;
            objectAnimator4 = ObjectAnimator.ofFloat(testlaout,"Y",wmParams.y-dp2pix(279)/2+dp2pix(24),(screenHeight-dp2pix(279))/2);
            Log.i("wmParamsY", String.valueOf(wmParams.y));
            objectAnimator4.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator4.setDuration(100);
            objectAnimator4.start();
        }
        else{
            ObjectAnimator objectAnimator1;
            LinearLayout testlaout = (LinearLayout)float_window_menu.findViewById(R.id.float_window_menu_smallmenu);
            objectAnimator1 = ObjectAnimator.ofFloat(testlaout,"scaleX",1.0f,0.0f);
            objectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator1.setDuration(100);
            objectAnimator1.start();
            ObjectAnimator objectAnimator2;
            objectAnimator2 = ObjectAnimator.ofFloat(testlaout, "scaleY", 1.0f, 0.0f);
            objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator2.setDuration(100);
            objectAnimator2.start();
            ObjectAnimator objectAnimator3;
            int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
            int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
            objectAnimator3 = ObjectAnimator.ofFloat(testlaout,"X",(screenWidth-dp2pix(279))/2,wmParams.x-dp2pix(279)/2+dp2pix(24));
            objectAnimator3.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator3.start();
            ObjectAnimator objectAnimator4;
            objectAnimator4 = ObjectAnimator.ofFloat(testlaout,"Y",(screenHeight-dp2pix(279))/2,wmParams.y-dp2pix(279)/2+dp2pix(24));
            objectAnimator4.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator4.start();
            Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = 15;
                    handler.sendMessage(msg);
                }
            };
            timer.schedule(timerTask, 300);
            Message msg = new Message();
            msg.what = 111;
            handler.sendMessageDelayed(msg, 500);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(float_window_small != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(float_window_small);
        }
        if(float_window_menu != null) {
            mWindowManager.removeView(float_window_menu);
        }
    }

}
