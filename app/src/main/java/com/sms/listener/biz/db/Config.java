package com.sms.listener.biz.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Config extends RealmObject {

    private String localPhone;

    private String url;

    private String remark;

    public String getLocalPhone() {
        return localPhone;
    }

    public void setLocalPhone(String localPhone) {
        this.localPhone = localPhone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
