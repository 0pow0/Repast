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
  private double sinr;
  private double distance;
  private int cqi;

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

  public double getSinr() {
    return sinr;
  }

  public void setSinr(double sinr) {
    this.sinr = sinr;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public int getCqi() {
    return cqi;
  }

  public void setCqi(int cqi) {
    this.cqi = cqi;
  }
}
