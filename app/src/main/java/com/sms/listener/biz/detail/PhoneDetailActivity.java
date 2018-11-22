package com.sms.listener.biz.detail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


import com.sms.listener.R;
import com.sms.listener.base.ToolbarActivity;

public class PhoneDetailActivity extends ToolbarActivity {

    public static final String PHONE_DETAIL_TITLE = "phone_detail_title";
    private Bundle dataBundle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_layout;
    }

    @Override
    public Fragment getContentFragment() {
        return PhoneDetailFragment.newInstance(dataBundle);
    }

    @Override
    public int getFragmentContentId() {
        return R.id.contentFrame;
    }

    @Override
    protected void init() {
        super.init();
        setTitle(null == dataBundle ? "" : dataBundle.getString(PHONE_DETAIL_TITLE, ""));
        showRightTv(false);
        showBackBtn(true);
    }

    public static Intent makeIntent(Context context, Bundle bundle) {
        Intent intent = new Intent(context, PhoneDetailActivity.class);
        intent.putExtra(BUNDLE, bundle);
        return intent;
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        if(null != intent) {
            dataBundle = intent.getBundleExtra(BUNDLE);
        }
    }
}
