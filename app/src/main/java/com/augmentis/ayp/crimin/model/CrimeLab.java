package com.augmentis.ayp.crimin.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.augmentis.ayp.crimin.model.CrimeDbSchema.CrimeTable;

/**
 * Created by Noppharat on 7/18/2016.
 */
public class CrimeLab {
    protected static final String TAG = "CrimeLab";
    /////////////////////////////////////// STATIC /////////////////////////////////////////////////

//    List<Crime> crimeList;

    private static CrimeLab instance;

    public static CrimeLab getInstance(Context context){
        if(instance == null){
            instance = new CrimeLab(context);
        }
        return instance;
    }

    public static ContentValues getContentValues(Crime crime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, crime.getCrimeDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED, (crime.isSolved()) ? 1: 0);
        Log.d(TAG, "CrimeLab " + crime.isSolved()+ " uuid ->" + crime.getId().toString() );
        Log.d(TAG, "title " + crime.getTitle());
        return contentValues;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private  Context context;
    private SQLiteDatabase database;

    private CrimeLab(Context context){

        //เก็บ reference ไปหาตัว Application แทน ลดปัญหา memory
        this.context = context.getApplicationContext();
        CrimeBaseHelper crimeBaseHelper = new CrimeBaseHelper(context);
        database = crimeBaseHelper.getWritableDatabase();

//        crimeList = new ArrayList<>();

//        for(int i = 0; i < 100 ; i++){
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setSolved(i % 2 == 0);
//
//            crimeList.add(crime);
//        }
    }

//    public int getCrimePositionById(UUID uuid){
////        int size = crimeList.size();
////        for (int i = 0; i < size; i++){
////            if(crimeList.get(i).getId().equals(uuid)){
////                return i;
////            }
////        }
//        return -1;
//    }

    public Crime getCrimeById(UUID uuid){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ? ",
                new String[]{ uuid.toString()});
        try {
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
//        for (Crime crime : crimeList){
//            if(crime.getId().equals(uuid)){
//                return crime;
//            }
//        }
       // return null;
    }

    public CrimeCursorWrapper queryCrimes(String whereCause, String[] whereArgs){
        Cursor cursor = database.query(CrimeTable.NAME,
                null,
                whereCause,
                whereArgs,
                null,
                null,
                null);
        return new CrimeCursorWrapper(cursor);
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

//    public static void main(String [] args){
//        CrimeLab crimeLab = CrimeLab.getInstance(null);
//        List<Crime> crimeList = crimeLab.getCrimes();
//        int size = crimeList.size();
//        for(int i = 0; i < size ; i++){
//            System.out.println(crimeList.get(i));
//        }
//        System.out.println(crimeLab.toString());
//
//        System.out.println(CrimeLab.getInstance(null));
//
//    }

    public void addCrime(Crime crime){
        Log.d(TAG, "Crime Lab : " + getCrimeById(crime.getId()));
//        crimeList.add(crime);
        ContentValues contentValues = getContentValues(crime);
        database.insert(CrimeTable.NAME, null, contentValues);
    }

    public void deleteCrime(UUID uuid){
        database.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ? ",
                new String[]{ uuid.toString()});
    }

    public void updateCrime(Crime crime){
        Log.d(TAG, "Crime Lab update");
        String uuidStr = crime.getId().toString();
        ContentValues contentValues = getContentValues(crime);

        //จะไม่ต่อ query โดยตรง แก้ปัญหา SQL injection
        database.update(CrimeTable.NAME, contentValues, CrimeTable.Cols.UUID + " = ? " , new String[] { uuidStr });
    }

    public File getPhotoFile(Crime crime){
        File externalFileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFileDir == null){
            return null;
        }

        return new File(externalFileDir, crime.getPhotoFileName());
    }
}
