package com.augmentis.ayp.crimin;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.augmentis.ayp.crimin.model.Crime;

import java.util.UUID;

public class CrimePagerActivity extends SingleFragmentActivity implements CrimeFragment.Callback{

    private UUID _crimeId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_single_fragment;
    }

    @Override
    protected Fragment onCreateFragment() {
        _crimeId = (UUID) getIntent().getSerializableExtra(CRIME_ID);
        return CrimeFragment.newInstance(_crimeId);
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        //TODO : I'll see what I can do here.
    }

    protected static final String CRIME_ID = "crimePagerActivity.crimeId";

    public static Intent newIntent(Context activity, UUID id) {
        Intent intent = new Intent(activity, CrimePagerActivity.class);
        intent.putExtra(CRIME_ID, id);
        return intent;
    }
}
