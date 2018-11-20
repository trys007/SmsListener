package com.sms.listener.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.sms.listener.service.BaseService;

/**
 * Created by xuzhou on 2018/6/27.
 */

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Cursor cursor = null;
        try {
            if (SMS_RECEIVED.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    if (messages.length > 0) {
                        String content = messages[0].getMessageBody();
                        String sender = messages[0].getOriginatingAddress();
                        String smsToast = "监听到短信 : "
                                + sender + "\n'"
                                + content + "'";
                        Toast.makeText(context, smsToast, Toast.LENGTH_LONG)
                                .show();
                        Intent i = new Intent(context, BaseService.class);
                        i.putExtra("S_TYPE", "send");
                        i.putExtra("SMS_CONTENT", sender + "-" + content);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(i);
                        } else {
                            context.startService(i);
                        }
                    }
                }
            } else if("action.heart".equals(intent.getAction())) {
                Intent i = new Intent(context, BaseService.class);
                i.putExtra("S_TYPE", "start");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                } else {
                    context.startService(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}
