package com.example.manvi.walkmore.utils;


/**
 * Created by manvi on 23/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class WeightUtils {

    private static final double kgCoversionFactor = .45;
    private static final double calorieBurnPerMileConversion = 0.57;
    private static final double avgStrideFactor = 0.413;
    private static final int inchPerFeet = 12;
    private static final int feetInMile = 5280;
    private static final double kgtoPound = 2.205;

    public static double convertWeightFromPoundsToKg(float weightInPounds){
        return (weightInPounds * kgCoversionFactor);
    }

    //http://www.livestrong.com/article/238020-how-to-convert-pedometer-steps-to-calories/
    //https://www.beachbodyondemand.com/blog/how-many-steps-walk-per-mile

    public static int countCalories(float weighInPounds, float heightInInch, int stepsCount){
        float calPerMile = (float)(calorieBurnPerMileConversion * weighInPounds);
        float avgStrideLength = (float)(heightInInch * avgStrideFactor)/inchPerFeet;
        int stepsPerMile = (int)(feetInMile/avgStrideLength);
        double conversionFac = calPerMile/stepsPerMile;
        return (int) (stepsCount * conversionFac);
    }

    public static double calculateDistanceFromSteps(int steps, float heightInInch, boolean isKilos){
        float avgStrideLength = (float)(heightInInch * avgStrideFactor)/inchPerFeet;
        int stepsPerMile = (int)(feetInMile/avgStrideLength);
        float distanceInMiles = (float)((steps * 1.0)/stepsPerMile);
        if(!isKilos){
            return distanceInMiles;
        }else {
            return DistanceUtils.convertMilesToKiloMeter(distanceInMiles);
        }
    }

    public static int convertKiloToPounds(double weightInKg){
        return (int)((weightInKg * kgtoPound));
    }

    public static float convertPoundsToKilo(float weightInPound){
        return (float) (weightInPound /kgtoPound);
    }
}
