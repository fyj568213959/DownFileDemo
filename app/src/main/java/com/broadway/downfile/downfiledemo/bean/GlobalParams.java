package com.broadway.downfile.downfiledemo.bean;

public class GlobalParams {

    public static final int STATUS_NOT_DOWNLOAD = 0;//没有下载
    public static final int STATUS_WAIT = 1;//等待下载
    public static final int STATUS_START = 2;//开始下载
    public static final int STATUS_DOWNLOADING = 3;//下载中
    public static final int STATUS_PAUSE = 4;//暂停下载
    public static final int STATUS_RESTART = 5;//重新开始下载
    public static final int STATUS_COMPLETE = 6;//下载完成
    public static final int STATUS_INSTALLED = 7;//安装完成
    public static final int STATUS_DOWNLOAD_ERROR = 8;//下载错误
    public static final int STATUS_DOWNLOAD_CANCLE = 9;//下载取消

    //http://apistore.baidu.com/apiworks/servicedetail/508.html 百度糯米接口文档
    public static final String API_KEY = "fc2be3afb3324db10ff4eb40d9fc23c1";
    public static final String NUO_MI_DEALS = "http://apis.baidu.com/baidunuomi/openapi/searchdeals";
    public static final String NUO_MI_CITIES = "http://apis.baidu.com/baidunuomi/openapi/cities";

    //默认城市
    public static String DEFAULT_CITY_NAME = "北京市";
    public static String DEFAULT_CITY_ID = "100010000";
    public static String CITY = "city";


    public static String DOWNLOAD_APP_KEY = "download_app_key";
    public static String DOWNLOAD_APP_CHECK_ACTION = "download_app_check_action";
    public static String DOWNLOAD_APP_CHECK_KEY = "download_app_check_key";
    public static String DOWNLOAD_APP_STOP_ACTION = "download_app_stop_action";
    public static String DOWNLOAD_APP_STOP_KEY = "download_app_stop_key";


    //下载APP

    public static final String[] DOWNLOAD_APP_NAMES = {
            "网易云音乐",
            "腾讯视频",
            "UC浏览器",
            "360手机卫士",
            "前程无忧51job",
            "搜狐视频",
            "微信电话本",
            "淘宝",
            "聚美优品",
            "搜房网"
    };

    public static final String[] DOWNLOAD_APP_ICONS = {
            "http://img.wdjimg.com/mms/icon/v1/d/f1/1c8ebc9ca51390cf67d1c3c3d3298f1d_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/8/10/1b26d9f0a258255b0431c03a21c0d108_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/3/89/9f5f869c0b6a14d5132550176c761893_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/d/29/dc596253e9e80f28ddc84fe6e52b929d_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/e/d0/03a49009c73496fb8ba6f779fec99d0e_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/2/bf/939a67b179e75326aa932fc476cbdbf2_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/b/fe/718d7c213ce633fd4e25c278c19acfeb_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/f/29/cf90d1294ac84da3b49561a6f304029f_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/4/43/0318ce32731600bfa66cbb5018e1a434_512_512.png",
            "http://img.wdjimg.com/mms/icon/v1/7/08/2b3858e31efdee8a7f28b06bdb83a087_512_512.png"
    };

    public static final String[] DOWNLOAD_APP_URLS = {
            "http://s1.music.126.net/download/android/CloudMusic_2.8.1_official_4.apk",
            "http://dldir1.qq.com/qqmi/TencentVideo_V4.1.0.8897_51.apk",
            "http://wap3.ucweb.com/files/UCBrowser/zh-cn/999/UCBrowser_V10.6.0.620_android_pf145_(Build150721222435).apk",
            "http://msoftdl.360.cn/mobilesafe/shouji360/360safesis/360MobileSafe_6.2.3.1060.apk",
            "http://www.51job.com/client/51job_51JOB_1_AND2.9.3.apk",
            "http://upgrade.m.tv.sohu.com/channels/hdv/5.0.0/SohuTV_5.0.0_47_201506112011.apk",
            "http://dldir1.qq.com/qqcontacts/100001_phonebook_4.0.0_3148.apk",
            "http://download.alicdn.com/wireless/taobao4android/latest/702757.apk",
            "http://apps.wandoujia.com/apps/com.jm.android.jumei/download",
            "http://download.3g.fang.com/soufun_android_30001_7.9.0.apk"
    };
}
