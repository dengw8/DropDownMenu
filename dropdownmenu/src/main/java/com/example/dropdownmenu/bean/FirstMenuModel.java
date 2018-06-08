package com.example.dropdownmenu.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/6.
 */

public class FirstMenuModel {
    private String name;
    private List<SecondMenuModel> mList = new ArrayList<>();   //当前一级菜单下的二级菜单列表

    public FirstMenuModel(String name, List<SecondMenuModel> mList) {
        this.name = name;
        this.mList = mList;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName() {
        return  name;
    }
    public void setmList(List<SecondMenuModel> mList) {
        this.mList = mList;
    }
    public List<SecondMenuModel> getmList() {
        return mList;
    }
}
