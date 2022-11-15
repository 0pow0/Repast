/*
 * Created on Sun Nov 06 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

import ROC.UeReq;

public class UserEquipmentReq {
  private String ueID;

  public String getUeID() {
    return ueID;
  }

  public void setUeID(String ueID) {
    this.ueID = ueID;
  }

  public static class Builder {
    private String ueID;

    public Builder ueID(String ueID) {
      this.ueID = ueID;
      return this;
    }

    public UserEquipmentReq build() {
      return new UserEquipmentReq(this);
    }
  }

  private UserEquipmentReq(Builder builder) {
    this.ueID = builder.ueID;
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUeID = builder.createString(ueID);
    UeReq.startUeReq(builder);
    UeReq.addUeId(builder, sUeID);
    int ueReq = UeReq.endUeReq(builder);
    return ueReq;
  }

  public UserEquipmentReq deserialize(Message message) {
    UeReq req = (UeReq) message.getMessage()
      .data(new UeReq());
    ueID = req.ueId();
    return this;
  }
}
