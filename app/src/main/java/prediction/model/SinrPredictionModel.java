package prediction.model;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Paths;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Translator;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.TranslatorContext;
import jzombies.UAV;
import jzombies.Util;
import repast.BaseStation;
import repast.BaseStationContainer;
import repast.UserEquipment;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;

public class SinrPredictionModel {
  private InputTranslator translator;
  private Criteria<FloatBuffer, Float> criteria;
  private ZooModel<FloatBuffer, Float> model;
  private Predictor<FloatBuffer, Float> predictor;

  private int numIntfBS;
  private String modelPath;

  private SinrPredictionModel(Builder builder) throws ModelNotFoundException, MalformedModelException, IOException {
    numIntfBS = builder.numIntfBS;
    modelPath = builder.modelPath;
    translator = new InputTranslator(numIntfBS);
    criteria = Criteria.builder()
      .setTypes(FloatBuffer.class, Float.class)
      .optTranslator((Translator) translator)
      .optModelPath(Paths
          .get(modelPath))
      .build();
    model = criteria.loadModel();
    predictor = model.newPredictor();
  }

  public float predict(float[] arr) throws TranslateException {
    FloatBuffer buffer = FloatBuffer.wrap(arr);
    return predictor.predict(buffer);
  }

  public static class Builder {
    private int numIntfBS;
    private String modelPath = "/home/rzuo02/work/repast/app/src/main/resources/repast/traced_sinr_regreesion_model.pt";

    public Builder numIntfBS(int numIntfBS) {
      this.numIntfBS = numIntfBS;
      return this;
    }
    
    public Builder modelPath(String path) {
      this.modelPath = path;
      return this;
    }

    public SinrPredictionModel build() throws ModelNotFoundException, MalformedModelException, IOException {
      return new SinrPredictionModel(this);
    }
  }

  final class InputTranslator implements Translator<FloatBuffer, Float> {
    private int numIntfBS;

    public InputTranslator(int numIntfBS) {
      this.numIntfBS = numIntfBS;
    }

    @Override
    public NDList processInput(TranslatorContext ctx, FloatBuffer input) throws Exception {
      Shape shape = new Shape(1, numIntfBS, 5);
      NDManager manager = ctx.getNDManager();
      NDArray array = manager.create(shape);
      array.set(input);
      return new NDList(array);
    }

    @Override
    public Float processOutput(TranslatorContext ctx, NDList list) throws Exception {
      float[] res = list.get(0).toFloatArray();
      return res[0];
    }

    @Override
    public Batchifier getBatchifier() {
      // TODO Auto-generated method stub
      return null;
    }
  }
}
