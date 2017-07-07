package com.yuqirong.listenwechatnotification;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {


    private Button mButton;
    private Intent mService;
    private boolean isRunning;
    private final int REQUEST_ACCESSIBILITY_SETTINGS = 0x01;
    private final int REQUEST_NOTIFICATION_LISTENER_SETTINGS = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.mButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 18) {
                    if (!WeChatNotificationListenerService.isEnabled(MainActivity.this)) {
                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        startActivityForResult(intent, REQUEST_NOTIFICATION_LISTENER_SETTINGS);
                    }
                } else {
                    if (!WeChatAccessibilityService.isAccessibilitySettingsOn(MainActivity.this)) {
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }else{
                        mService = new Intent(MainActivity.this, WeChatAccessibilityService.class);
                        MainActivity.this.startService(mService);
                    }
                }
                }
            }
        });
    }

    /**
     * 方法描述：判断某一Service是否正在运行
     *
     * @param context     上下文
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCESSIBILITY_SETTINGS) {
            if (!WeChatAccessibilityService.isAccessibilitySettingsOn(MainActivity.this)) {
                Toast.makeText(MainActivity.this, "权限未打开", Toast.LENGTH_LONG).show();
            } else {
                Log.i("WeChatAccessibility","1");
                mService = new Intent(MainActivity.this, WeChatAccessibilityService.class);
                isRunning = isServiceRunning(MainActivity.this, "com.yuqirong.listenwechatnotification.WeChatAccessibilityService");
            }
        } else if (requestCode == REQUEST_NOTIFICATION_LISTENER_SETTINGS) {
            if (!WeChatNotificationListenerService.isEnabled(MainActivity.this)) {
                Toast.makeText(MainActivity.this, "权限未打开", Toast.LENGTH_LONG).show();
            } else {
                mService = new Intent(MainActivity.this, WeChatNotificationListenerService.class);
                isRunning = isServiceRunning(MainActivity.this, "com.yuqirong.listenwechatnotification.WeChatNotificationListenerService");
            }
        }

        if (mService != null) {
            Log.i("WeChatAccessibility","2");
            MainActivity.this.startService(mService);
        }


    }
}
