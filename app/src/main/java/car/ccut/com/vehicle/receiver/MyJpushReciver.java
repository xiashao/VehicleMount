package car.ccut.com.vehicle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;

import car.ccut.com.vehicle.MyApplication;
import cn.jpush.android.api.JPushInterface;

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
 * Created by WangXin on 2016/7/17 0017.
 */
public class MyJpushReciver extends BroadcastReceiver {
    private SynthesizerListener mTtsListener;
    private SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(MyApplication.getContext(),null);
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

         if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
             String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
             System.out.println(message+"++++++");
             startTts("实时,"+message);

        }
    }

    private void startTts(String text){
        int code = mTts.startSpeaking(text, (com.iflytek.cloud.SynthesizerListener) mTtsListener);
        if (code!= ErrorCode.SUCCESS){
            System.out.println(code);
        }
    }
}
