/*
 * Created on Sun Nov 06 2022
 * @author Rui Zuo
 */

package repast;

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
    Context<Object> context = ContextUtils.getContext(this);
		NS3CommunicatiorHelper ns3CommunicatiorHelper
			= (NS3CommunicatiorHelper) context
				.getObjects(NS3CommunicatiorHelper.class).get(0);
    for (UserEquipment ue : container) {
      ns3CommunicatiorHelper.sendUserEquipmentReq(
        Integer.toString(ue.getUavId()));
      UserEquipmentResp resp = ns3CommunicatiorHelper
        .receiveUserEquipmentResp();
      ue.setAttachedBaseStationID(Integer.parseInt(resp.getAttachedEnbID()));
    }  
  }

  public UserEquipmentContainer getContainer() {
    return container;
  }
}
