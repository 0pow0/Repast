/*
 * Created on Sun Nov 06 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

import ROC.UeResp;

public class UserEquipmentResp {
  private String ueID;  
  private String attachedEnbID;

  public String getUeID() {
    return ueID;
  }

  public void setUeID(String ueID) {
    this.ueID = ueID;
  }

  public String getAttachedEnbID() {
    return attachedEnbID;
  }

  public void setAttachedEnbID(String attachedEnbID) {
    this.attachedEnbID = attachedEnbID;
  }

  public UserEquipmentResp() {
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUeID = builder.createString(ueID);
    int sAttachedEnbID = builder.createString(attachedEnbID);
    UeResp.startUeResp(builder);
    UeResp.addUeId(builder, sUeID);
    UeResp.addAttachedEnbId(builder, sAttachedEnbID);
    int resp = UeResp.endUeResp(builder);
    return resp;
  }

  public UserEquipmentResp deserialize(Message message) {
    UeResp resp = (UeResp) message.getMessage()
      .data(new UeResp());
    ueID = resp.ueId();
    attachedEnbID = resp.attachedEnbId();
    return this;
  }

  @Override
  public String toString() {
    return "UserEquipmentResp [ueID=" + ueID + ", attachedEnbID=" + attachedEnbID + "]";
  }
}
