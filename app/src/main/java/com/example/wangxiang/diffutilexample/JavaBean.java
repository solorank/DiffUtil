package com.example.wangxiang.diffutilexample;

/**
 * Created by wangxiang on 2016-10-10.
 * 一个普通的JavaBean，但是实现了clone方法，仅仅用于写Demo时，模拟刷新从网络获取数据用.
 */
class JavaBean implements Cloneable {
    private String name;
    private String desc;
    private int pic;

    public JavaBean(String name, String desc, int pic) {
        this.name = name;
        this.desc = desc;
        this.pic = pic;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    //仅写DEMO 用 实现克隆方法
    @Override
    public JavaBean clone() throws CloneNotSupportedException {
        JavaBean bean = null;
        try {
            bean = (JavaBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }
}