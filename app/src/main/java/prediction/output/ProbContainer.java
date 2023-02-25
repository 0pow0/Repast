package prediction.output;

import java.util.ArrayList;
import java.util.List;

public class ProbContainer {
  public List<Float> probs;
  public ProbContainer() {
    probs = new ArrayList<>();
  }

  public float genIntersectProb(String mask) {
    float prob = 1.0f;
    for (int i = 0; i < mask.length(); ++i) {
      if (mask.charAt(i) == '1')
        prob *= probs.get(i);
      else if (mask.charAt(i) == '0')
        prob *= (1.0f - probs.get(i));
    }
    return prob;
  }
}
