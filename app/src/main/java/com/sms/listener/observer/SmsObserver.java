package com.sms.listener.observer;

import android.database.ContentObserver;
import android.os.Handler;

import static com.sms.listener.service.BaseService.NEW_MSG_WHAT;

/**
 * Created by xuzhou on 2018/9/7.
 */

public class SmsObserver extends ContentObserver {

    private Handler mHandler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(Handler handler) {
        super(handler);
        this.mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        mHandler.obtainMessage(NEW_MSG_WHAT).sendToTarget();
    }

}
