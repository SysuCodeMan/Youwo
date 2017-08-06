package com.example.davidwillo.youwo.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by limin on 2017/6/13.
 */

public class LifeAccount {
    public int id;
    public boolean earnings;
    public float money;
    public String remark;
    public String time;
    public String type;
    @SerializedName("username")
    public String name;

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
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public float getMoney() {
        return money;
    }
    public void setMoney(float money) {
        this.money = money;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean isEarnings() {
        return earnings;
    }
    public void setEarnings(boolean earnings) {
        this.earnings = earnings;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
