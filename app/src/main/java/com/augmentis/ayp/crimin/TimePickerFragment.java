package com.augmentis.ayp.crimin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by Noppharat on 7/28/2016.
 */
public class TimePickerFragment extends DialogFragment implements DialogInterface.OnClickListener {

    protected static final String EXTRA_TIME = "TimePickerFragment.EXTRA_TIME";
    private static final String TAG = "TAG";

    public static TimePickerFragment newInstance(Date date){
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable("ARG_TIME", date);
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }

    TimePicker _timePicker;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date time = (Date) getArguments().getSerializable("ARG_TIME");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        _timePicker = (TimePicker) v.findViewById(R.id.time_picker_in_dialog);
        _timePicker.setHour(hour);
        _timePicker.setMinute(minute);
        builder.setView(v);
        builder.setTitle("Pick Time");
        builder.setPositiveButton(android.R.string.ok, this);
        return builder.create();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Date dateinput = (Date) getArguments().getSerializable("ARG_TIME");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateinput);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = _timePicker.getHour();
        int minute = _timePicker.getMinute();
        Log.d(TAG, "Day : " + dayOfMonth + " Month : " + month + " Year : " + year + " Hour :" + hour + " Minute : " + minute);

        Date date = new Date(year, month, dayOfMonth, hour, minute);
        sendResult(Activity.RESULT_OK, date);
    }

    private void sendResult(int resultOk, Date time) {
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultOk, intent);
    }
}
