package com.sms.listener.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.sms.listener.IOnLiveService;

/**
 * Created by xuzhou on 2018/9/4.
 */

public class RemoteService extends Service {

    private RemoteBinder mRemoteBinder;
    private ServiceConnection mRemoteConnect;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if(null == mRemoteConnect) {
            mRemoteConnect = new RemoteConnection();
        }
        mRemoteBinder = new RemoteBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mRemoteBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intents = new Intent();
        intents.setClass(this, BaseService.class);
        bindService(intents, mRemoteConnect, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    static class RemoteBinder extends IOnLiveService.Stub {

        @Override
        public String getName() throws RemoteException {
            return "远程连接";
        }
    }

    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent intent = new Intent(RemoteService.this,
                    BaseService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                RemoteService.this.startForegroundService(intent);
            } else {
                RemoteService.this.startService(intent);
            }
            RemoteService.this.bindService(new Intent(RemoteService.this,
                    BaseService.class), mRemoteConnect, Context.BIND_IMPORTANT);
        }
    }

}
