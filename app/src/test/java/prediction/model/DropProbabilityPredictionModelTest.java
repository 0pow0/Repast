package prediction.model;

import static org.junit.Assert.*;
import org.junit.Test;

import ai.djl.ndarray.types.Shape;
import util.AppConf;

public class DropProbabilityPredictionModelTest {
  @Test
  public void testPredict() {
    String path = AppConf.getInstance()
      .getString("prediction.model.DropProbabilityModel.path");
    Shape shape = new Shape(1, 2);
    DropProbabilityPredictionModel model = new DropProbabilityPredictionModel(path, shape);
    float[] x = new float[]{10.0f, 20.0f};
    float y = model.predict(x);
    assertEquals("y = " + y + "yhat = " + 0.5015f,
    0.5015f, y, 0.0001f);
  }
}
