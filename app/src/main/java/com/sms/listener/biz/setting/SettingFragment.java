package com.sms.listener.biz.setting;

import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sms.listener.R;
import com.sms.listener.base.BaseFragment;
import com.sms.listener.biz.db.Config;
import com.sms.listener.utils.TelephoneUtils;

import io.realm.Realm;

/**
 * Created by xuzhou on 2018/10/29.
 */

public class SettingFragment extends BaseFragment {

    private TextInputEditText phoneEt;
    private TextInputEditText urlEt;
    private TextInputEditText remarkEt;
    private TextView tipsTv;
    private Button confirmBtn;
    private Config mConfig;
    private String phone;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        phoneEt = view.findViewById(R.id.setting_phone_et);
        urlEt = view.findViewById(R.id.setting_url_et);
        remarkEt = view.findViewById(R.id.setting_remark_et);
        tipsTv = view.findViewById(R.id.setting_tips_tv);
        confirmBtn = view.findViewById(R.id.setting_confirm_btn);

        initData();

        initListener();

    }

    private void initData() {
        phone = TelephoneUtils.getLocalPhoneNumber(mActivity);
        mConfig = queryConfig();
        if(null != mConfig) {
            phoneEt.setText(mConfig.getLocalPhone());
            urlEt.setText(mConfig.getUrl());
            remarkEt.setText(mConfig.getRemark());
        }
    }

    private void initListener() {
        confirmBtn.setOnClickListener((v) ->
                saveConfig()
        );
        phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        urlEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString())) {
                    tipsTv.setText("");
                }
            }
        });
    }

    public static SettingFragment newInstance(Bundle bundle) {
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setArguments(bundle);
        return settingFragment;
    }

    private Config queryConfig() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Config.class).findFirst();
    }

    private void saveConfig() {
        if(TextUtils.isEmpty(phoneEt.getText().toString())) {
            tipsTv.setText("*本机号码不能为空");
            return;
        }
        if(!TextUtils.isEmpty(phone) && !phone.equals(phoneEt.getText().toString())) {
            tipsTv.setText("*本机号码输入有误，请重新输入");
            return;
        } else {
            tipsTv.setText("");
        }
        if(TextUtils.isEmpty(urlEt.getText().toString())) {
            tipsTv.setText("*请求地址不能为空");
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(null == mConfig) {
            mConfig = realm.createObject(Config.class);
        }
        mConfig.setLocalPhone(phoneEt.getText().toString());
        mConfig.setUrl(urlEt.getText().toString());
        mConfig.setRemark(remarkEt.getText().toString());
        realm.commitTransaction();
        Snackbar.make(phoneEt, "保存成功", Snackbar.LENGTH_LONG).setAction("确定", (v) -> mActivity.finish()).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                mActivity.finish();
            }
        }).show();
    }

}
