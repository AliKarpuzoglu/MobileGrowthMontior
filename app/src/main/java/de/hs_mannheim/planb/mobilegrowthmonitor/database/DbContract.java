package de.hs_mannheim.planb.mobilegrowthmonitor.database;

import android.provider.BaseColumns;

/**
 * SQLite-Datamodel of MobileGrowthMonitor
 */

public final class DbContract {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbContract() {
    }

    public static final String DATABASE_NAME = "growthMonitor";
    public static final int DATABASE_VERSION = 3;

    public static abstract class FeedProfile implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_ID = "pId";
        public static final String COLUMN_NAME_LASTNAME = "lastname";
        public static final String COLUMN_NAME_FIRSTNAME = "firstname";
        public static final String COLUMN_NAME_SEX = "sex";
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
        public static final String COLUMN_NAME_PROFILEPIC = "profilepic";
    }

    public static abstract class FeedMeasurement implements BaseColumns {
        public static final String TABLE_NAME = "measurement";
        public static final String COLUMN_NAME_ID = "profile_id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_SIZE = "height";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_IMAGE = "image_path";
    }

    public static final String CREATE_PROFILES_TABLE_QUERY = "CREATE TABLE " + FeedProfile.TABLE_NAME + "(" +
            FeedProfile.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
            FeedProfile.COLUMN_NAME_LASTNAME + " TEXT," +
            FeedProfile.COLUMN_NAME_FIRSTNAME + " TEXT," +
            FeedProfile.COLUMN_NAME_SEX + " INTEGER," +
            FeedProfile.COLUMN_NAME_BIRTHDAY + " TEXT," +
            FeedProfile.COLUMN_NAME_PROFILEPIC + " TEXT" + ")";

    public static final String PROFILE_ALL_SELECT_QUERY = "SELECT * FROM " + FeedProfile.TABLE_NAME;

    public static final String CREATE_MEASUREMENT_TABLE_QUERY = "CREATE TABLE " + FeedMeasurement.TABLE_NAME + "(" +
            FeedMeasurement.COLUMN_NAME_ID + " INTEGER NOT NULL, " +
            FeedMeasurement.COLUMN_NAME_DATE + " TEXT NOT NULL, " +
            FeedMeasurement.COLUMN_NAME_SIZE + " REAL, " +
            FeedMeasurement.COLUMN_NAME_WEIGHT + " REAL, " +
            FeedMeasurement.COLUMN_NAME_IMAGE + " TEXT, " +
            "FOREIGN KEY(" + FeedMeasurement.COLUMN_NAME_ID + ")" + " REFERENCES " +
            FeedProfile.TABLE_NAME + "(" + FeedProfile.COLUMN_NAME_ID + "), " +
            "PRIMARY KEY("+ FeedMeasurement.COLUMN_NAME_ID + ", " + FeedMeasurement.COLUMN_NAME_DATE + "))";
}
