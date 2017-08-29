package com.fitness.manvi.walkmore.data;

/**
 * Created by manvi on 28/8/17.
 */
import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by manvi on 31/5/17.
 */

public interface fitnessColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT)
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    @NotNull
    public static final String COLUMN_DATE = "data";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_STEPS = "steps_count";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_CALORIES = "calories";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_DISTANCE = "distance";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_DURATION = "duration";
}