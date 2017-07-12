package com.fitness.manvi.walkmore.utils;
import com.google.common.base.Preconditions;

import static java.lang.Math.round;

/**
 * Created by manvi on 23/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class HeightUtils {

    private static final double inchToMeterConversion = 0.025;
    private static final double feetToInchConversion = 12;
    private static final double centiToInchConversion= 0.394;
    private static final double inchToCentiConversion= 2.54;

    public static float convertInchtoMeter(float heightInInch){
        Preconditions.checkArgument(heightInInch>0.0, "heightInInch should be in positive");
        return (float) (heightInInch * inchToMeterConversion);
    }

    public static float convertFeetToInch(float feet, float inch){
        Preconditions.checkArgument(feet>0.0, "feet should be in positive");
        Preconditions.checkArgument(inch>=0.0, "inch should be in positive");
        return (float) ((feet * feetToInchConversion) + inch);
    }

    public static int convertCentimeterToInch(float centimeter){
        Preconditions.checkArgument(centimeter>0.0, "centimeter should be in positive");
        return (int)(round(centimeter * centiToInchConversion));
    }

    public static int convertInchToCentimeter(float inch){
        Preconditions.checkArgument(inch>0.0, "inch should be in positive");
        return (int)(round(inch * inchToCentiConversion));
    }
}
