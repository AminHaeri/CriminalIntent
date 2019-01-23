package com.example.amin.criminalintent.controller;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;

import com.example.amin.criminalintent.R;

/**
 * All activities that host one fragment must extend this class
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    public abstract Fragment createFragment();

    @LayoutRes
    public abstract int getLayoutResId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.fragment_list_container) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_list_container, createFragment())
                    .commit();
        }
    }
}
