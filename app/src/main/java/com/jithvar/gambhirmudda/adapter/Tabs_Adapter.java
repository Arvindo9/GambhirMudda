package com.jithvar.gambhirmudda.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jithvar.gambhirmudda.HomeTab;
import com.jithvar.gambhirmudda.TabTwo;

import java.util.ArrayList;

/**
 * Created by Arvindo on 22-02-2017.
 * Company KinG
 * email at support@towardtheinfinity.com
 */

public class Tabs_Adapter extends FragmentStatePagerAdapter {

    private int num_of_tabs;
    private final ArrayList<String> tabNameList;
    private final int noOfTabs;
    private String[] tabName;


    public Tabs_Adapter(FragmentManager fm, int num_of_tabs, ArrayList<String> tabNameList,
                        int noOfTabs) {
        super(fm);
        this.num_of_tabs = num_of_tabs;
        this.tabNameList = tabNameList;
        this.noOfTabs = noOfTabs;
        loadTab();
    }

    private void loadTab(){
        tabName = new String[noOfTabs];
        for(int i = 0; i < noOfTabs; i++){
            tabName[i] = "tab_" + i;
        }
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return new HomeTab();
//            case 1:
//                return new TabTwo();
//            case 2:
//                return new TabThree();
//            case 3:
//                return new TabFour();
//            case 4:
//                return new TabFive();
//            default:
//                return null;
//        }

        switch (position) {
            case 0:
                return new HomeTab();

            default:
                TabTwo fragment=new TabTwo();
                Bundle bundle = new Bundle();
                bundle.putInt("tabPosition", position);
                fragment.setArguments(bundle);
                return fragment;//new TabTwo();
        }


    }

    @Override
    public int getCount() {
        return num_of_tabs;
    }
}
