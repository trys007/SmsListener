package com.sms.listener.biz.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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

public class MainFragment extends BaseFragment {

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    RecyclerView recyclerView;
    SmsAdapter smsAdapter;
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

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    private void initData() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                new String[] {
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                }
        );
        if(!isAllGranted) {
            /**
             * 第 2 步: 请求权限
             * 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
             */
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[] {
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS,
                    },
                    MY_PERMISSION_REQUEST_CODE
            );
        } else {
            dataList = getSmsFromPhone();
        }
        if(null == dataList || dataList.size() <= 0) {
            dataList = new ArrayList<>();
            dataList.add(new SmsMessage(-1, "", "Sms Listener", System.currentTimeMillis()));
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        smsAdapter = new SmsAdapter(dataList, mActivity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(smsAdapter);
    }

    public List<SmsMessage> getSmsFromPhone() {
        List<SmsMessage> smsMessageList = null;
        ContentResolver cr = mActivity.getContentResolver();
        String[] projection = new String[] { "_id", "body", "address", "date"};//"_id", "address", "person",, "date", "type
        String where = " date >  " + (System.currentTimeMillis() - 1000 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur)
            return null;
        if (cur.moveToFirst()) {
            smsMessageList = new ArrayList<>();
            while(cur.moveToNext()) {
                int id = cur.getInt(cur.getColumnIndex("_id"));
                String number = cur.getString(cur.getColumnIndex("address"));//手机号
                String body = cur.getString(cur.getColumnIndex("body"));
                long date = cur.getLong(cur.getColumnIndex("date"));
                SmsMessage smsMessage = new SmsMessage(id, number, body, date);
                smsMessageList.add(smsMessage);
            }
        }
        return smsMessageList;
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                dataList = getSmsFromPhone();
                if(null == smsAdapter) {
                    initRecyclerView();
                }
                smsAdapter.notifyDataSetChanged();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("备份通讯录需要访问 “通讯录” 和 “外部存储器”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHolder> {

        private List<SmsMessage> smsMessageList;
        private Context context;
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public SmsAdapter(List<SmsMessage> list, Context context) {
            this.smsMessageList = list;
            this.context = context;
        }

        @Override
        public SmsAdapter.SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SmsViewHolder holder = new SmsViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_recycler_item, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(SmsAdapter.SmsViewHolder holder, int position) {
            holder.smsPhoneTv.setText(smsMessageList.get(position).getSmsPhone());
            holder.smsBodyTv.setText(smsMessageList.get(position).getSmsBody());
            holder.smsDateTv.setText(simpleDateFormat.format(new Date(smsMessageList.get(position).getDate())));
        }

        @Override
        public int getItemCount() {
            return smsMessageList.size();
        }

        public class SmsViewHolder extends RecyclerView.ViewHolder {

            public TextView smsPhoneTv;
            public TextView smsBodyTv;
            public TextView smsDateTv;

            public SmsViewHolder(View itemView) {
                super(itemView);
                smsPhoneTv = itemView.findViewById(R.id.sms_phone);
                smsBodyTv = itemView.findViewById(R.id.sms_body);
                smsDateTv = itemView.findViewById(R.id.sms_date);
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
