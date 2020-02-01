package com.recore.projectrecoresc.Common;

import android.text.format.DateFormat;

import com.recore.projectrecoresc.Model.User;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static final int PICK_IMAGE_REQUEST =9999 ;
    public static String CameraLocationRef ="CameraLocation";
    public static String CameraArea ="CameraArea";
    public static String UserLocation="UserLocation";
    public static String UserInformation="UserInformation";
    public static final String CHAT="CHAT";
    public static String feedback ="FEEDBACK";


    public static User currentUser;

    public static String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd \n hh:mm:ss a", calendar).toString();
        return date;
    }
}
