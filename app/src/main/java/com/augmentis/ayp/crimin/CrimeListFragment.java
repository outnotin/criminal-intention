package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Noppharat on 7/18/2016.
 */
public class CrimeListFragment extends Fragment {

    private static final int REQUEST_UPDATE_CRIME = 1234;
    private RecyclerView _crimeRecyclerView;
    private CrimeAdapter _adapter;
    protected static final String TAG = "CRIME_LIST";
    private Integer[] crimePos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resume list");
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_UPDATE_CRIME){
            if(resultCode == Activity.RESULT_OK){
//                crimePos = (int) data.getExtras().get("position");
                crimePos = (Integer[]) data.getExtras().get("position");
                Log.d(TAG , "get crimePos = " + crimePos);
            }


            Log.d(TAG, "Return CrimeFragment");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //view have inflate
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);


        _crimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        //recyclerview need setLayoutManager to start view
        _crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(_adapter == null){
            _adapter = new CrimeAdapter(crimes);
            _crimeRecyclerView.setAdapter(_adapter);
        }else{
            //_adapter.notifyDataSetChanged();
//            _adapter.notifyItemChanged(crimePos);
            if(crimePos != null){
                for (Integer pos : crimePos){
                    _adapter.notifyItemChanged(pos);

                }
            }
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView _titleTextView;
        public TextView _dateTextView;
        public CheckBox _solvedCheckBox;

        Crime _crime;
        int _position;


        public CrimeHolder(View itemView) {
            super(itemView);

            _titleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            _solvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_solved_check_box);
            _dateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);

            itemView.setOnClickListener(this);

        }

        public void bind(Crime crime, int position) {
            _crime = crime;
            _titleTextView.setText(_crime.getTitle());
            _solvedCheckBox.setChecked(_crime.isSolved());
            _dateTextView.setText(_crime.getCrimeDate().toString());
            _position = position;
        }

        @Override
        public void onClick(View view) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), _crime.getId(), _position);
//            crimePos = _position;
            startActivityForResult(intent, REQUEST_UPDATE_CRIME);

        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> _crimes;
        private int viewCreatingCount;

        public CrimeAdapter(List<Crime> crimes){
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
