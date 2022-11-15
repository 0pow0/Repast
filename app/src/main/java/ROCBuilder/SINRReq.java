/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class SINRReq {
  private String uavId;

  public String getUavId() {
    return uavId;
  }

  public SINRReq setUavId(String uavId) {
    this.uavId = uavId;
    return this;
  }

  private SINRReq(Builder builder) {
    this.uavId = builder.uavId;
  }

  public static class Builder {
    private String uavId;

    public Builder uavId(String uavId) {
      this.uavId = uavId;
      return this;
    }

    public SINRReq build() {
      return new SINRReq(this);
    }

    public Builder() {
    }
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUavId = builder.createString(uavId);
    ROC.SINRReq.startSINRReq(builder);
    ROC.SINRReq.addUavId(builder, sUavId);
    int sinrReq = ROC.SINRReq.endSINRReq(builder);
    return sinrReq;
  }

  public SINRReq deserialize(Message message) {
    ROC.SINRReq req = (ROC.SINRReq) message.getMessage()
        .data(new ROC.SINRReq());
    uavId = req.uavId();
    return this;
  }
}