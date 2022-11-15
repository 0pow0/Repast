/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

import ROC.EnbResp;

public class BaseStationResp {
  private String eNBId;
  private String usedRB;
  private String numberOfUe;

  public BaseStationResp seteNBId(String eNBId) {
    this.eNBId = eNBId;
    return this;
  }

  public BaseStationResp setUsedRB(String usedRB) {
    this.usedRB = usedRB;
    return this;
  }

  public BaseStationResp setNumberOfUe(String numberOfUe) {
    this.numberOfUe = numberOfUe;
    return this;
  }

  public String geteNBId() {
    return eNBId;
  }

  public String getUsedRB() {
    return usedRB;
  }

  public String getNumberOfUe() {
    return numberOfUe;
  }

  public BaseStationResp() {
  }

  public BaseStationResp deserialize(Message message) {
    EnbResp resp = (EnbResp) message.getMessage().data(new EnbResp());
    eNBId = resp.enbId();
    usedRB = resp.usedRb();
    numberOfUe = resp.numberOfUe();
    return this;
  }

  public int serialize(FlatBufferBuilder builder) {
    int sEnbId = builder.createString(eNBId);
    int sUsedRB = builder.createString(usedRB);
    int sNumberOfUe = builder.createString(numberOfUe);
    EnbResp.startEnbResp(builder);
    EnbResp.addEnbId(builder, sEnbId);
    EnbResp.addUsedRb(builder, sUsedRB); 
    EnbResp.addNumberOfUe(builder, sNumberOfUe);
    int enbResp = EnbResp.endEnbResp(builder);
    return enbResp;
  }

  @Override
  public String toString() {
    return "BaseStationResp [eNBId=" + eNBId + ", usedRB=" + usedRB + ", numberOfUe=" + numberOfUe + "]";
  }
}
