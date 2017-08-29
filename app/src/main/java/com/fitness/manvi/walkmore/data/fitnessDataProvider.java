package com.fitness.manvi.walkmore.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by manvi on 31/5/17.
 */
@ContentProvider(authority = fitnessDataProvider.AUTHORITY, database = fitnessDataBase.class,
        packageName = "com.fitness.manvi.walkmore.data.generated")

public final class fitnessDataProvider {

    public static final String AUTHORITY = "com.fitness.manvi.walkmore.data.fitnessDataProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String PATH_FITNESS = "fitness";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }


    @TableEndpoint(table = fitnessDataBase.TABLE_NAME)
    public static class fitness {

        @ContentUri(
                path = Path.PATH_FITNESS,
                type = "vnd.android.cursor.dir/fitness")
        public static final Uri CONTENT_URI = buildUri(Path.PATH_FITNESS);

        @InexactContentUri(
                name = "FITNESS_ID",
                path = Path.PATH_FITNESS + "/#",
                type = "vnd.android.cursor.item/fitness",
                whereColumn = fitnessColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.PATH_FITNESS, String.valueOf(id));
        }

        @InexactContentUri(
                name = "FITNESS_DATE",
                path = Path.PATH_FITNESS + "/*",
                type = "vnd.android.cursor.item/fitness",
                whereColumn = fitnessColumns.COLUMN_DATE,
                pathSegment = 1)
        public static Uri WithDate(String date) {
            return buildUri(Path.PATH_FITNESS, date);
        }
    }
}