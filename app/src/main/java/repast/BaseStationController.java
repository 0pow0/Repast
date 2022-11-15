/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package repast;

import ROCBuilder.BaseStationResp;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;

/*
 * Responsible for updating each base stations's number of attached ue dynamica-
 * lly. Updating operation are accomplished by communication through socket 
 * with NS3.
 */
public class BaseStationController {
  private BaseStationContainer container;

  public BaseStationContainer getContainer() {
    return container;
  }

  public BaseStationController(BaseStationContainer container) {
    this.container = container;
  }

	@ScheduledMethod(start = 1, interval = 1)
  public void update() {
    Context<Object> context = ContextUtils.getContext(this);
		NS3CommunicatiorHelper ns3CommunicatiorHelper
			= (NS3CommunicatiorHelper) context
				.getObjects(NS3CommunicatiorHelper.class).get(0);
    for (BaseStation bs : container) {
      ns3CommunicatiorHelper.sendBaseStationReq(Integer.toString(bs.getId()));
      BaseStationResp resp = ns3CommunicatiorHelper.receiveBaseStationResp();
      bs.setNumberOfAttachedUe(Integer.parseInt(resp.getNumberOfUe()));
    }  
  }
}

