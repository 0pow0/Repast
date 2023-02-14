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
    private InputTranslator translator;
    private Criteria<Float, Float> criteria;
    private ZooModel<Float, Float> model;
    private Predictor<Float, Float> predictor;

    private static String modelPath;
    
    public DropProbabilityPredictionModel() throws ModelNotFoundException, MalformedModelException, IOException {
        this.translator = new InputTranslator();
        modelPath = AppConf.getInstance().getString("prediction.DropProbabilityModel.path");
        criteria = Criteria.builder()
            .setTypes(Float.class, Float.class)
            .optTranslator((Translator) translator)
            .optModelPath(Paths.get(modelPath))
            .build();
        model = criteria.loadModel();
        predictor = model.newPredictor();
    }

    public float predict(float arr) throws TranslateException {
        return predictor.predict(arr);
    }

    private final class InputTranslator implements Translator<Float, Float> {

        @Override
        public NDList processInput(TranslatorContext ctx, Float input) throws Exception {
            Shape shape = new Shape(1);
            NDManager manager = ctx.getNDManager();
            NDArray array = manager.create(shape);
            array.set(new float[]{input});
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