package com.fitness.manvi.walkmore.utils;


/**
 * Created by manvi on 22/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DistanceUtils {

    private static final double meterToKmConversion = 0.001;
    private static final double milesToKmConversion = 1.61;
    private static final double kilosToMilesConversion = 0.62;

    public static float covertMetersToKiloMeters(float meters, boolean isKilos){
        float distanceInKm = (float) (meters * meterToKmConversion);
        if(isKilos) {
            return distanceInKm;
        }else {
            return convertKiloMetersToMiles(distanceInKm);
        }
    }

    public static float convertMilesToKiloMeter(float miles){
        return (float) (miles * milesToKmConversion);
    }

    private static float convertKiloMetersToMiles(float kilo){
        return (float)(kilo*kilosToMilesConversion);
    }
}
