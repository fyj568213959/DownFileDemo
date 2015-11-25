package com.broadway.downfile.downfiledemo.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.broadway.downfile.downfiledemo.R;
import com.broadway.downfile.downfiledemo.bean.DownloadInfo;
import com.broadway.downfile.downfiledemo.bean.GlobalParams;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyanjun on 15/11/19.
 */
public class DownLoadAdapter extends BaseAdapter {

    private static final String TAG = DownLoadAdapter.class.getSimpleName();
    private Context mContext;
    private WeakHandler mHandler;
    private List<DownloadInfo> mDownloadinfos;
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public DownLoadAdapter(Context context, WeakHandler handler,List<DownloadInfo> downloadInfos) {
        this.mContext = context;
        this.mHandler = handler;
        this.mDownloadinfos = downloadInfos;
    }

    @Override
    public int getCount() {
        return mDownloadinfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mDownloadinfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<DownloadInfo> downloadInfos){
        //this.mDownloadinfos = downloadInfos;
        Log.i(TAG,"TTTT === "+mDownloadinfos.size());
        mDownloadinfos.clear();
        mDownloadinfos.addAll(downloadInfos);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_down, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_down_name = (TextView) convertView.findViewById(R.id.tv_down_name);
            viewHolder.tv_download_size = (TextView) convertView.findViewById(R.id.tv_download_size);
            viewHolder.tv_download_status = (TextView) convertView.findViewById(R.id.tv_download_status);
            viewHolder.iv_download_icon = (ImageView) convertView.findViewById(R.id.iv_download_icon);
            viewHolder.bt_download = (Button) convertView.findViewById(R.id.bt_download);
            viewHolder.pb_download = (ProgressBar) convertView.findViewById(R.id.pb_download);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DownloadInfo downloadInfo = mDownloadinfos.get(position);
        viewHolder.tv_down_name.setText(downloadInfo.getName());

        int max = downloadInfo.getMax();
        int progress = downloadInfo.getProgress();

        if (max == 0 || progress == 0) {
            viewHolder.pb_download.setVisibility(View.INVISIBLE);
            viewHolder.pb_download.setProgress(0);
            viewHolder.pb_download.setMax(0);
        } else {
            viewHolder.pb_download.setVisibility(View.VISIBLE);
            String downloadPerSize = getDownloadPerSize(progress, max);
            viewHolder.tv_download_size.setText(downloadPerSize);
            viewHolder.pb_download.setMax(max);
            viewHolder.pb_download.setProgress(progress);
        }

        Log.i(TAG,"downloadInfo.getStatus() == "+downloadInfo.getStatus());

        switch (downloadInfo.getStatus()){
            case GlobalParams.STATUS_NOT_DOWNLOAD:
                viewHolder.tv_download_status.setText("未下载");
                viewHolder.bt_download.setText("下载");
                viewHolder.bt_download.setEnabled(true);
                viewHolder.bt_download.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case GlobalParams.STATUS_WAIT:
                viewHolder.tv_download_status.setText("排队中.请稍后");
                viewHolder.bt_download.setText("等待");
                viewHolder.bt_download.setEnabled(true);
                viewHolder.bt_download.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case GlobalParams.STATUS_START:
                viewHolder.tv_download_status.setText("连接中");
                viewHolder.bt_download.setText("连接中");
                viewHolder.bt_download.setEnabled(false);
                viewHolder.bt_download.setTextColor(mContext.getResources().getColor(R.color.accent_yellow));
                break;
            case GlobalParams.STATUS_DOWNLOADING:
                viewHolder.tv_download_status.setText("下载中");
                viewHolder.bt_download.setText("暂停");
                viewHolder.bt_download.setEnabled(true);
                viewHolder.bt_download.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case GlobalParams.STATUS_PAUSE:
                viewHolder.tv_download_status.setText("暂停下载");
                viewHolder.bt_download.setText("继续");
                viewHolder.bt_download.setEnabled(true);
                viewHolder.bt_download.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case GlobalParams.STATUS_COMPLETE:
                viewHolder.tv_download_status.setText("下载完成");
                viewHolder.bt_download.setText("安装");
                viewHolder.bt_download.setEnabled(true);
                viewHolder.bt_download.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case GlobalParams.STATUS_DOWNLOAD_ERROR:
                viewHolder.tv_download_status.setText("下载失败");
                viewHolder.bt_download.setText("重新下载");
                viewHolder.bt_download.setEnabled(true);
                viewHolder.bt_download.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
        }



        viewHolder.bt_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (downloadInfo.getStatus()){

                    case GlobalParams.STATUS_NOT_DOWNLOAD:
                        Message startMsg = Message.obtain();
                        startMsg.what = MainActivity.MSG_DOWNLOAD_START;
                        startMsg.obj = position;
                        mHandler.sendMessage(startMsg);
                        break;
                    case GlobalParams.STATUS_WAIT:
                        Message msgCancelWait = Message.obtain();
                        msgCancelWait.what = MainActivity.MSG_DOWNLOAD_CANCEL_WAIT;
                        msgCancelWait.obj = position;
                        mHandler.sendMessage(msgCancelWait);
                        break;
                    case GlobalParams.STATUS_DOWNLOADING:
                        Message msgPause = Message.obtain();
                        msgPause.what = MainActivity.MSG_DOWNLOAD_PAUSE;
                        msgPause.obj = position;
                        mHandler.sendMessage(msgPause);
                        break;
                    case GlobalParams.STATUS_PAUSE:
                        Message msgReStart = Message.obtain();
                        msgReStart.what = MainActivity.MSG_DOWNLOAD_START;
                        msgReStart.obj = position;
                        mHandler.sendMessage(msgReStart);
                        break;
                    case GlobalParams.STATUS_COMPLETE:
                        Message msgInstall = Message.obtain();
                        msgInstall.what = MainActivity.MSG_INSTALL_APP;
                        msgInstall.obj = position;
                        mHandler.sendMessage(msgInstall);
                        break;
                    case GlobalParams.STATUS_DOWNLOAD_ERROR:
                        Message msgerrReStart = Message.obtain();
                        msgerrReStart.what = MainActivity.MSG_DOWNLOAD_START;
                        msgerrReStart.obj = position;
                        mHandler.sendMessage(msgerrReStart);
                        break;
                }

            }
        });
        return convertView;
    }

    private class ViewHolder {
        public TextView tv_down_name;
        public TextView tv_download_size;
        public TextView tv_download_status;
        public ImageView iv_download_icon;
        public Button bt_download;
        public ProgressBar pb_download;
    }

    private String getDownloadPerSize(long finished, long total) {
        return DF.format((float) finished / (1024 * 1024)) + "M/" + DF.format((float) total / (1024 * 1024)) + "M";
    }
}
