/*
 * Created on Sun Nov 06 2022
 * @author Rui Zuo
 */

package repast;

/*
 * Represents User Equipment(UE) contains infomation about:
 * 1) Which base station it connected
 */
public class UserEquipment {
  private int uavId;
  private int attachedBaseStationID; 

  public int getUavId() {
    return uavId;
  }

  public UserEquipment(int uavId) {
    this.uavId = uavId;
    attachedBaseStationID = -1;
  }

  public int getAttachedBaseStationID() {
    return attachedBaseStationID;
  }

  public void setAttachedBaseStationID(int attachedBaseStationID) {
    this.attachedBaseStationID = attachedBaseStationID;
  }
}
