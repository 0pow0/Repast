package prediction.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputWrap {
  private InputFromBaseStation inputOfAttachedBS;
  private List<InputFromBaseStation> inputOfInterferingBS;

  public InputWrap() {
    inputOfInterferingBS = new ArrayList<>();
  }

  public InputFromBaseStation getInputOfAttachedBS() {
    return inputOfAttachedBS;
  }

  public void setInputOfAttachedBS(InputFromBaseStation inputOfAttachedBS) {
    this.inputOfAttachedBS = inputOfAttachedBS;
  }

  public List<InputFromBaseStation> getInputOfInterferingBS() {
    return inputOfInterferingBS;
  }

  public void setInputOfInterferingBS(List<InputFromBaseStation> inputOfInterferingBS) {
    this.inputOfInterferingBS = inputOfInterferingBS;
  }

  public float[] toInputOfDropPercentageModel(String mask) {
    ArrayList<Float> res = new ArrayList<>();
    for (int i = 0; i < mask.length(); ++i) {
      if (mask.charAt(i) == '1') {
        float[] xi = inputOfInterferingBS.get(i).toArray();
        for (int j = 0; j < xi.length; ++j) {
          res.add(xi[j]);
        }
      }
    }
    float[] ret = new float[res.size()];
    for (int i = 0; i < res.size(); ++i) {
      ret[i] = res.get(i);
    }
    return ret;
  }

  public List<float[]> genInputOfProbModel() {
    List<float[]> res = new ArrayList<>();
    for (int i = 0; i < inputOfInterferingBS.size(); ++i) {
      float[] x = new float[2];
      x[0] = (float) inputOfAttachedBS.numberOfAttachedUe;
      x[1] = (float) inputOfInterferingBS.get(i).numberOfAttachedUe;
      res.add(x);
    }
    return res;
  }
}
