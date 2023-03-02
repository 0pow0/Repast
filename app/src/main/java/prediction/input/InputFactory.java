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
  public static InputWrap produceInput(UserEquipment ue,
		List<Double> coor) {
		double lng = coor.get(0);
		double lat = coor.get(1);
		int attachedEnbID = ue.getAttachedBaseStationID();
		while (attachedEnbID == -1) {
			attachedEnbID = ue.getAttachedBaseStationID();
		}

		InputWrap input = new InputWrap();
    BaseStationController controller = new BaseStationController();
		for (BaseStation bs : controller.getContainer()) {
			double distance = Utils.calcDistance(lat, lng, bs.getLat(), bs.getLng());
			InputFromBaseStation x = new InputFromBaseStation(bs.getId(), distance,
				bs.getTxPower(), bs.getBandwidth(), bs.getSubBandwidth(),
				bs.getSubBandOffset(), bs.getNumberOfAttachedUe());
			if (bs.getId() == attachedEnbID) {
				input.setInputOfAttachedBS(x);
			} else {
				input.getInputOfInterferingBS().add(x);
			}
		}
		return input;
  }
}
