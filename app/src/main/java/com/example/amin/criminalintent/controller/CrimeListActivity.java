package com.example.amin.criminalintent.controller;

import android.support.v4.app.Fragment;

import com.example.amin.criminalintent.R;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }
}
