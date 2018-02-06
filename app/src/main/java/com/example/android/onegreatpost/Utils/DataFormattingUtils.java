package com.example.android.onegreatpost.Utils;

import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
   Created by dasse on 28-Jan-18.
 */

public class DataFormattingUtils {

    private static final SimpleDateFormat fullFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss 'Z'", Locale.US);
    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("MMM d,yyyy  h:mm a", Locale.US);
    private static final String TAG = "DataFormattingUtils";

    public static String getDatabaseIDFormat(String id) {
        if(id.contains("/")){
            id = id.replace("/", "-xs-");
        }
        return id;
    }

    public static Date getFullTimestampFormat(String timeStamp) {
        Timestamp timestamp = new Timestamp(Long.parseLong(timeStamp));
        try {
            Date mDate = fullFormat.parse(fullFormat.format(timestamp.getTime()));
            return mDate;
        } catch (ParseException e){
            Log.e(TAG,"getFullTimestampFormat_error: " + e.getLocalizedMessage());
            return null ;
        }
    }

    public static String getPartialTimestampFormat(String timeStamp) {
        Timestamp timestamp = new Timestamp(Long.parseLong(timeStamp));
        try {
            Date mDate= fullFormat.parse(fullFormat.format(timestamp.getTime()));
            return new SimpleDateFormat("d-MMM-yyyy", Locale.US).format(mDate);
        } catch (ParseException e){
            Log.e(TAG, "getPartialTimestampFormat_error: " + e.getLocalizedMessage());
            return null ;
        }
    }

    public static String getPostNameFormat(String id) {

        if(id.contains("/")){
            id = id.replace("/", "-xs-");
        }

        return id;
    }

    public static String getDateFromTitle(String date) {
        int end;
        date = date.substring(0, date.indexOf("Posted"));

        if(date.contains("2018"))
            end = date.indexOf("2018")+3;
        else
            end = date.indexOf("2017")+3;

        return date.replace(", ", "-")
                .replace(" ", "-")
                .substring(12, end);
    }
}
