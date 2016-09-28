package com.augmentis.ayp.crimin;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;

import java.util.List;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callback, CrimeFragment.Callback{
    @Override
    protected Fragment onCreateFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        //Update list
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onOpenSelectFirst() {

        if(findViewById(R.id.detail_fragment_container) == null){
            List<Crime> crimeList = CrimeLab.getInstance(this).getCrimes();
            //single fragment
            if(crimeList != null && crimeList.size() > 0){
                Crime crime = crimeList.get(0);

                //replace old fragment with new one
                Fragment newDetailFragment = CrimeFragment.newInstance(crime.getId());
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, newDetailFragment).commit();

            }
        }

    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null){
            //single fragment
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }else {

            //replace old fragment with new one
            Fragment newDetailFragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, newDetailFragment).commit();
        }
    }
}
