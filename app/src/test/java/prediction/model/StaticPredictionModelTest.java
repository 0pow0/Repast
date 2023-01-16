package prediction.model;

import static org.junit.Assert.*;
import org.junit.Test;

import ai.djl.ndarray.types.Shape;
import util.AppConf;

public class StaticPredictionModelTest {
  @Test
  public void testPredict() {
    String path = AppConf.getInstance()
      .getString("prediction.model.StaticPredictionModel.path");
    Shape shape = new Shape(1, 5);
    StaticPredictionModel model = new StaticPredictionModel(path, shape);
    float[] x = new float[]{10.0f, 20.0f, 30.0f, 40.0f, 5.0f};
    float y = model.predict(x);
    assertEquals("y = " + y + "yhat = " + 40.2766f,
      40.3766f, y, 0.00001f);
  }
}

