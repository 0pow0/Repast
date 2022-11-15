/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import com.google.flatbuffers.FlatBufferBuilder;

public class ThroughputResp {
  private String uavId;
  private String throughput;
  private String timeStamp;
  private String totalBytes;

  public String getUavId() {
    return uavId;
  }

  public String getThroughput() {
    return throughput;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public String getTotalBytes() {
    return totalBytes;
  }

  public ThroughputResp() {
  }

  public ThroughputResp deserialize(Message message) {
    ROC.ThroughputResp resp = (ROC.ThroughputResp) message.getMessage()
        .data(new ROC.ThroughputResp());
    uavId = resp.uavId();
    throughput = resp.throughput();
    timeStamp = resp.timeStamp();
    totalBytes = resp.totalBytes();
    return this;
  }

  public int serialize(FlatBufferBuilder builder) {
    int sUavId = builder.createString(uavId);
    int sThrpt = builder.createString(throughput);
    int sTimeStamp = builder.createString(timeStamp);
    int sTotalBytes = builder.createString(totalBytes);
    int sNoConn = builder.createString("");
    ROC.ThroughputResp.startThroughputResp(builder);
    ROC.ThroughputResp.addUavId(builder, sUavId);
    ROC.ThroughputResp.addThroughput(builder, sThrpt);
    ROC.ThroughputResp.addTimeStamp(builder, sTimeStamp);
    ROC.ThroughputResp.addTotalBytes(builder, sTotalBytes);
    ROC.ThroughputResp.addNoConnection(builder, sNoConn);
    int resp = ROC.ThroughputResp.endThroughputResp(builder);
    return resp;
  }
}
