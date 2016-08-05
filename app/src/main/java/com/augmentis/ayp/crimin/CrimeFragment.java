package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Noppharat on 7/18/2016.
 */
public class CrimeFragment extends Fragment {

    private static final String CRIME_ID = "CrimeFragment.CRIME_ID";
//    private static final String CRIME_POSITION = "CrimeFragment.CRIME_POS";
    private static final String DIALOG_DATE = "CrimeFragment.DIALOG_DATE";
    private static final String DIALOG_TIME = "CrimeFragment.DIALOG_TIME";
    private static final String DIALOG_IMAGE = "CrimeFragment.DIALOG_IMAGE";
    private static final int REQUEST_DATE = 1234;
    private static final int REQUEST_TIME = 4321;
    private static final int REQUEST_CAPTURE_PHOTO = 3333;
    private static final int REQUEST_BIGGER_IMAGE = 2222;

    private Crime crime;
    private File photoFile;
//    private int position;
    private EditText editText;
    private Button crimeDateButton;
    private Button crimeTimeButton;
    private CheckBox crimeSolvedCheckbox;
    private Button crimeDeleteButton;
    private ImageView photoView;
    private ImageButton photoButton;

    private Callback callback;

    //Callback

    public interface Callback{
        void onCrimeUpdated(Crime crime);
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

    public CrimeFragment(){}

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, crimeId);
//        args.putInt(CRIME_POSITION, position);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        crime = CrimeLab.getInstance(getActivity()).getCrimeById(crimeId);

        photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        editText = (EditText) v.findViewById(R.id.crime_title);

        editText.setText(crime.getTitle());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
                updateCrime();
//                addThisPositionToResult(position);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
//        crimeDateButton.setText(crime.getCrimeDate().toString());

//        DateFormat dfm = new DateFormat();
//        String dateFormatLabel = dfm.format("dd MMMM yyyy", crime.getCrimeDate()).toString();
//        crimeDateButton.setText(dateFormatLabel);

        crimeDateButton.setText(crime.getStringDate(crime.getCrimeDate()));
        crimeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialogFragment = DatePickerFragment.newInstance(crime.getCrimeDate());
//                        new DatePickerFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("ARG_DATE", crime.getCrimeDate());
//                dialogFragment.setArguments(args);
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialogFragment.show(fm, DIALOG_DATE);
            }
        });


        crimeTimeButton = (Button) v.findViewById(R.id.crime_time) ;
        crimeTimeButton.setText(crime.getSringTime(crime.getCrimeDate()));
        crimeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               FragmentManager fm = getFragmentManager();
                TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(crime.getCrimeDate());
                timePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                timePickerFragment.show(fm, DIALOG_TIME);
            }
        });


        crimeSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solve);
        crimeSolvedCheckbox.setChecked(crime.isSolved());
        crimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                crime.setSolved(isChecked);
                updateCrime();
//                addThisPositionToResult(position);
                Log.d(CrimeListFragment.TAG, "Crime:" + isChecked + " " + crime.isSolved());
                Log.d(CrimeListFragment.TAG, "uuid:" + crime.getId().toString());
            }
        });
        //check ว่า layout นั้นมี button หรือเปล่า

//        Intent intent = new Intent();
//        intent.putExtra("position", position);
//        getActivity().setResult(Activity.RESULT_OK, intent);

        crimeDeleteButton = (Button) v.findViewById(R.id.delete_crime);
        crimeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimeLab.getInstance(getActivity()).deleteCrime(crime.getId());
//                getActivity().finish();
                updateCrime();

                if(CrimeLab.getInstance(getActivity()).getCrimes().size() > 0 ){
                    Fragment newDetailFragment = CrimeFragment.newInstance(CrimeLab.getInstance(getActivity()).getCrimes().get(0).getId());
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.detail_fragment_container, newDetailFragment).commit();
                }else{

                }

            }
        });

        photoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        photoView = (ImageView) v.findViewById(R.id.crime_photo);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ImageDialog imageDialog = ImageDialog.newInstance(photoFile);
                imageDialog.setTargetFragment(CrimeFragment.this, REQUEST_BIGGER_IMAGE);
                imageDialog.show(fm, DIALOG_IMAGE);
            }
        });


        PackageManager packageManager = getActivity().getPackageManager();
        //Call camera intent
        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //check if we can take a photo
        boolean canTakePhoto = photoButton != null
                && captureImageIntent.resolveActivity(packageManager) != null;
        if(canTakePhoto){
            Uri uri = Uri.fromFile(photoFile);
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        //on click -> start activity for camera
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImageIntent, REQUEST_CAPTURE_PHOTO);
            }
        });

        updatePhotoView();
        return v;

    }

//    protected void addThisPositionToResult(int position){
//        if(getActivity() instanceof CrimePagerActivity){
//            ((CrimePagerActivity) getActivity()).addPageUpdate(position);
//        }
//    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("CrimeLab","On Pause in CrimeFragment");
//        updateCrime();
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if(result != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setCrimeDate(date);
            crimeDateButton.setText(getFormattedDate(crime.getCrimeDate()));
            updateCrime();
//            addThisPositionToResult(position);
        }

        if(requestCode == REQUEST_TIME){
            Date time = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            crime.setCrimeDate(time);
            crimeTimeButton.setText(getFormattedTime(crime.getCrimeDate()));
            updateCrime();
//            addThisPositionToResult(position);
        }

        if(requestCode == REQUEST_CAPTURE_PHOTO){
            updatePhotoView();
            updateCrime();
        }
    }

    private void updateCrime(){
        CrimeLab.getInstance(getActivity()).updateCrime(crime);
//        if(CrimeFragment.this.isResumed()){
            callback.onCrimeUpdated(crime);
//        }

    }

    private String getFormattedDate(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    private String getFormattedTime(Date date){
        return new SimpleDateFormat("HH : mm").format(date);
    }

    public void updatePhotoView(){
        if(photoFile == null || !photoFile.exists()){
            photoView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaleBitmap(photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }
    }
}
