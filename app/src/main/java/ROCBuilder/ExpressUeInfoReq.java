package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class ExpressUeInfoReq {
  private String lng;
  private String lat;

  public String getLng() {
    return lng;
  }
  public void setLng(String lng) {
    this.lng = lng;
  }
  public String getLat() {
    return lat;
  }
  public void setLat(String lat) {
    this.lat = lat;
  }

  public static class Builder {
    private String lng;
    private String lat;

    public Builder lng(String lng) {
      this.lng = lng;
      return this;
    }
    public Builder lat(String lat) {
      this.lat = lat;
      return this;
    }
    public ExpressUeInfoReq build() {
      return new ExpressUeInfoReq(this);
    }
  }

  private ExpressUeInfoReq(Builder builder) {
    this.lng = builder.lng;
    this.lat = builder.lat;
  }

  public int serialize(FlatBufferBuilder builder) {
    int sLng = builder.createString(lng);
    int sLat = builder.createString(lat);
    ROC.ExpressUeInfoReq.startExpressUeInfoReq(builder);
    ROC.ExpressUeInfoReq.addLng(builder, sLng);
    ROC.ExpressUeInfoReq.addLat(builder, sLat);
    int req = ROC.ExpressUeInfoReq.endExpressUeInfoReq(builder);
    return req;
  }

  public ExpressUeInfoReq deserialize(Message message) {
    ROC.ExpressUeInfoReq req = (ROC.ExpressUeInfoReq) message.getMessage()
      .data(new ROC.ExpressUeInfoReq());
    lng = req.lng();
    lat = req.lat();
    return this;
  }
}
