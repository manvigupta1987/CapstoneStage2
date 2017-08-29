package com.fitness.manvi.walkmore.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by manvi on 31/5/17.
 */
@Database(version = fitnessDataBase.VERSION)
public final class fitnessDataBase {

    private fitnessDataBase(){}

    public static final int VERSION = 1;
    @Table(fitnessColumns.class) public static final String TABLE_NAME = "fitnessdata";
}