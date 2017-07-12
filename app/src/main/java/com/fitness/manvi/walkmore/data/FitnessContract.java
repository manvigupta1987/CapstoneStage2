package com.fitness.manvi.walkmore.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;



import static android.content.ContentResolver.*;

/**
 * Created by manvi on 1/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class FitnessContract {

    private FitnessContract() {
        throw new AssertionError();  //AssertionError isn’t strictly required, but it provides insurance in case the constructor is accidentally invoked from within the class. It guarantees that the c
        // class will never be instantiated under any circumstances.
    }

    public static final String CONTENT_AUTHORITY = "com.fitness.manvi.walkmore";
    private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_Fitness = "fitness";

    public static final class fitnessDataEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_Fitness).build();

        public static final String TABLE_NAME = "fitnessdata";

        public static final String CONTENT_TYPE =
                CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Fitness;
        public static final String CONTENT_ITEM_TYPE =
                CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Fitness;

        //Movie ID from the json data. This will be act as foreign key for the other tables.
        public static final String COLUMN_DATE = "data";
        public static final String COLUMN_STEPS = "steps_count";
        public static final String COLUMN_CALORIES = "calories";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_DURATION = "duration";


        public static Uri buildFitnessDataUriWithDate(String date) {
            if(!Strings.isNullOrEmpty(date)) {
                return CONTENT_URI.buildUpon().appendPath(date).build();
            }else {
                return null;
            }
        }

        public static Uri buildFitnessDataUriWithTabID(int tab_id) {
            Preconditions.checkArgument(tab_id >=0, "tab id should not be negative");
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(tab_id)).build();
        }

        public static String getDateFromUri(Uri uri) {
            return (uri.getPathSegments().get(1));
        }
    }

}
