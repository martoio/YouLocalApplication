package com.martoio.android.youlocalapplication;


import android.support.v4.app.Fragment;
/*
* Starting activity;
* */
public class YouLocalActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return YouLocalFragment.newInstance();
    }
}
