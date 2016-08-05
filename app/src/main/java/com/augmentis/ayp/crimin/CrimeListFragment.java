package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Noppharat on 7/18/2016.
 */
public class CrimeListFragment extends Fragment {

    private static final int REQUEST_UPDATE_CRIME = 1234;
    private static final String SUBTITLE_VISIBLE_STATE = "SUBTITLE_VISIBLE";
    private RecyclerView _crimeRecyclerView;
    private CrimeAdapter _adapter;
    protected static final String TAG = "CRIME_LIST";
    private Integer[] crimePos;
    private boolean _subtitleVisible;
    private Callback callback;

    public interface Callback{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (Callback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }


    private File photoFile;

    public void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if(!_subtitleVisible){
            subtitle = null;
        }

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_VISIBLE_STATE, _subtitleVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.menu_item_new_crime:

                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);//TODO: add addCrime() to Crime
                //support tablet
                updateUI();
                callback.onCrimeSelected(crime);

                return true;
            case R.id.menu_item_show_subtitle:
                _subtitleVisible = !_subtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_show_subtitle);

        if(_subtitleVisible){
            menuItem.setTitle(R.string.hide_subtitle);
        }else {
            menuItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        selectFirstItem();

    }

    public void selectFirstItem(){
        if(CrimeLab.getInstance(getActivity()).getCrimes().size() > 0){
            callback.onCrimeSelected(CrimeLab.getInstance(getActivity()).getCrimes().get(0));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CrimeLab", "Resume list");
//        Log.d("CrimeLab", "First is "+CrimeLab.getInstance(getActivity()).getCrimes().get(0).isSolved());
        updateUI();
        selectFirstItem();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_UPDATE_CRIME){
            if(resultCode == Activity.RESULT_OK){
//                crimePos = (int) data.getExtras().get("position");
                crimePos = (Integer[]) data.getExtras().get("position");
                Log.d("CrimeLab", "get crimePos = " + crimePos);
            }


            Log.d(TAG, "Return CrimeFragment");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //view have inflate
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        Log.d("CrimeLab", "========================Create View=====================================");

        _crimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        //recyclerview need setLayoutManager to start view
        _crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null){
            _subtitleVisible = savedInstanceState.getBoolean(SUBTITLE_VISIBLE_STATE);
        }

        updateUI();

        return v;
    }

    public void updateUI(){
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(_adapter == null){
            _adapter = new CrimeAdapter(crimes, this);
            _crimeRecyclerView.setAdapter(_adapter);
        }else{

            _adapter.setCrimes(crimeLab.getCrimes());
//            _crimeRecyclerView.setAdapter(_adapter);
            _adapter.notifyDataSetChanged();

//            _adapter.notifyItemChanged(crimePos);
//            if(crimePos != null){
//                for (Integer pos : crimePos){
//                    _adapter.notifyItemChanged(pos);
//
//                }
//            }
        }



        updateSubtitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView _titleTextView;
        public TextView _dateTextView;
        public CheckBox _solvedCheckBox;
        public ImageView _imageView;


        Crime _crime;
        int _position;


        public CrimeHolder(View itemView) {
            super(itemView);

            _titleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            _solvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_solved_check_box);
//            _solvedCheckBox.setEnabled(false);
            _imageView = (ImageView) itemView.findViewById(R.id.list_item_crime_image_view);
            _dateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);

            itemView.setOnClickListener(this);

        }

        public void bind(Crime crime, int position) {
            _crime = crime;

            photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(_crime);
            Bitmap bitmap = PictureUtils.getScaleBitmap(photoFile.getPath(), getActivity());
            _imageView.setImageBitmap(bitmap);

            Log.d("CrimeLab", "2-Crime Before Set:" + _crime.isSolved());
            _titleTextView.setText(_crime.getTitle());


            _solvedCheckBox.setChecked(_crime.isSolved());
            _solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    _crime.setSolved(b);
                    Log.d("CrimeLab", "2-Crime:" + b + " " + _crime.isSolved());
                    Log.d("CrimeLab", "2-uuid:" + _crime.getId().toString());
                    CrimeLab.getInstance(getActivity()).updateCrime(_crime);
                    if(CrimeListFragment.this.isResumed()) {
                        callback.onCrimeSelected(_crime);
                    }
                }
            });
            _dateTextView.setText(_crime.getStringDateTime(_crime.getCrimeDate()));
            _position = position;
        }

        @Override
        public void onClick(View view) {

            updateUI();
            callback.onCrimeSelected(_crime);
//            Intent intent = CrimePagerActivity.newIntent(getActivity(), _crime.getId());
////            crimePos = _position;
//            startActivityForResult(intent, REQUEST_UPDATE_CRIME);

        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> _crimes;
        private Fragment _f;
        private int viewCreatingCount;

        public CrimeAdapter(List<Crime> crimes, Fragment f){
            _crimes = crimes;
            _f = f;
        }
//
//        protected void setCrimes(List<Crime> crimes){
//            _crimes = crimes;
//        }

        public void setCrimes(List<Crime> crimes){
            _crimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            viewCreatingCount++;
            Log.d(TAG, "Create view holder for CrimeList: creating view time = " + viewCreatingCount);
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);

            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Log.d(TAG, "Bind view holder for CrimeList : position = " + position);
            Crime crime = _crimes.get(position);
            holder.bind(crime, position);
        }

        @Override
        public int getItemCount() {
            return _crimes.size();
        }
    }

}
