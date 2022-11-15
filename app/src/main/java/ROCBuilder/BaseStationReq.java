/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import ROC.EnbReq;
import com.google.flatbuffers.FlatBufferBuilder;

public class BaseStationReq {
  private String eNBId;

  public String getEnBID() {
    return eNBId;
  }

  public BaseStationReq setEnBID(String eNBId) {
    this.eNBId = eNBId;
    return this;
  }

  private BaseStationReq(Builder builder) {
    this.eNBId = builder.eNBId;
  }

  public static class Builder {
    private String eNBId;

    public Builder() {
    }

    public Builder eNBId(String eNBId) {
      this.eNBId = eNBId;
      return this;
    }

    public BaseStationReq build() {
      return new BaseStationReq(this);
    }
  }

  public int serialize(FlatBufferBuilder builder) {
    int sEnbId = builder.createString(eNBId);
    EnbReq.startEnbReq(builder);
    EnbReq.addEnbId(builder, sEnbId);
    int eNBReq = EnbReq.endEnbReq(builder);
    return eNBReq;
  }

  public BaseStationReq deserialize(Message message) {
    EnbReq req = (EnbReq) message.getMessage().data(new EnbReq());
    eNBId = req.enbId();
    return this;
  }

  @Override
  public String toString() {
    return "BaseStationReq [eNBId=" + eNBId + "]";
  }
}