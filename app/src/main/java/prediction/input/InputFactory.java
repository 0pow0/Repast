package prediction.input;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import ROCBuilder.UserEquipmentResp;
import repast.BaseStation;
import repast.BaseStationController;
import repast.NS3CommunicatiorHelper;
import repast.UserEquipment;
import util.Utils;
import util.AppConf;

public class InputFactory {
  /*
   * Query NS3 for base station id to which Ue at this location will attched.
   * @param coor: coor.get(0) is longitude, coor.get(1) is latitude
   */
  public static List<float[]> produceInput(UserEquipment ue,
		List<Double> coor) {
		double lng = coor.get(0);
		double lat = coor.get(1);
		int attachedEnbID = ue.getAttachedBaseStationID();
		while (attachedEnbID == -1) {
			attachedEnbID = ue.getAttachedBaseStationID();
		}
  
    BaseStationController controller = new BaseStationController();
		List<float[]> xs = new ArrayList<>();
    int size = controller.getContainer().size();
		int featureSize = AppConf.getInstance().getInt("featureSize");
    FloatBuffer buffer = FloatBuffer.allocate((size - 1) * featureSize); 
		int cnt = 0;
		for (BaseStation bs : controller.getContainer()) {
			double distance = Utils.calcDistance(lat, lng, bs.getLat(), bs.getLng());
			float[] curr = new float[5];
			curr[0] = (float) distance;
			curr[1] = (float) bs.getTxPower();
			curr[2] = (float) bs.getBandwidth();
			curr[3] = (float) bs.getSubBandwidth();
			curr[4] = (float) bs.getSubBandOffset();
			if (bs.getId() == attachedEnbID) {
				xs.add(curr);
			} else {
				buffer.put(curr);
				cnt++;
			}
		}
		xs.add(buffer.array());
		assert cnt == size - 1;
		return xs;
  }  
}
