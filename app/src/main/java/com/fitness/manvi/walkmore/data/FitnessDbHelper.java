package com.fitness.manvi.walkmore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by manvi on 31/5/17.
 */
@SuppressWarnings("DefaultFileTemplate")
class FitnessDbHelper extends SQLiteOpenHelper {


    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "fitness.db";

    public FitnessDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_FITNESS_DATA_TABLE = "CREATE TABLE IF NOT EXISTS " + FitnessContract.fitnessDataEntry.TABLE_NAME +
                " (" + FitnessContract.fitnessDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FitnessContract.fitnessDataEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                FitnessContract.fitnessDataEntry.COLUMN_STEPS + " INTEGER NOT NULL, " +
                FitnessContract.fitnessDataEntry.COLUMN_CALORIES + " INTEGER NOT NULL, " +
                FitnessContract.fitnessDataEntry.COLUMN_DISTANCE + " INTEGER NOT NULL, " +
                FitnessContract.fitnessDataEntry.COLUMN_DURATION + " INTEGER NOT NULL, " +
                "UNIQUE (" + FitnessContract.fitnessDataEntry.COLUMN_DATE + ") ON CONFLICT REPLACE" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FITNESS_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FitnessContract.fitnessDataEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
