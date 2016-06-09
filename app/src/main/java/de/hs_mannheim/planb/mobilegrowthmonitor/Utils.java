package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by defin on 08.06.2016.
 */
public class Utils {

    /**
     * Returns age of a profileUser
     * @param date
     * @return
     */
    public static int getAge(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = new Date();
        try {
            tempDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int age = 0;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(tempDate);
        Calendar today = Calendar.getInstance();
        age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;
    }

    /**
     * Rotates Bitmap
     *
     * @param source bitmap to be rotated
     * @param angle  angle that the picture has to be rotated
     * @return rotated bitmap
     */
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}