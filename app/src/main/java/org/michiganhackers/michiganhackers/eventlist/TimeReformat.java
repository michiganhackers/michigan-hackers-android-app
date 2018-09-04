package org.michiganhackers.michiganhackers.eventlist;

import com.google.api.client.util.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class TimeReformat{
    public static String getDate(DateTime dateTime){
        DateFormat newDF = getDateInstance();
        if(dateTime.isDateOnly()){
            DateFormat oldDF = getDateInstance();
            Date date = new Date();
            try{
                date = oldDF.parse(dateTime.toString());
            }
            catch (Exception ex){
                // Todo: exception handling
                ex.printStackTrace();
            }
            return newDF.format(date);
        }
        else{
            DateFormat oldDF = getDateInstance();
            Date date = new Date();
            try{
                date = oldDF.parse(dateTime.toString());
            }
            catch (Exception ex){
                // Todo: exception handling
                ex.printStackTrace();
            }
            return newDF.format(date);
        }
    }
    // Only valid if input has time
    public static String getTime(DateTime dateTime){
        if(dateTime.isDateOnly()){
            // Todo: Implement exception handling
            return null;
        }
        else{
            DateFormat oldDF = getDateInstance();
            DateFormat newDF = getDateInstance();
            Date date = new Date();
            try{
                date = oldDF.parse(dateTime.toString());
            }
            catch (Exception ex){
                // Todo: exception handling
                ex.printStackTrace();
            }
            return newDF.format(date);
        }
    }
}