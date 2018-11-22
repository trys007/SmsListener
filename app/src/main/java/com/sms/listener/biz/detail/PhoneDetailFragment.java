package com.sms.listener.biz.detail;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sms.listener.R;
import com.sms.listener.base.BaseFragment;
import com.sms.listener.po.SmsMessage;
import com.sms.listener.service.BaseService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xuzhou on 2018/10/29.
 */

public class PhoneDetailFragment extends BaseFragment {

    RecyclerView recyclerView;
    PhoneDetailAdapter phoneDetailAdapter;
    List<SmsMessage> dataList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
        initData();
    }

    public static PhoneDetailFragment newInstance(Bundle bundle) {
        PhoneDetailFragment mainFragment = new PhoneDetailFragment();
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    private void initData() {

        initRecyclerView();
    }

    private void initRecyclerView() {
        phoneDetailAdapter = new PhoneDetailAdapter(dataList, mActivity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(phoneDetailAdapter);
    }



    class PhoneDetailAdapter extends RecyclerView.Adapter<PhoneDetailAdapter.PhoneDetailViewHolder> {

        private List<SmsMessage> smsMessageList;
        private Context context;
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public PhoneDetailAdapter(List<SmsMessage> list, Context context) {
            this.smsMessageList = list;
            this.context = context;
        }

        @Override
        public PhoneDetailAdapter.PhoneDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PhoneDetailViewHolder holder = new PhoneDetailViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_phone_detail_item, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(PhoneDetailAdapter.PhoneDetailViewHolder holder, int position) {
            holder.phoneDetailDateTv.setText(simpleDateFormat.format(new Date(smsMessageList.get(position).getDate())));
            holder.phoneDetailBodyTv.setText(smsMessageList.get(position).getSmsBody());
            holder.phoneDetailTipsTv.setText("");
        }

        @Override
        public int getItemCount() {
            return smsMessageList.size();
        }

        public class PhoneDetailViewHolder extends RecyclerView.ViewHolder {

            public TextView phoneDetailDateTv;
            public TextView phoneDetailBodyTv;
            public TextView phoneDetailTipsTv;

            public PhoneDetailViewHolder(View itemView) {
                super(itemView);
                phoneDetailDateTv = itemView.findViewById(R.id.phone_detail_date);
                phoneDetailBodyTv = itemView.findViewById(R.id.phone_detail_body);
                phoneDetailTipsTv = itemView.findViewById(R.id.phone_detail_tips);
                itemView.setOnClickListener((v -> sendMsg()));
            }
        }
    }

    public void sendMsg() {
        Intent i = new Intent(mActivity, BaseService.class);
        i.putExtra("S_TYPE", "send");
        i.putExtra("SMS_CONTENT", "13611721323-254386");
        mActivity.startService(i);
    }

}
