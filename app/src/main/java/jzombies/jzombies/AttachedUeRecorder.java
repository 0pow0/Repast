package jzombies;

import java.util.HashMap;
import java.util.List;

import ROCBuilder.UserEquipmentResp;
import repast.NS3CommunicatiorHelper;

public class AttachedUeRecorder {
	// Key: Timestep, value: (Key: enb id, number of Ue attached to this enb)
	private HashMap<Integer, HashMap<Integer, Integer>> memo;

	public AttachedUeRecorder() {
		memo = new HashMap<>();
	}

	public HashMap<Integer, Integer> getByTimestep(int timeStep) {
		return memo.getOrDefault(timeStep, new HashMap<>());
	}

	public int getByTimestepAndEnbId(int timeStep, int enbId) {
		return getByTimestep(timeStep).getOrDefault(enbId, 0);
	}

	public void inc(int timeStep, int enbId) {
		HashMap<Integer, Integer> enbAttachedUeMap = memo.getOrDefault(timeStep, new HashMap<>());
		enbAttachedUeMap.put(enbId, enbAttachedUeMap.getOrDefault(enbId, 0) + 1);
		memo.put(timeStep, enbAttachedUeMap);
	} 

	public void update(List<List<Double>> path, int uavId, List<Integer> timesteps) {
		NS3CommunicatiorHelper ns3CommunicatiorHelper = new NS3CommunicatiorHelper();
		for (int i = 0; i < path.size(); ++i) {
      List<Double> coor = path.get(i);
			ns3CommunicatiorHelper.sendActionReq(Integer.toString(uavId),
				Double.toString(coor.get(1)), Double.toString(coor.get(0)));
			ns3CommunicatiorHelper.sendUserEquipmentReq(Integer.toString(uavId));
			UserEquipmentResp resp = ns3CommunicatiorHelper
				.receiveUserEquipmentResp();
			int attachedEnbID = Integer.parseInt(resp.getAttachedEnbID());
      inc(timesteps.get(i), attachedEnbID);
		}
	}
}