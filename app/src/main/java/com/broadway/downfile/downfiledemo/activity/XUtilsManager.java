package com.broadway.downfile.downfiledemo.activity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.HttpManager;
import org.xutils.x;

import java.io.File;

/**
 * Created by fengyanjun on 15/11/20.
 */
public class XUtilsManager {

    private static final String TAG = XUtilsManager.class.getSimpleName();
    private static XUtilsManager mDbUtis;
    private DbManager.DaoConfig daoConfig;
    private HttpManager mHttp;
    private DbManager db;

    public XUtilsManager(final Context context) {
        Log.i(TAG,"111111");
        daoConfig = new DbManager.DaoConfig()
                .setDbName("fengyj")
                .setDbDir(new File(Utils.getCachePath(context)))
                .setDbVersion(1)
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        Log.i(TAG, "oldVersion = " + oldVersion + "   newVersion = " + newVersion);
                        Toast.makeText(context, "oldVersion = " + oldVersion + "   newVersion = " + newVersion, Toast.LENGTH_LONG).show();
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        //添加字段
//                        try {
//                            db.addColumn(Dog.class, "temap");
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
                    }
                });
        Log.i(TAG,"22222");
        db = x.getDb(daoConfig);
        mHttp = x.http();
    }

    public static XUtilsManager getInstance(Context context) {
        Log.i(TAG,"mDbUtis = "+mDbUtis);
        if (null == mDbUtis) {
            synchronized (XUtilsManager.class) {
                if (null == mDbUtis) {
                    mDbUtis = new XUtilsManager(context);
                }
            }
        }
        return mDbUtis;
    }


    public HttpManager getHttp() {
        return mHttp;
    }

    public DbManager getDb() {
        return db;
    }

}
