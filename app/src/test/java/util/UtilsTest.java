package util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UtilsTest {
    @Test
    void testCalcDistance() {
      double dist = Utils.calcDistance(40.688939, -74.04455, 40.746853, -73.985633);
      assertEquals(8.131774297975046, dist, 0.0001, "Test distance");
    }
}
