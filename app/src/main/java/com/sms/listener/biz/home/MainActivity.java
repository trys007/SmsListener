package com.sms.listener.biz.home;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.TextView;

import com.sms.listener.R;
import com.sms.listener.base.ToolbarActivity;
import com.sms.listener.biz.setting.SettingActivity;
import com.sms.listener.service.BaseService;
import com.sms.listener.service.JobHandlerService;
import com.sms.listener.service.RemoteService;

public class MainActivity extends ToolbarActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_layout;
    }

    @Override
    public Fragment getContentFragment() {
        return MainFragment.newInstance(null);
    }

    @Override
    public int getFragmentContentId() {
        return R.id.contentFrame;
    }

    @Override
    protected void init() {
        super.init();
        setTitle(getString(R.string.main_page_title));
        setRightTv(getString(R.string.setting_page_title));
        showRightTv(true);
        showBackBtn(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            openJobService();
        } else {
            openTwoService();
        }
    }

    @Override
    protected void onRightTvClick(TextView view) {
        super.onRightTvClick(view);
        startActivity(SettingActivity.makeIntent(this, null));
    }

    private void startBaseService() {
        Intent intent = new Intent(this, BaseService.class);
        intent.putExtra("S_TYPE", "start");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void openTwoService() {
        startBaseService();
        Intent intent = new Intent(this, RemoteService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void openJobService() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, JobHandlerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void toHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}
