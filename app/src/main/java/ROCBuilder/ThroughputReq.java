/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class ThroughputReq {
  private String uavId;

  public String getUavId() {
    return uavId;
  }

  public ThroughputReq setUavId(String uavId) {
    this.uavId = uavId;
    return this;
  }

  private ThroughputReq(Builder builder) {
    this.uavId = builder.uavId;
  }

  public static class Builder {
    private String uavId;

    public Builder uavId(String uavId) {
      this.uavId = uavId;
      return this;
    }

    public ThroughputReq build() {
      return new ThroughputReq(this);
    }

    public Builder() {
    }
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUavId = builder.createString(this.uavId);
    ROC.ThroughputReq.startThroughputReq(builder);
    ROC.ThroughputReq.addUavId(builder, sUavId);
    int throughputReq = ROC.ThroughputReq.endThroughputReq(builder);
    return throughputReq;
  }

  public ThroughputReq deserialize(Message message) {
    ROC.ThroughputReq req = (ROC.ThroughputReq) message
        .getMessage()
        .data(new ROC.ThroughputReq());
    uavId = req.uavId();
    return this;
  }
}
