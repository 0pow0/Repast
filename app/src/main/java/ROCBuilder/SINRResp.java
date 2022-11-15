/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class SINRResp {
  private String uavId;
  private String sinr;
  private String distance;
  private String cqi;
  private String enbId;

  public String getUavId() {
    return uavId;
  }

  public String getSinr() {
    return sinr;
  }

  public String getDistance() {
    return distance;
  }

  public String getCqi() {
    return cqi;
  }

  public String getEnb_id() {
    return enbId;
  }

  public SINRResp() {
  }

  public SINRResp deserialize(Message message) {
    ROC.SINRResp resp = (ROC.SINRResp) message.getMessage()
        .data(new ROC.SINRResp());
    uavId = resp.uavId();
    sinr = resp.sinr();
    distance = resp.distance();
    cqi = resp.cqi();
    enbId = resp.enbId();
    return this;
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUavId = builder.createString(uavId);
    int sSinr =  builder.createString(sinr);
    int sDistance = builder.createString(distance);
    int sCqi = builder.createString(cqi);
    int sEnbId = builder.createString(enbId);
    ROC.SINRResp.startSINRResp(builder);
    ROC.SINRResp.addUavId(builder, sUavId);
    ROC.SINRResp.addSinr(builder, sSinr);
    ROC.SINRResp.addDistance(builder, sDistance);
    ROC.SINRResp.addCqi(builder, sCqi);
    ROC.SINRResp.addEnbId(builder, sEnbId);
    int sinrResp = ROC.SINRResp.endSINRResp(builder);
    return sinrResp;
  }
}