package prediction.model;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Paths;

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
import util.AppConf;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;

public class DropProbabilityPredictionModel {
  private static String modelPath;
  private Shape inputShape;
  private InputTranslator translator;
  private Criteria<FloatBuffer, Float> criteria;
  private ZooModel<FloatBuffer, Float> model;
  private Predictor<FloatBuffer, Float> predictor;


  public DropProbabilityPredictionModel(String path, Shape shape) {
    this.translator = new InputTranslator();
    modelPath = path;
    inputShape = shape;
    criteria = Criteria.builder()
        .setTypes(FloatBuffer.class, Float.class)
        .optTranslator((Translator) translator)
        .optModelPath(Paths.get(path))
        .build();
    try {
      model = criteria.loadModel();
    } catch (ModelNotFoundException | MalformedModelException | IOException e) {
      throw new RuntimeException(e);
    }
    predictor = model.newPredictor();
  }

  public float predict(float[] arr) {
    FloatBuffer buffer = FloatBuffer.wrap(arr);
    try {
      return predictor.predict(buffer);
    } catch (TranslateException e) {
      throw new RuntimeException(e);
    }
  }

  private final class InputTranslator implements Translator<FloatBuffer, Float> {

    @Override
    public NDList processInput(TranslatorContext ctx, FloatBuffer input) throws Exception {
      NDManager manager = ctx.getNDManager();
      NDArray array = manager.create(inputShape);
      array.set(input);
      return new NDList(array);
    }

    @Override
    public Float processOutput(TranslatorContext ctx, NDList list) throws Exception {
      float[] res = list.get(0).toFloatArray();
      return res[0];
    }
  }
}