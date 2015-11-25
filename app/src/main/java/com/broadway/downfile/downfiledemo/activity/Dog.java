package com.broadway.downfile.downfiledemo.activity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by fengyanjun on 15/11/20.
 */
@Table(name = "dog")
public class Dog {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "color")
    private String color;

    @Column(name="temap")
    private String temap;

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


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTemap() {
        return temap;
    }

    public void setTemap(String temap) {
        this.temap = temap;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}