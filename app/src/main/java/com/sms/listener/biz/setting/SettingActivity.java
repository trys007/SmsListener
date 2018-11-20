package com.sms.listener.biz.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sms.listener.R;
import com.sms.listener.base.ToolbarActivity;

/**
 * Created by xuzhou on 2018/10/29.
 */

public class SettingActivity extends ToolbarActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_common_layout;
    }

    @Override
    public Fragment getContentFragment() {
        return SettingFragment.newInstance(null);
    }

    @Override
    public int getFragmentContentId() {
        return R.id.contentFrame;
    }

    @Override
    protected void init() {
        super.init();
        setTitle(getString(R.string.setting_page_title));
        showBackBtn(true);
    }

    public static Intent makeIntent(Context context, Bundle bundle) {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtra(BUNDLE, bundle);
        return intent;
    }

}
