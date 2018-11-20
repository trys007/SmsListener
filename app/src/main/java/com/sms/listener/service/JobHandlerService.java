package com.sms.listener.service;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;

/**
 * Created by xuzhou on 2018/9/4.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobHandlerService extends JobService {

    public static final String BASE_SERVICE_TAG = "com.sms.listener.service.BaseService";
    public static final String REMOTE_SERVICE_TAG = "com.sms.listener.service.RemoteService";
    private JobScheduler mJobScheduler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(startId++,
                    new ComponentName(getPackageName(), JobHandlerService.class.getName()));

            builder.setPeriodic(5000); //每隔5秒运行一次
            builder.setRequiresCharging(true);
            builder.setPersisted(true);  //设置设备重启后，是否重新执行任务
            builder.setRequiresDeviceIdle(true);

            if (JobScheduler.RESULT_FAILURE >= mJobScheduler.schedule(builder.build())) {
                //If something goes wrong
                System.out.println("工作失败");
            } else {
                System.out.println("工作成功");
            }
        }
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        startService();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        startService();
        return true;
    }

    private void startService() {
        if (!isServiceRunning(BASE_SERVICE_TAG) || !isServiceRunning(REMOTE_SERVICE_TAG)) {
            startService(new Intent(this, BaseService.class));
            startService(new Intent(this, RemoteService.class));
        }
    }

    // 服务是否运行
    public boolean isServiceRunning(String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : lists) {// 获取运行服务再启动
            System.out.println(info.processName);
            if (info.processName.equals(serviceName)) {
                isRunning = true;
            }
        }
        return isRunning;

    }

}
