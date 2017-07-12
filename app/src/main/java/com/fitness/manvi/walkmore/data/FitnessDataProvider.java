package com.fitness.manvi.walkmore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fitness.manvi.walkmore.utils.ConstantUtils;

/**
 * Created by manvi on 31/5/17.
 */
@SuppressWarnings("DefaultFileTemplate")
public final class FitnessDataProvider extends ContentProvider {

    private static final int CODE_FITNESS_DIR = 100;
    private static final int CODE_FITNESS_ID = 101;
    private static final int CODE_FITNESS_WITH_DATE = 102;

    private FitnessDbHelper mfitnessDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FitnessContract.CONTENT_AUTHORITY, FitnessContract.PATH_Fitness, CODE_FITNESS_DIR);
        uriMatcher.addURI(FitnessContract.CONTENT_AUTHORITY, FitnessContract.PATH_Fitness + "/#", CODE_FITNESS_ID);
        uriMatcher.addURI(FitnessContract.CONTENT_AUTHORITY, FitnessContract.PATH_Fitness + "/*", CODE_FITNESS_WITH_DATE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mfitnessDbHelper = new FitnessDbHelper(getContext());
        return true;
    }
    private static final SQLiteQueryBuilder sFitnessQueryBuilder;
    static{
        sFitnessQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sFitnessQueryBuilder.setTables(FitnessContract.fitnessDataEntry.TABLE_NAME);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        int matchId = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mfitnessDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (matchId){
            case CODE_FITNESS_DIR:
                cursor = sqLiteDatabase.query(FitnessContract.fitnessDataEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,orderBy);
                break;
            case CODE_FITNESS_ID:
                String id = uri.getLastPathSegment();
                int tab_Id = Integer.parseInt(id);
                String fitnessQuery;
                switch (tab_Id) {
                    case ConstantUtils.WEEK_TAB:
                        fitnessQuery = "SELECT * FROM " + FitnessContract.fitnessDataEntry.TABLE_NAME + " WHERE " + FitnessContract.fitnessDataEntry.COLUMN_DATE
                                + " BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime')" +
                                " ORDER BY " + FitnessContract.fitnessDataEntry.COLUMN_DATE;
                        break;
                    case ConstantUtils.MONTH_TAB:
                        fitnessQuery = "SELECT * FROM " + FitnessContract.fitnessDataEntry.TABLE_NAME + " WHERE " + FitnessContract.fitnessDataEntry.COLUMN_DATE
                                + " BETWEEN datetime('now', '-29 days') AND datetime('now', 'localtime')" +
                                " ORDER BY " + FitnessContract.fitnessDataEntry.COLUMN_DATE;
                        break;
                    case ConstantUtils.YEAR_TAB:
                        fitnessQuery = "SELECT strftime('%m', " + FitnessContract.fitnessDataEntry.COLUMN_DATE + " ) as " + FitnessContract.fitnessDataEntry.COLUMN_DATE + " , SUM(" + FitnessContract.fitnessDataEntry.COLUMN_STEPS + " ) as " + FitnessContract.fitnessDataEntry.COLUMN_STEPS + " FROM " + FitnessContract.fitnessDataEntry.TABLE_NAME + " WHERE " + FitnessContract.fitnessDataEntry.COLUMN_DATE
                                + " BETWEEN datetime('now', '-364 days') AND datetime('now', 'localtime')" + " GROUP BY strftime('%m', " + FitnessContract.fitnessDataEntry.COLUMN_DATE + " )";
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal position");
                }
                cursor = sqLiteDatabase.rawQuery(fitnessQuery,null);
                break;
            case CODE_FITNESS_WITH_DATE:
                String date = FitnessContract.fitnessDataEntry.getDateFromUri(uri);
                selection = FitnessContract.fitnessDataEntry.COLUMN_DATE + "=?";
                selectionArgs = new String[]{date};
                cursor = sqLiteDatabase.query(FitnessContract.fitnessDataEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,orderBy);
                break;
            default:
                throw new IllegalArgumentException("Invaild URI exception");
        }
        if(getContext()!=null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_FITNESS_DIR:
                return FitnessContract.fitnessDataEntry.CONTENT_TYPE;
            case CODE_FITNESS_ID:
                return FitnessContract.fitnessDataEntry.CONTENT_ITEM_TYPE;
            case CODE_FITNESS_WITH_DATE:
                return FitnessContract.fitnessDataEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int matchId = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mfitnessDbHelper.getWritableDatabase();
        long rowInserted;
        switch (matchId) {
            case CODE_FITNESS_DIR:
                rowInserted = sqLiteDatabase.insert(FitnessContract.fitnessDataEntry.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowInserted >0)
        {
            if(getContext()!=null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return ContentUris.withAppendedId(uri,rowInserted);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int matchId = sUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mfitnessDbHelper.getWritableDatabase();
        int rowDeleted;
        switch (matchId)
        {
            case CODE_FITNESS_DIR:{
                rowDeleted = sqLiteDatabase.delete(FitnessContract.fitnessDataEntry.TABLE_NAME,null,null);
                break;
            }
            case CODE_FITNESS_ID:{
                selection = FitnessContract.fitnessDataEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = sqLiteDatabase.delete(FitnessContract.fitnessDataEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowDeleted > 0)
        {
            if(getContext()!=null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
