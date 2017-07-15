package com.fitness.manvi.walkmore.utils;
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
        return (float) (heightInInch * inchToMeterConversion);
    }

    public static float convertFeetToInch(float feet, float inch){
        return (float) ((feet * feetToInchConversion) + inch);
    }

    public static int convertCentimeterToInch(float centimeter){
          return (int)(round(centimeter * centiToInchConversion));
    }

    public static int convertInchToCentimeter(float inch){
        return (int)(round(inch * inchToCentiConversion));
    }
}
