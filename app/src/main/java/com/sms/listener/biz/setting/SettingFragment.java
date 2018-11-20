package com.sms.listener.biz.setting;

import android.os.Bundle;
import android.view.View;

import com.sms.listener.R;
import com.sms.listener.base.BaseFragment;

/**
 * Created by xuzhou on 2018/10/29.
 */

public class SettingFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    public static SettingFragment newInstance(Bundle bundle) {
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setArguments(bundle);
        return settingFragment;
    }

}
