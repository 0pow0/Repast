package prediction.model;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Paths;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;

public class StaticPredictionModel {
  private String modelPath;
  private Shape inputShape;
  private InputTranslator translator;
  private Criteria<FloatBuffer, Float> criteria;
  private ZooModel<FloatBuffer, Float> model;
  private Predictor<FloatBuffer, Float> predictor;

  public StaticPredictionModel(String path, Shape shape) {
    modelPath = path;
    inputShape = shape;
    translator = new InputTranslator();
    criteria = Criteria.builder()
      .setTypes(FloatBuffer.class, Float.class)
      .optTranslator((Translator) translator)
      .optModelPath(Paths
          .get(modelPath))
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

  final class InputTranslator implements Translator<FloatBuffer, Float> {

    @Override
    public NDList processInput(TranslatorContext ctx, FloatBuffer input) throws Exception {
      NDManager manager = ctx.getNDManager();
      NDArray array = manager.create(inputShape);
      array.set(input);
      System.out.println(array);
      return new NDList(array);
    }

    @Override
    public Float processOutput(TranslatorContext ctx, NDList list) throws Exception {
      float[] res = list.get(0).toFloatArray();
      return res[0];
    }
  }
}
