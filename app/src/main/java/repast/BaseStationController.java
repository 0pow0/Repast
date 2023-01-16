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

  public BaseStationController() {
    this.container = BaseStationContainer.getInstance();
  }

	@ScheduledMethod(start = 1, interval = 1)
  public void update() {
    NS3CommunicatiorHelper ns3CommunicatiorHelper
      = new NS3CommunicatiorHelper();
    for (BaseStation bs : container) {
      ns3CommunicatiorHelper.sendBaseStationReq(Integer.toString(bs.getId()));
      BaseStationResp resp = ns3CommunicatiorHelper.receiveBaseStationResp();
      bs.setNumberOfAttachedUe(Integer.parseInt(resp.getNumberOfUe()));
      System.out.println("Send BaseStation Req " + bs.getId() + "\n" + bs);
    }  
  }
}

