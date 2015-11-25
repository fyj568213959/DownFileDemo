package com.broadway.downfile.downfiledemo.activity;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.broadway.downfile.downfiledemo.bean.DownloadInfo;
import com.broadway.downfile.downfiledemo.bean.GlobalParams;

import org.xutils.DbManager;
import org.xutils.HttpManager;
import org.xutils.common.Callback;
import org.xutils.db.DbManagerImpl;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * IntentService 总结:
 * 一启动方式
 * 1:Bind方式 如果是Activity用Bind启动IntentService,Activity挂掉，IntentService也会挂掉.IntentService会依次执行-->.构造函数,onCreate,onBind
 * 2:start方式 如果是start启动IntentService,IntentService会依次执行-->构造函数,onCreate,onStartCommand,onHandleIntent(新线程),onDestroy
 * (注意：执行完onHandleIntent()方法会自动Destroy)
 * 3.先Bind，后start的方式,执行完onHandleIntent()方法不会自动Destroy，当Activity调用unbindService()方法。IntentService会触发onUnbind方法。并且判断onHandleIntent方法是否执行完毕
 * 如果onHandleIntent方法执行完毕会直接调用onDestroy,没有执行完毕，会等着onHandleIntent()执行完毕之后再onDestroy.
 * 如果没有手动调用unbindService()，当activity,Destroy的时候系统会调用unbindService().
 */
public class DownloadService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String TAG = DownloadService.class.getSimpleName();
    private static final String ACTION_FOO = "com.broadway.downfile.downfiledemo.activity.action.FOO";
    private static final String ACTION_BAZ = "com.broadway.downfile.downfiledemo.activity.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.broadway.downfile.downfiledemo.activity.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.broadway.downfile.downfiledemo.activity.extra.PARAM2";

    public static final String ACTION_DOWNLOAD_START = "action.download.start"; // 开始下载
    public static final String ACTION_DOWNLOAD_PAUSE = "action.download.pause"; // 暂停下载
    public static final String ACTION_DOWNLOAD_CANCEL_WAIT = "action.download.cancel.wait"; // 取消等待
    public static final String ACTION_DOWNLOAD_STOP = "action.download.stop"; // 停止下载
    public static final String ACTION_DOWNLOAD_CHECK_ACTIVITY = "action.download.check.activity"; // Activity检查当前是否有任务正在下载

    private HashMap<String, DownloadInfo> mDownloadAppInfos = new HashMap<String, DownloadInfo>();
    private HashMap<String, Callback.Cancelable> mCancelables = new HashMap<>();

    private HttpManager mHttp;
    private DbManager mDb;

    private DownLoadListener mDownLoadListener;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        Log.i("abc", "Service---onCreate");
        super.onCreate();
        XUtilsManager xUtilsManager = XUtilsManager.getInstance(getApplicationContext());
        mHttp = xUtilsManager.getHttp();
        mDb = xUtilsManager.getDb();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("abc", "Service---onBind");
        return new DownloadServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("abc", "Service---onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i("abc", "Service---onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("abc", "Service---onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开始下载
     *
     * @param context
     * @param downloadInfo
     */
    public static void startActionDownloadInfo(Context context, DownloadInfo downloadInfo) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_START);
        intent.putExtra(GlobalParams.DOWNLOAD_APP_KEY, downloadInfo);
        context.startService(intent);
    }

    /**
     * 暂停
     * @param context
     * @param downloadInfo
     */
    public static void pauseActionDownloadInfo(Context context,DownloadInfo downloadInfo){
        Intent intent = new Intent(context,DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_PAUSE);
        intent.putExtra(GlobalParams.DOWNLOAD_APP_KEY, downloadInfo);
        context.startService(intent);
    }

    /**
     * 取消等待下载
     *
     * @param context
     * @param downloadInfo
     */
    public static void cancelActionWaitDownload(Context context, DownloadInfo downloadInfo) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(DownloadService.ACTION_DOWNLOAD_CANCEL_WAIT);
        intent.putExtra(GlobalParams.DOWNLOAD_APP_KEY, downloadInfo);
        context.startService(intent);
    }

    public static void callbackActionApp(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(DownloadService.ACTION_DOWNLOAD_CHECK_ACTIVITY);
        context.startService(intent);
    }

    public static void stopActionDownload(Context context){
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(DownloadService.ACTION_DOWNLOAD_STOP);
        context.startService(intent);
    }
    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * 绑定Service
     *
     * @param context
     * @param mServiceConnection
     */
    public static void bindService(Context context, ServiceConnection mServiceConnection) {
        //绑定Service
        Intent intent = new Intent(context, DownloadService.class);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
            DownloadInfo downloadInfo = (DownloadInfo) intent.getSerializableExtra(GlobalParams.DOWNLOAD_APP_KEY);
            if (downloadInfo != null) {
                mDownloadAppInfos.put(downloadInfo.getUrl(), downloadInfo);
            }
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_START.equals(action)) {
                startDownloadInfo(downloadInfo);
            } else if (ACTION_DOWNLOAD_PAUSE.equals(action)) {
                pauseDownloadInfo(downloadInfo);
            } else if (ACTION_DOWNLOAD_CANCEL_WAIT.equals(action)){
                cancleWaitDownloadInfo(downloadInfo);
            } else if(ACTION_DOWNLOAD_CHECK_ACTIVITY.equals(action)){
                callBackIsAppDownloading(true);
            }else if (ACTION_DOWNLOAD_STOP.equals(action)) {
                stopAllDownloadApp();
            }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void startDownloadInfo(final DownloadInfo downloadInfo) {
        String url = downloadInfo.getUrl();
        String path = Utils.getCachePath(getApplicationContext()) + downloadInfo.getName() + ".apk";
        downloadInfo.setPath(path);


        Log.i(TAG, "positon = "+downloadInfo.getPosition()+"   name = "+downloadInfo.getName()+"   url === " + url);

        RequestParams params = new RequestParams(url);
        params.setAutoResume(true); // 设置是否在下载是自动断点续传
        params.setCancelFast(true); // 是否可以被立即停止
        final Callback.Cancelable cancelable = mHttp.get(params, new Callback.ProgressCallback<File>() {

            @Override
            public void onStarted() {
                ////Log.i(TAG, "onStarted ............");
                downloadInfo.setStatus(GlobalParams.STATUS_START);
                if (mDownLoadListener != null) {
                    mDownLoadListener.onStarted(downloadInfo);
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.i(TAG, "onLoading ............current:"+current+"       total:"+total);
                downloadInfo.setStatus(GlobalParams.STATUS_DOWNLOADING);
                downloadInfo.setMax((int) total);
                downloadInfo.setProgress((int) current);
                if (mDownLoadListener != null) {
                    mDownLoadListener.onLoading(downloadInfo);
                }
            }

            @Override
            public void onWaiting() {
                //Log.i(TAG, "onWaiting ............");
                downloadInfo.setStatus(GlobalParams.STATUS_WAIT);
                if (mDownLoadListener != null) {
                    mDownLoadListener.onWaiting(downloadInfo);
                }
            }

            @Override
            public void onSuccess(File result) {
                //Log.i(TAG, "onSuccess ............");
                downloadInfo.setStatus(GlobalParams.STATUS_COMPLETE);
                if (mDownLoadListener != null) {
                    mDownLoadListener.onSuccess(downloadInfo);
                }
                replaceDownloadAppInfos();
                installApp(downloadInfo);
                if (!checkIsDownloading()) {
                    mDownLoadListener.stopDownloadService();
                }

            }

            @Override
            public void onFinished() {
                //Log.i(TAG, "onFinished ............");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Log.i(TAG, "onError ............");

                if (checkAppISOK(downloadInfo)){
                    if (Utils.isAppInstalled(getApplicationContext(), downloadInfo.getPackageName())) {
                        if (mDownLoadListener != null) {
                            mDownLoadListener.onSuccess(downloadInfo);
                        }
                        downloadInfo.setStatus(GlobalParams.STATUS_INSTALLED);
                    }else {
                        if (mDownLoadListener != null) {
                            mDownLoadListener.onSuccess(downloadInfo);
                        }
                        downloadInfo.setStatus(GlobalParams.STATUS_COMPLETE);
                    }
                }else{
                    downloadInfo.setStatus(GlobalParams.STATUS_DOWNLOAD_ERROR);
                    if (mDownLoadListener != null) {
                        mDownLoadListener.onError(downloadInfo);
                    }
                }



                replaceDownloadAppInfos();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Log.i(TAG, "onCancelled ............");
                downloadInfo.setStatus(GlobalParams.STATUS_PAUSE);
                if (mDownLoadListener != null) {
                    mDownLoadListener.onCancelled(downloadInfo);
                }
                replaceDownloadAppInfos();
            }

        });

        saveCancelable(url, cancelable);


    }

    /**
     * 暂停下载
     * @param downloadInfo
     */
    private void pauseDownloadInfo(DownloadInfo downloadInfo){
        Callback.Cancelable cancelable = mCancelables.get(downloadInfo.getUrl());
        if (cancelable != null && !cancelable.isCancelled()){
            cancelable.cancel();
        }
    }

    /**
     * 取消等待下载
     * @param downloadInfo
     */
    private void cancleWaitDownloadInfo(DownloadInfo downloadInfo){
        Iterator<Map.Entry<String, DownloadInfo>> downloadAppInfoIterator = mDownloadAppInfos.entrySet().iterator();
        while (downloadAppInfoIterator.hasNext()) {
            Map.Entry<String, DownloadInfo> entry = downloadAppInfoIterator.next();
            if (downloadInfo.getUrl().equals(entry.getKey())) {
                DownloadInfo entryValue = entry.getValue();
                entryValue.setStatus(GlobalParams.STATUS_PAUSE);
            }
        }

       Iterator<Map.Entry<String,Callback.Cancelable>> cancelableIterator =  mCancelables.entrySet().iterator();
        while (cancelableIterator.hasNext()){
            Map.Entry<String,Callback.Cancelable> entry = cancelableIterator.next();
            Callback.Cancelable cancelable = entry.getValue();
            if (downloadInfo.getUrl().equals(entry.getKey())){
                cancelable.cancel();
                cancelableIterator.remove();
            }
        }
    }

    /**
     * 安装app
     * @param downloadInfo
     */
    private void installApp(DownloadInfo downloadInfo){
        String path = downloadInfo.getPath();
        Utils.installApp(getApplicationContext(), new File(path));
    }

    public void callBackIsAppDownloading(boolean isActivity) {
        boolean isAppDownloading = checkIsDownloading();
        if (!isAppDownloading) {
            if (mDownLoadListener != null) {
                mDownLoadListener.stopDownloadService();
            }
        }

        if (isActivity) {
            Intent intent = new Intent();
            intent.setAction(GlobalParams.DOWNLOAD_APP_CHECK_ACTION);
            intent.putExtra(GlobalParams.DOWNLOAD_APP_CHECK_KEY, isAppDownloading);
            sendBroadcast(intent);
        }
    }

    /**
     * 停止下载
     */
    private void stopAllDownloadApp() {
        Iterator<Map.Entry<String,Callback.Cancelable>> iterator =  mCancelables.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,Callback.Cancelable> entry = iterator.next();
            Callback.Cancelable cancelable = entry.getValue();
            if (cancelable != null && !cancelable.isCancelled()) {
                cancelable.cancel();
            }
        }
        replaceDownloadAppInfos();
        Intent intent = new Intent();
        intent.setAction(GlobalParams.DOWNLOAD_APP_STOP_ACTION);
        sendBroadcast(intent);

        if (mDownLoadListener != null) {
            mDownLoadListener.stopDownloadService();
        }
    }

    /**
     * 检查文件是否已经下载
     */
    private boolean checkAppISOK(DownloadInfo downloadnfo) {
        String path = downloadnfo.getPath();
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);
            try {
                int available = fis.available();
                int max = downloadnfo.getMax();
                return available == max;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if (!Utils.listIsEmpty(packages)) {
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIsDownloading() {
        boolean isAppDownloading = false;
        Iterator<Map.Entry<String, DownloadInfo>> iterator = mDownloadAppInfos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DownloadInfo> entry = iterator.next();
            DownloadInfo downloadAppInfo = entry.getValue();
            if (downloadAppInfo != null) {
                if (GlobalParams.STATUS_DOWNLOADING == downloadAppInfo.getStatus()) {
                    isAppDownloading = true;
                }
            }
        }
        return isAppDownloading;
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 从数据库中查找
     *
     * @return
     */
    public List<DownloadInfo> findDownloadInfos() {
        List<DownloadInfo> downloadInfos = null;
        try {
            downloadInfos = mDb.findAll(DownloadInfo.class);
            //Log.i(TAG,"downloadInfos ========== "+downloadInfos.size());
        } catch (DbException e) {
            Log.i(TAG, "message ========== " + e.getMessage());
            e.printStackTrace();
        }

        return downloadInfos;
    }

    /**
     * 替换数据
     */
    public void replaceDownloadAppInfos() {
        Iterator<Map.Entry<String, DownloadInfo>> iterator = mDownloadAppInfos.entrySet().iterator();
        ArrayList<DownloadInfo> downloadInfos = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, DownloadInfo> entry = iterator.next();
            DownloadInfo value = entry.getValue();
            downloadInfos.add(value);
        }
        try {
            for (int i = 0; i < downloadInfos.size(); i++) {
                DownloadInfo downloadInfo = downloadInfos.get(i);
                mDb.deleteById(DownloadInfo.class, downloadInfo.getId());
                mDb.save(downloadInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void saveCancelable(String url, Callback.Cancelable cancelable) {
        if (cancelable != null) {
            mCancelables.put(url, cancelable);
        }
    }


    public class DownloadServiceBinder extends Binder {
        public DownloadService getDownloadService() {
            return DownloadService.this;
        }
    }


    public void setDownLoadListener(DownLoadListener mDownLoadListener) {
        this.mDownLoadListener = mDownLoadListener;
    }

    public interface DownLoadListener {
        void onStarted(DownloadInfo downloadinfo);

        void onLoading(DownloadInfo downloadinfo);

        void onWaiting(DownloadInfo downloadinfo);

        void onSuccess(DownloadInfo downloadinfo);

        void onFinished(DownloadInfo downloadinfo);

        void onError(DownloadInfo downloadinfo);

        void onCancelled(DownloadInfo downloadinfo);

        void stopDownloadService();
    }
}
