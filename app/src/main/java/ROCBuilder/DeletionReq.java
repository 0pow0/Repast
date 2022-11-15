/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class DeletionReq {
  private String uavId;

  private DeletionReq(Builder builder) {
    this.uavId = builder.uavId;
  }

  public DeletionReq setUavId(String uavId) {
    this.uavId = uavId;
    return this;
  }

  public String getUavId() {
    return uavId;
  }

  public static class Builder {
    private String uavId;

    public Builder uavId(String uavId) {
      this.uavId = uavId;
      return this;
    }

    public DeletionReq build() {
      return new DeletionReq(this);
    }

    public Builder() {
    }
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUavId = builder.createString(uavId);
    ROC.DeletionReq.startDeletionReq(builder);
    ROC.DeletionReq.addUavId(builder, sUavId);
    int deletionInfo = ROC.DeletionReq.endDeletionReq(builder);
    return deletionInfo;
  }

  public DeletionReq deserialize(Message message) {
    ROC.DeletionReq req = (ROC.DeletionReq) message.getMessage()
        .data(new ROC.DeletionReq());
    uavId = req.uavId();
    return this;
  }
}
