package com.broadway.downfile.downfiledemo.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.broadway.downfile.downfiledemo.R;
import com.broadway.downfile.downfiledemo.bean.DownloadInfo;
import com.broadway.downfile.downfiledemo.bean.GlobalParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG  = MainActivity.class.getSimpleName();
    private Context context;
    private DownloadBroadcastReceiver mDownloadBroadcastReceiver;
    private DownLoadAdapter mDownLoadAdapter;
    private ListView mListView;
    private List<DownloadInfo> mDownLoadInfos;
    private DownloadService mDownloadService;

    private long mExitTime = 0;

    public static final int MSG_DOWNLOAD_START = 1; //开始下载
    public static final int MSG_DOWNLOAD_PAUSE = 2; //暂停
    public static final int MSG_DOWNLOAD_CANCEL_WAIT = 3; // 取消等待
    public static final int MSG_INSTALL_APP = 4; // 安装app
    public static final int MSG_OPEN_APP = 5;//打开app


    // 利用Handler更新UI
    private WeakHandler handler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int position = (int) msg.obj;
            switch (msg.what) {
                case MSG_DOWNLOAD_START:
                    startDownload(position);
                    break;
                case MSG_DOWNLOAD_PAUSE:
                    pauseDownload(position);
                    break;
                case MSG_DOWNLOAD_CANCEL_WAIT:
                    cancelWaitDownload(position);
                    break;
                case MSG_INSTALL_APP:
                    installApp(position);
                    break;
                case MSG_OPEN_APP:
                    openApp(position);
                    break;
                case 0x124:
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mListView = (ListView) findViewById(R.id.listivew);
        registerDownloadReceiver();

    }

    @Override
    protected void onStart() {
        super.onStart();
        List<DownloadInfo> downloadInfos = getDownloadInfo();
        fillData(downloadInfos);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //  mDownloadService.bindService(context, mServiceConnection);

        //绑定Service
        Intent intent = new Intent(context, DownloadService.class);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.unbindService(mServiceConnection);
        unregisterDownloadReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.i(TAG,"Status === "+Status);
//            if (Status == GlobalParams.STATUS_DOWNLOADING){
//                callbackApp();
//            }else{
//                if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                    Toast.makeText(context, "再按一次返回键退出应用", Toast.LENGTH_LONG).show();
//                    mExitTime = System.currentTimeMillis();
//                } else {
//                    context.unbindService(mServiceConnection);
//                    MainActivity.this.finish();
//                }
//            }

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(context, "再按一次返回键退出应用", Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 开始
     */
    private void startDownload(int position) {
        DownloadInfo downloadInfo = mDownLoadInfos.get(position);
        mDownloadService.startActionDownloadInfo(context, downloadInfo);
    }

    /**
     * 暂停
     */
    private void pauseDownload(int position) {
        DownloadInfo downloadInfo = mDownLoadInfos.get(position);
        mDownloadService.pauseActionDownloadInfo(context, downloadInfo);
    }

    /**
     * 取消等待
     */
    private void cancelWaitDownload(int position) {
        DownloadInfo downloadInfo = mDownLoadInfos.get(position);
        mDownloadService.cancelActionWaitDownload(context, downloadInfo);
    }

    /**
     * 安装app
     */
    private void installApp(int position) {
        DownloadInfo downloadInfo = mDownLoadInfos.get(position);
        String path = downloadInfo.getPath();
        Utils.installApp(context, new File(path));
    }

    private void openApp(int position) {
        final DownloadInfo downloadAppInfo = mDownLoadInfos.get(position);
        final String packagename = downloadAppInfo.getPackageName();
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }


    private void callbackApp() {
        mDownloadService.callbackActionApp(context);
    }


    /**
     * 初始化数据
     *
     * @return
     */
    private List<DownloadInfo> getDownloadInfo() {
        ArrayList<DownloadInfo> downloadInfos = new ArrayList<>();
        for (int i = 0; i < GlobalParams.DOWNLOAD_APP_NAMES.length; i++) {
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setId(i + 1);
            downloadInfo.setStatus(0);
            downloadInfo.setPosition(i);
            downloadInfo.setImg(GlobalParams.DOWNLOAD_APP_ICONS[i]);
            downloadInfo.setName(GlobalParams.DOWNLOAD_APP_NAMES[i]);
            downloadInfo.setUrl(GlobalParams.DOWNLOAD_APP_URLS[i]);
            downloadInfos.add(downloadInfo);
        }

        if (mDownloadService == null) {
            return downloadInfos;
        }

        List<DownloadInfo> downloadDbInfos = mDownloadService.findDownloadInfos();
        if (downloadDbInfos == null || downloadDbInfos.size() == 0) {
            return downloadInfos;
        }
        for (int i = 0; i < downloadDbInfos.size(); i++) {
            DownloadInfo downloadDbInfo = downloadDbInfos.get(i);
            for (int j = 0; j < downloadInfos.size(); j++) {
                if (downloadDbInfo.getName().equals(downloadInfos.get(j).getName())) {
                    int status = downloadDbInfo.getStatus();
                    int max = downloadDbInfo.getMax();
                    int progress = downloadDbInfo.getProgress();
                    String path = downloadDbInfo.getPath();

                    downloadInfos.get(j).setStatus(status);
                    downloadInfos.get(j).setMax(max);
                    downloadInfos.get(j).setProgress(progress);
                    downloadInfos.get(j).setPath(path);
                }
            }
        }

        //判断该APP是否已经安装
        for (int i = 0; i < downloadInfos.size(); i++) {
            DownloadInfo downloadAppInfo = downloadInfos.get(i);
            String path = downloadAppInfo.getPath();
            if (null != path && !"".equals(path)) {
                String apkFilePackage = Utils.getApkFilePackage(context, new File(path));
                if (null != apkFilePackage && !"".equals(apkFilePackage)) {
                    if (Utils.isAppInstalled(context, apkFilePackage)) {
                        downloadAppInfo.setStatus(GlobalParams.STATUS_INSTALLED);
                        downloadAppInfo.setPackageName(apkFilePackage);
                    }
                }
            }
        }


        return downloadInfos;
    }


    private int Status = 0;
    private void refreshList(final DownloadInfo downloadInfo) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DownloadInfo oldDownloadInfo = mDownLoadInfos.get(downloadInfo.getPosition());
                oldDownloadInfo.setStatus(downloadInfo.getStatus());
                oldDownloadInfo.setProgress(downloadInfo.getProgress());
                oldDownloadInfo.setMax(downloadInfo.getMax());
                oldDownloadInfo.setPackageName(downloadInfo.getPackageName());
                oldDownloadInfo.setPath(downloadInfo.getPath());

                Status = downloadInfo.getStatus();

                mDownLoadAdapter.setData(mDownLoadInfos);
                mDownLoadAdapter.notifyDataSetChanged();
            }
        });
    }


    private void fillData(List<DownloadInfo> downloadAppInfo) {
        mDownLoadInfos = downloadAppInfo;
        if (mDownLoadAdapter == null) {
            Log.i(TAG,"mDownLoadAdapter 0000000 ");
            mDownLoadAdapter = new DownLoadAdapter(context, handler, mDownLoadInfos);
            mListView.setAdapter(mDownLoadAdapter);
        } else {
            Log.i(TAG,"mDownLoadAdapter 000000111111111 ");
            mDownLoadAdapter.setData(mDownLoadInfos);
            mDownLoadAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示是否继续下载的dialog
     */
    private void showkeepOnDownloadDialog() {
        final NormalDialog dialog = new NormalDialog(context);
        BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
        BaseAnimatorSet bas_out = new FadeExit();
        dialog.content("当前有任务正在下载，是否在后台继续下载？")//
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(23)//
                .btnText("后台下载", "停止下载")//
                .btnTextColor(Color.parseColor("#383838"), Color.parseColor("#D4D4D4"))//
                .btnTextSize(16f, 16f)//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();

        dialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
            @Override
            public void onBtnLeftClick() {
                dialog.dismiss();

                MainActivity.this.finish();
            }
        });

        dialog.setOnBtnRightClickL(new OnBtnRightClickL() {
            @Override
            public void onBtnRightClick() {
                dialog.superDismiss();

                // 停止下载
                mDownloadService.stopActionDownload(context);
                MainActivity.this.finish();
            }
        });
    }


    // 注册广播接收者
    private void registerDownloadReceiver() {
        if (mDownloadBroadcastReceiver == null) {
            mDownloadBroadcastReceiver = new DownloadBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(GlobalParams.DOWNLOAD_APP_CHECK_ACTION);
            filter.addAction(GlobalParams.DOWNLOAD_APP_STOP_ACTION);
            registerReceiver(mDownloadBroadcastReceiver, filter);
        }
    }

    // 注销广播接收者
    private void unregisterDownloadReceiver() {
        if (mDownloadBroadcastReceiver != null) {
            try {
                unregisterReceiver(mDownloadBroadcastReceiver);
                mDownloadBroadcastReceiver = null;
            } catch (Exception e) {
            }
        }
    }

    /**
     * 接收下载的广播
     */
    private class DownloadBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (GlobalParams.DOWNLOAD_APP_CHECK_ACTION.equals(action)) {
                boolean isAppDownload = intent.getBooleanExtra(GlobalParams.DOWNLOAD_APP_CHECK_KEY, false);
                if (isAppDownload) {
                    showkeepOnDownloadDialog();
                }
            } else if (GlobalParams.DOWNLOAD_APP_STOP_ACTION.equals(action)) {

            }
        }
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder localBinder) {
            mDownloadService = ((DownloadService.DownloadServiceBinder) localBinder).getDownloadService(); //获取Myservice对象
            mDownloadService.setDownLoadListener(new MyDownLoadListener());

            List<DownloadInfo> downloadInfos = getDownloadInfo();
            fillData(downloadInfos);

        }

        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public class MyDownLoadListener implements DownloadService.DownLoadListener {

        @Override
        public void onStarted(DownloadInfo downloadinfo) {
            refreshList(downloadinfo);
        }

        @Override
        public void onLoading(DownloadInfo downloadinfo) {
            refreshList(downloadinfo);
        }

        @Override
        public void onWaiting(DownloadInfo downloadinfo) {
            refreshList(downloadinfo);
        }

        @Override
        public void onSuccess(DownloadInfo downloadinfo) {
            refreshList(downloadinfo);
        }

        @Override
        public void onFinished(DownloadInfo downloadinfo) {

        }

        @Override
        public void onError(DownloadInfo downloadinfo) {
            refreshList(downloadinfo);
        }

        @Override
        public void onCancelled(DownloadInfo downloadinfo) {
            refreshList(downloadinfo);
        }

        @Override
        public void stopDownloadService() {
            try {
                context.unbindService(mServiceConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
