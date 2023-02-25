package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class UtilsTest {
    @Test
    public void testCalcDistance() {
      double dist = Utils.calcDistance(40.688939, -74.04455, 40.746853, -73.985633);
      assertEquals(8131.774297975046, dist, 0.0001, "Test distance");
    }

    @Test
    public void testGetCombinations() {
      int n = 8;
      int N = (int) Math.pow(2, n);
      HashMap<Integer, Set<String>> combinations = new HashMap<>();
      for (int i = 0; i < N; ++i) {
        int numberOf1 = 0;
        StringBuilder sb = new StringBuilder();
        int j = i;
        for (int k = 0; k < n; ++k) {
          numberOf1 += (j & 1);
          sb.append(Integer.toString(j & 1));
          sb.reverse();
          j >>= 1;
        }
        if (!combinations.containsKey(numberOf1))
          combinations.put(numberOf1, new HashSet<>());
        combinations.get(numberOf1).add(sb.toString());
      }
      System.out.println(combinations);
      for (Map.Entry<Integer, Set<String>> entry : combinations.entrySet()) {
        int k = entry.getKey();
        Set<String> v = entry.getValue();
        Set<String> comb = new HashSet<>();
        comb.addAll(Utils.getCombinations(n, k));
        System.out.println(v);
        System.out.println(comb);
        assertTrue(v.equals(comb));
      }
    }
}
