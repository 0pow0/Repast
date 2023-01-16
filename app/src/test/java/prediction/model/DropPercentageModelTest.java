package prediction.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.junit.Test;

import ai.djl.MalformedModelException;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import util.AppConf;

public class DropPercentageModelTest {
    @Test
    public void testPredict() throws ModelNotFoundException, MalformedModelException, IOException, TranslateException {
      String path = AppConf.getInstance()
        .getString("prediction.model.DropPercentageModel.path");
      Shape shape = new Shape(3, 5);
      DropPercentageModel model = new DropPercentageModel(path, shape);
      FloatBuffer buffer = FloatBuffer.allocate(20);
      float[] x = new float[]{10.0f, 20.0f, 30.0f, 40.0f, 5.0f};
      for (int i = 0; i < 3; ++i) {
        buffer.put(x);
      }
      float y = model.predict(buffer.array());
      assertEquals("y = " + y + "yhat = " + 0.1824f,
        0.1824f, y, 0.0001f);
    }
}
