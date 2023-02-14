package prediction.model;

import java.util.Arrays;

import ai.djl.ndarray.types.Shape;
import ai.djl.translate.TranslateException;
import util.AppConf;

public class Model {
	private DropProbabilityPredictionModel probModel;
	private StaticPredictionModel staticPredictionModel;
	private SinrPredictionModel sinrModel;
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
		sinrModel = new SinrPredictionModel.Builder().numIntfBS(numInterferenceBS)
			.build();
		probModel = new DropProbabilityPredictionModel();
		weight = 0.7;
		sinrThreshold = 20.0;

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
		dropPercentageModel = new DropPercentageModel(pathForDropPercentageModel,
			shapeForDropPercentageModel);

		method = AppConf.getInstance().getString(
			"prediction.model.Model.method");
		unit = AppConf.getInstance().getString(
			"prediction.model.Model.unit");
	}

	public double calcHCost(double hcost, double sinr) {
		if (sinr >= sinrThreshold) return hcost;
		double phi = (sinrThreshold - sinr) / sinrThreshold;
		return hcost * (1.0 + phi);
	}

	private static double calcStaticSINR(double distance, int numberOfRB,
		double txPower) {
		double noise = -174.0 + 10.0 * Math.log10((double) (numberOfRB * 180000));
		double pathLoss = Math.max(23.9, 1.8 * Math.log10(100));
		pathLoss = Math.max(pathLoss, 20.0);
		pathLoss = pathLoss * Math.log10(distance) + 20.0 * Math.log10(40.0 * Math.PI * (2110e6 / 1e9) / 3.0);
		double rx = txPower - pathLoss - 9.0;
		return rx - noise;
	}

	protected float calcStaticSINR(float[] x) {
		float y = staticPredictionModel.predict(x);
		return y;
	}

	protected float calcDropPercentage(float[] x) {
		float y;
		try {
			y = dropPercentageModel.predict(x);
		} catch (TranslateException e) {
			throw new RuntimeException(e);
		}
		return y;
	}

	public float calcPreictedSinr(float[] xForStaticPredictionModel,
		float[] xForDropPercentageModel, float[] xForProbPredictionModel) {
		float staticSinrDb = calcStaticSINR(xForStaticPredictionModel);
		float staticSinrLinear = (float) Math.pow(10.0, staticSinrDb / 10.0);
		float predictedSinrLinear;

		if (method.equals("min")) {
			float dropPercentageLinear = calcDropPercentage(xForDropPercentageModel);
			float dropLinear = staticSinrLinear * dropPercentageLinear;
			predictedSinrLinear = staticSinrLinear - dropLinear;
		} else if (method.equals("max")) {
			predictedSinrLinear = staticSinrLinear;
		} else if (method.equals("prob")) {
			float dropPercentageLinear = calcDropPercentage(xForDropPercentageModel);
			float dropLinear = staticSinrLinear * dropPercentageLinear;
			float minLinear = staticSinrLinear - dropLinear;
			float prob;
			try {
				if (xForProbPredictionModel[0] == 0.0f) prob = 0.0f;
				else prob = probModel.predict(xForProbPredictionModel[0]);
			} catch (TranslateException e) {
				throw new RuntimeException(e);
			}
			System.out.println("x = " + Arrays.toString(xForProbPredictionModel)
				+ " Prob = " + prob + " minLinear = " + minLinear + " 1-prob = " + (1 - prob) + " StaticLinear = " + staticSinrLinear);
			predictedSinrLinear = prob * minLinear + (1 - prob) * staticSinrLinear;
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

	public double calcWeightedSINR(float[] sinrX, float probX, double distanceToAttachedBS,
			int numberOfRB) throws TranslateException {
		// in dB
		double staticSINR = calcStaticSINR(distanceToAttachedBS, numberOfRB, 40.0);

		// Torch model is FLOAT32
		double prob = (double) probModel.predict(probX);
		// SINR_1 = log10(Linear Drop) * 10
		double sinrDropDb = (double) sinrModel.predict(sinrX);
		// Linear Drop = SINR_max - SINR_min (Linear)
		// = 10^(SINR_1 / 10.0)
		double sinrDropLinear = Math.pow(10.0, (sinrDropDb / 10.0));
		// Linear Static = 10^(static/10.0)
		double staticSINRLinear = Math.pow(10.0, (staticSINR / 10.0));
		double sinrLinear = staticSINRLinear - sinrDropLinear;
		double sinrDb = 10 * Math.log10(sinrLinear);
	
		double weightedSINR = prob * sinrDb + (1.0 - prob) * staticSINR;

		System.out.println("[Model.calcWeightedSINR]"
			+ " Prob x = " + probX 
			+ " Prob = " + prob
			+ " x = " + Arrays.toString(sinrX)
			+ " staticSINRLinear = " + staticSINRLinear 
			+ " sinrDropLinear = " + sinrDropLinear 
			+ " weighted SINR = " + weightedSINR
			+ "Static SINR = " + staticSINR);
		return staticSINR;
	}

	public DropProbabilityPredictionModel getProbModel() {
		return probModel;
	}

	public SinrPredictionModel getSinrModel() {
		return sinrModel;
	}

	public double getWeight() {
		return weight;
	}
}
