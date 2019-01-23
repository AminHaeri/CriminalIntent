package com.example.amin.criminalintent.controller;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.amin.criminalintent.R;
import com.example.amin.criminalintent.models.Crime;

public class CrimeListActivity extends SingleFragmentActivity implements
        CrimeListFragment.Callbacks, CrimeDetailFragment.Callbacks {

    @Override
    public Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.fragment_detail_container) == null) {
            //Phone
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            //Tablet
            CrimeDetailFragment crimeDetailFragment = CrimeDetailFragment.newInstance(crime.getId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_detail_container, crimeDetailFragment)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment crimeListFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_list_container);

        crimeListFragment.updateUI();
    }
}










