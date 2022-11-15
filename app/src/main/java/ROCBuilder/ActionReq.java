/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class ActionReq {
  private String uavId;
  private String lat;
  private String lng;

  public String getUavId() {
    return uavId;
  }

  public ActionReq setUavId(String uavId) {
    this.uavId = uavId;
    return this;
  }

  public String getLat() {
    return lat;
  }

  public ActionReq setLat(String lat) {
    this.lat = lat;
    return this;
  }

  public String getLng() {
    return lng;
  }

  public ActionReq setLng(String lng) {
    this.lng = lng;
    return this;
  }

  public static class Builder {
    private String uavId;
    private String lat;
    private String lng;

    public Builder() {
    }
    
    public Builder uavId(String uavId) {
      this.uavId = uavId;
      return this;
    }

    public Builder lat(String lat) {
      this.lat = lat;
      return this;
    }

    public Builder lng(String lng) {
      this.lng = lng;
      return this;
    }

    public ActionReq build() {
      return new ActionReq(this);
    }
  }

  private ActionReq(Builder build) {
    this.uavId = build.uavId;
    this.lat = build.lat;
    this.lng = build.lng;
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUavId = builder.createString(uavId);
    int sLat = builder.createString(lat);
    int sLng = builder.createString(lng);
    ROC.ActionReq.startActionReq(builder);
    ROC.ActionReq.addUavId(builder, sUavId);
    ROC.ActionReq.addLatitude(builder, sLat);
    ROC.ActionReq.addLongitude(builder, sLng);
    int actionReq = ROC.ActionReq.endActionReq(builder);
    return actionReq;
  }

  public ActionReq deserialize(Message message) {
    ROC.ActionReq req = (ROC.ActionReq) message.getMessage()
        .data(new ROC.ActionReq()); 
    uavId = req.uavId();
    lat = req.uavId();
    lng = req.longitude();
    return this;
  }
}
