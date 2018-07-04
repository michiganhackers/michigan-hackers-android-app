package org.michiganhackers.michiganhackers;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeReformat{
    public static String getDate(DateTime dateTime){
        SimpleDateFormat newSDF = new SimpleDateFormat("EEEE, MMMM dd");
        if(dateTime.isDateOnly()){
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd");
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
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
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
            SimpleDateFormat oldSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            SimpleDateFormat newSDF = new SimpleDateFormat("hh:mm aaa");
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