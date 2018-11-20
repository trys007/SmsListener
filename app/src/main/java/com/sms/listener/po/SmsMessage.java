package com.sms.listener.po;

/**
 * Created by xuzhou on 2018/6/27.
 */

public class SmsMessage {

    private int smsId;
    private String smsPhone;
    private String smsBody;
    private long date;

    public SmsMessage(int smsId, String smsPhone, String smsBody, long date) {
        this.smsId = smsId;
        this.smsPhone = smsPhone;
        this.smsBody = smsBody;
        this.date = date;
    }

    public int getSmsId() {
        return smsId;
    }

    public void setSmsId(int smsId) {
        this.smsId = smsId;
    }

    public String getSmsPhone() {
        return smsPhone;
    }

    public void setSmsPhone(String smsPhone) {
        this.smsPhone = smsPhone;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
