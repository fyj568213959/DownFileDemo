package com.broadway.downfile.downfiledemo.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by fengyanjun on 15/11/19.
 */
@Table(name = "downloadinfo")
public class DownloadInfo implements Serializable {
    //往xutil里面存，必须有一个int 类型的id
    @Column(name = "id",isId = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url; //网络下载地址

    @Column(name = "img")
    private String img;

    @Column(name = "status")
    private int status;

    @Column(name = "max")
    private int max;

    @Column(name = "progress")
    private int progress;

    @Column(name = "position")
    private int position; //在list里面的位置

    @Column(name = "path")
    private String path;//本地保存路径

    @Column(name = "packageName")
    private String packageName;//程序包名

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
