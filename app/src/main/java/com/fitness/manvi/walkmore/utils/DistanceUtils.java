package com.fitness.manvi.walkmore.utils;

import com.google.common.base.Preconditions;

/**
 * Created by manvi on 22/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DistanceUtils {

    private static final double meterToKmConversion = 0.001;
    private static final double milesToKmConversion = 1.61;
    private static final double kilosToMilesConversion = 0.62;

    public static float covertMetersToKiloMeters(float meters, boolean isKilos){
        Preconditions.checkArgument(meters >= 0.0, "meters should be in positive");
        float distanceInKm = (float) (meters * meterToKmConversion);
        if(isKilos) {
            return distanceInKm;
        }else {
            return convertKiloMetersToMiles(distanceInKm);
        }
    }

    public static float convertMilesToKiloMeter(float miles){
        Preconditions.checkArgument(miles>=0.0, "Miles should be in positive");
        return (float) (miles * milesToKmConversion);
    }

    private static float convertKiloMetersToMiles(float kilo){
        Preconditions.checkArgument(kilo>=0.0, "Kilo should be in positive");
        return (float)(kilo*kilosToMilesConversion);
    }
}
