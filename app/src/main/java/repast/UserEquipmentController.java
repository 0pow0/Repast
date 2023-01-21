/*
 * Created on Sun Nov 06 2022
 * @author Rui Zuo
 */

package repast;

import ROCBuilder.SINRResp;
import ROCBuilder.UserEquipmentResp;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;

/*
 * Responsible for updating each user equipment's attached base station dynamic-
 * ally. Updating operation are accomplished by communication through socket 
 * with NS3.
 */
public class UserEquipmentController {
  private UserEquipmentContainer container;

  public UserEquipmentController(UserEquipmentContainer container) {
    this.container = container;
  }

  public UserEquipmentController() {
    this.container = new UserEquipmentContainer();
  }
  
	@ScheduledMethod(start = 1, interval = 1)
  public void update() {
    NS3CommunicatiorHelper ns3CommunicatiorHelper
      = new NS3CommunicatiorHelper();
    for (UserEquipment ue : container) {
      System.out.println("Send SINR Req " + ue.getUavId());
      ns3CommunicatiorHelper.sendSINRReq(Integer.toString(ue.getUavId()));
      SINRResp resp = ns3CommunicatiorHelper.receiveSINRResp();
      ue.setSinr(Double.parseDouble(resp.getSinr()));
      ue.setCqi(Integer.parseInt(resp.getCqi()));
      ue.setAttachedBaseStationID(Integer.parseInt(resp.getEnbId()));
      ue.setDistance(Double.parseDouble(resp.getDistance()));
    }  
  }

  public UserEquipmentContainer getContainer() {
    return container;
  }
}
