package prediction.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.djl.ndarray.types.Shape;
import ai.djl.translate.TranslateException;
import util.AppConf;
import prediction.input.InputWrap;
import prediction.output.ProbContainer;
import util.Utils;

public class Model {
	private DropProbabilityPredictionModel probModel;
	private StaticPredictionModel staticPredictionModel;
	private DropPercentageModel dropPercentageModel;
	private double weight;
	private double sinrThreshold;
	private String method;
	private String unit;

  private static Model model = null;
  public static int numInterferenceBS = 3;

  public static Model getInstance() {
    if (model == null) {
      try {
        model = new Model(numInterferenceBS);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return model;
  }

	private Model(int numInterferenceBS) throws Exception {
		//* Initialize Probability preidction model */
		String pathForProbabilityModel = AppConf.getInstance()
			.getString("prediction.model.DropProbabilityModel.path");
		Shape shapeForProbModel = new Shape(1, 2);
		probModel = new DropProbabilityPredictionModel(pathForProbabilityModel,
			shapeForProbModel);

		int featureSize = AppConf.getInstance().getInt("featureSize");
		// * Initialize StaticPredictionModel
		String pathForStaticPredictionModel = AppConf.getInstance()
			.getString("prediction.model.StaticPredictionModel.path");
		Shape shapeForStaticPredictionModel = new Shape(1, featureSize);
		staticPredictionModel = new StaticPredictionModel(
			pathForStaticPredictionModel, shapeForStaticPredictionModel);

		// * Initialize DropPercentageModel
		String pathForDropPercentageModel = AppConf.getInstance().getString(
			"prediction.model.DropPercentageModel.path");
		int numberOfInterferingBaseStation = AppConf.getInstance().getInt(
			"numberOfInterferingBaseStation");
		Shape shapeForDropPercentageModel = new Shape(
			numberOfInterferingBaseStation, featureSize);
		dropPercentageModel = new DropPercentageModel(pathForDropPercentageModel);

		method = AppConf.getInstance().getString(
			"prediction.model.Model.method");
		unit = AppConf.getInstance().getString(
			"prediction.model.Model.unit");
	}

	protected float calcStaticSINR(float[] x) {
		float y = staticPredictionModel.predict(x);
		return y;
	}

	protected float calcDropPercentage(float[] x) {
		float y;
		int featureSize = AppConf.getInstance().getInt("featureSize");
		Shape shape = new Shape(x.length / featureSize, featureSize);
		try {
			y = dropPercentageModel.predict(x, shape);
		} catch (TranslateException e) {
			throw new RuntimeException(e);
		}
		return y;
	}

	public float calcPreictedSinr(InputWrap wrap) {
		float[] xForStaticPredictionModel = wrap.getInputOfAttachedBS().toArray();
		float staticSinrDb = calcStaticSINR(xForStaticPredictionModel);
		float staticSinrLinear = (float) Math.pow(10.0, staticSinrDb / 10.0);
		float predictedSinrLinear;

		int numberOfInterferingBaseStation = AppConf.getInstance().getInt(
			"numberOfInterferingBaseStation");
		if (method.equals("min")) {
			ArrayList<String> maskOfIntfBS = Utils.getCombinations(
				numberOfInterferingBaseStation, numberOfInterferingBaseStation);
			float[] xForDropPercentageModel = wrap.toInputOfDropPercentageModel(maskOfIntfBS.get(0));
			float dropPercentageLinear = calcDropPercentage(xForDropPercentageModel);
			assert(dropPercentageLinear <= 1.0f && dropPercentageLinear >= 0.0f);
			float dropLinear = staticSinrLinear * dropPercentageLinear;
			predictedSinrLinear = staticSinrLinear - dropLinear;
			System.out.println(maskOfIntfBS + " " + dropPercentageLinear + " " + predictedSinrLinear);
		} else if (method.equals("max")) {
			predictedSinrLinear = staticSinrLinear;
		} else if (method.equals("prob")) {
			ProbContainer probWrap = genProbs(wrap);
			List<String> allMask = new ArrayList<>();
			List<Float> probs = new ArrayList<>();
			List<Float> sinrs = new ArrayList<>();
			List<Float> dropPercent = new ArrayList<>();
			for (int l = 0; l <= numberOfInterferingBaseStation; ++l) {
				ArrayList<String> masks = Utils.getCombinations(numberOfInterferingBaseStation, l);
				for (int i = 0; i < masks.size(); ++i) {
					allMask.add(masks.get(i));
					float[] xForDropPercentageModel = wrap.toInputOfDropPercentageModel(masks.get(i));
					if (xForDropPercentageModel.length == 0) sinrs.add(staticSinrLinear);
					else {
						float dropPercentageLinear = calcDropPercentage(xForDropPercentageModel);
						dropPercent.add(dropPercentageLinear);
						assert dropPercentageLinear <= 1.0f && dropPercentageLinear >= 0.0f;
						float dropLinear = staticSinrLinear * dropPercentageLinear;
						float minLinear = staticSinrLinear - dropLinear;
						sinrs.add(minLinear);
					}
					probs.add(probWrap.genIntersectProb(masks.get(i)));
				}
			}
			predictedSinrLinear = 0.0f;
			for (int i = 0; i < probs.size(); ++i) {
				predictedSinrLinear += sinrs.get(i) * probs.get(i);
			}
			System.out.println(allMask);
			System.out.println(sinrs);
			System.out.println(probs);
			System.out.println(dropPercent);
			System.out.println();
			// float dropPercentageLinear = calcDropPercentage(xForDropPercentageModel);
			// assert(dropPercentageLinear <= 1.0f && dropPercentageLinear >= 0.0f);
			// float dropLinear = staticSinrLinear * dropPercentageLinear;
			// float minLinear = staticSinrLinear - dropLinear;
			// float prob;
			// if (xForProbPredictionModel[0] == 0.0f) prob = 0.0f;
			// else prob = probModel.predict(xForProbPredictionModel);
			// System.out.println("x = " + Arrays.toString(xForProbPredictionModel)
				// + " Prob = " + prob + " minLinear = " + minLinear + " 1-prob = " + (1 - prob) + " StaticLinear = " + staticSinrLinear);
			// predictedSinrLinear = prob * minLinear + (1 - prob) * staticSinrLinear;
		} else {
			throw new RuntimeException("Unsupported method. Available methods are:"
				+ "min/max/prob");
		}

		if (unit.equals("Linear"))
			return predictedSinrLinear;
		else if (unit.equals("dB"))
			return (float) (Math.log10(predictedSinrLinear) * 10.0);
		else throw new RuntimeException("Unrecognized unit, available units are "
			+ "Linear / dB");
	}

	public DropProbabilityPredictionModel getProbModel() {
		return probModel;
	}

	private ProbContainer genProbs(InputWrap inputWrap) {
		ProbContainer res = new ProbContainer();
		List<float[]> xs = inputWrap.genInputOfProbModel();
		for (float[] x : xs) {
			res.probs.add(probModel.predict(x));
		}
		return res;
	}
}
