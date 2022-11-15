/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class CreationReq {
  private String uavId;
  private String lat;
  private String lng;
  private int masterId;

  public String getUavId() {
    return uavId;
  }

  public CreationReq setUavId(String uavId) {
    this.uavId = uavId;
    return this;
  }

  public String getLat() {
    return lat;
  }

  public CreationReq setLat(String lat) {
    this.lat = lat;
    return this;
  }

  public String getLng() {
    return lng;
  }

  public CreationReq setLng(String lng) {
    this.lng = lng;
    return this;
  }

  public int getMasterId() {
    return masterId;
  }

  public CreationReq setMasterId(int masterId) {
    this.masterId = masterId;
    return this;
  }

  public static class Builder {
    private String uavId;
    private String lat;
    private String lng;
    private int masterId;

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

    public Builder masterId(int masterId) {
      this.masterId = masterId;
      return this;
    }

    public Builder() {
    }

    public CreationReq build() {
      return new CreationReq(this);
    }
  }

  private CreationReq(Builder builder) {
    this.uavId = builder.uavId;
    this.lat = builder.lat;
    this.lng = builder.lng;
    this.masterId = builder.masterId;
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUavId = builder.createString(uavId);
    int sLat = builder.createString(lat);
    int sLng = builder.createString(lng);
    ROC.CreationReq.startCreationReq(builder);
    ROC.CreationReq.addUavId(builder, sUavId);
    ROC.CreationReq.addLatitude(builder, sLat);
    ROC.CreationReq.addLongitude(builder, sLng);
    ROC.CreationReq.addMasterId(builder, masterId);
    int creationInfo = ROC.CreationReq.endCreationReq(builder);
    return creationInfo;
  }

  public CreationReq deserialize(Message message) {
    ROC.CreationReq req = (ROC.CreationReq) message.getMessage()
        .data(new ROC.CreationReq());
    uavId = req.uavId();
    lat = req.latitude();
    lng = req.longitude();
    masterId = req.masterId();
    return this;
  }
}
