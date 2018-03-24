package com.view.mark.myprogressbar;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.view.mark.myprogressbar.view.HorizontalProgressbarWithProgress;

/**
 * Mark先生的自定义的进度条
 */
public class MainActivity extends AppCompatActivity {
    private static final int MSG_UPDATE = 0x110;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int progress = progressbarWithProgress1.getProgress();
            progressbarWithProgress1.setProgress(++progress);
            if (progress >= 100){
                mHandler.removeMessages(MSG_UPDATE);
            }
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE,100);
        }
    };

    private HorizontalProgressbarWithProgress progressbarWithProgress1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intView();
    }

    private void intView() {
        progressbarWithProgress1 = findViewById(R.id.pgone);
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }
}
