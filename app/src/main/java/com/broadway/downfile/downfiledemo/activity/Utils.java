package com.broadway.downfile.downfiledemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.List;

/**
 * Created by fengyanjun on 15/11/20.
 */
public class Utils {



    public static final boolean listIsEmpty(List list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if (!listIsEmpty(packages)) {
            for (PackageInfo packageInfo : packages) {
                if (packageInfo.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void installApp(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static String getApkFilePackage(Context context, File apkFile) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkFile.getPath(), PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.applicationInfo.packageName;
        }
        return null;
    }

    /**
     * 根据手机是否加载sd卡，配置对应缓存路径 .../mnt/sdcard/Android/data/包名/cache
     */
    public static String getCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = context.getExternalCacheDir();
            if (file != null) {
                return file.getPath() + "/"; // /storage/emulated/0/Android/data/com.yinwei.uu.fitness/cache/
            } else {
                return Environment.getExternalStorageDirectory().getPath() + "/"; // /storage/emulated/0/
            }
        } else {
            return context.getCacheDir().getPath() + "/"; // /data/data/com.yinwei.uu.fitness/cache/
        }
    }
}
