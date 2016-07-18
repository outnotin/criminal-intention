package com.augmentis.ayp.crimin;

import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Noppharat on 7/18/2016.
 */
public class Crime {
    private UUID id;
    private String title;
    private Date crimeDate;
    private boolean solved;

    public Crime(){
//        crimeDate = new Date();
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCrimeDate() {
        return crimeDate;
    }

    public void setCrimeDate(Date crimeDate) {
        this.crimeDate = crimeDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getStringDate(Date inputDate){
        DateFormat dfm = new DateFormat();
        return dfm.format("dd MMMM yyyy", inputDate).toString();
    }
    @Override
    public String toString() {
        StringBuilder builder =  new StringBuilder();
        builder.append("UUID=").append(id);
        builder.append(",Title=").append(title);
        builder.append(",Crime Date=").append(crimeDate);
        builder.append(",Solved=").append(solved);
        return builder.toString();
    }
}
