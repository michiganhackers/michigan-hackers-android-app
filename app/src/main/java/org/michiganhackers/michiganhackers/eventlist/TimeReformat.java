package org.michiganhackers.michiganhackers.eventlist;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class TimeReformat{
    public static String getDate(DateTime dateTime){
        SimpleDateFormat newSDF = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
        if(dateTime.isDateOnly()){
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            try{
                date = oldSDF.parse(dateTime.toString());
            }
            catch (Exception ex){
                // Todo: exception handling
                ex.printStackTrace();
            }
            return newSDF.format(date);
        }
        else{
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            Date date = new Date();
            try{
                date = oldSDF.parse(dateTime.toString());
            }
            catch (Exception ex){
                // Todo: exception handling
                ex.printStackTrace();
            }
            return newSDF.format(date);
        }
    }
    // Only valid if input has time
    public static String getTime(DateTime dateTime){
        if(dateTime.isDateOnly()){
            // Todo: Implement exception handling
            return null;
        }
        else{
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            SimpleDateFormat newSDF = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
            Date date = new Date();
            try{
                date = oldSDF.parse(dateTime.toString());
            }
            catch (Exception ex){
                // Todo: exception handling
                ex.printStackTrace();
            }
            return newSDF.format(date);
        }
    }
}