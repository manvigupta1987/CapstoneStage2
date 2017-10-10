package com.fitness.manvi.walkmore.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * Created by manvi on 8/10/17.
 */
public class DistanceUtilsTest {
    @Test
    public void covertMetersToKiloMeters() throws Exception {
        float actual = DistanceUtils.covertMetersToKiloMeters(1000, false);
        float expected = 0.62f;

        assertEquals("Conversion from meter to kilometer ", expected, actual );

        float actual1 = DistanceUtils.covertMetersToKiloMeters(1000, true);
        float expected1 = 1.0f;

        assertEquals("Conversion from meter to kilometer ", expected1, actual1 );
    }
}