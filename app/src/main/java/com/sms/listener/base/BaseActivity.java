package com.sms.listener.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by xuzhou on 2018/10/29.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public static final String BUNDLE = "bundle";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        handleIntent(getIntent());
        addFragmentToActivity();
        init();
    }


    protected void handleIntent(Intent intent) {

    }

    protected void init() {

    }

    private void addFragmentToActivity() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContentId());
        if (fragment == null) {
            fragment = getContentFragment();
            if (fragment != null) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(getFragmentContentId(), fragment, null);
                transaction.commitNowAllowingStateLoss();
            }
        }
    }

    /**
     * 初始化系统状态栏
     * 在4.4以上版本是否设置透明状态栏
     */
    private void initStatusBar() {
        if (!setTranslucentStatusBar()) {
            return;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    /**
     * 设置透明状态栏
     *
     * @return true 设置, false 不设置, 默认为false
     */
    protected boolean setTranslucentStatusBar() {
        return false;
    }

    public abstract int getLayoutId();

    public abstract Fragment getContentFragment();

    public abstract int getFragmentContentId();

}
