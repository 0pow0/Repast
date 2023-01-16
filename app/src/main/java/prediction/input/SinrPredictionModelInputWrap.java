package prediction.input;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class SinrPredictionModelInputWrap {
  public FloatBuffer x;
	public float distanceToAttachedBS;
	public int numberOfUeAttachedToInterferenceBS;
	@Override
	public String toString() {
		return "SinrPredictionModelInputWrap [x=" + Arrays.toString(x.array()) + ", distanceToAttachedBS=" + distanceToAttachedBS
				+ ", numberOfUeAttachedToInterferenceBS=" + numberOfUeAttachedToInterferenceBS + "]";
	}
}
