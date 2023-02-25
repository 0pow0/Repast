package prediction.model;

import org.junit.Test;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import au.com.bytecode.opencsv.CSVReader;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public class SinrPredictionModelTest {
    @Test
    public void appHasAGreeting() throws ModelNotFoundException, MalformedModelException, IOException, TranslateException {
        // SinrPredictionModel classUnderTest = new SinrPredictionModel();
        // assertNotNull("app should have a greeting", classUnderTest.getGreeting());
        // assertNotNull("app should have a greeting", null);
        // CSVReader reader = new CSVReader(new FileReader("/home/rzuo02/work/repast/app/src/test/resources/sinr-model-testing.csv"));
	  // SinrPredictionModel model = new SinrPredictionModel.Builder().numIntfBS(3)
			// .build();
      // FloatBuffer buffer = FloatBuffer.allocate(15);
      // float[] x = new float[]{10.0f, 20.0f, 30.0f, 40.0f, 5.0f};
      // for (int i = 0; i < 3; ++i) {
        // buffer.put(x);
      // }
      // float y = model.predict(buffer.array());
      // assertEquals("y = " + y + "yhat = " + 0.1824,
      //   0.1824f, y, 0.00001f);
    }

    @Test
    public void testInputGenerator() {
    }
}
