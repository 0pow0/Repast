/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.google.flatbuffers.FlatBufferBuilder;

// TODO Fill out
/*
 * Represents general message that can be used to communiscation with NS3.
 * Message types:
 *   - ActionReq: 
 *   - BaseStationReq: 
 *   - CreationReq: 
 *   - DeletionReq: 
 *   - SINRReq: 
 *   - ThroughputReq: 
 */
public class Message {

  private FlatBufferBuilder flatBufferBuilder;
  private byte type;
  private int payload;
  private ROC.Message message;

  public ROC.Message getMessage() {
    return message;
  }

  private Message(Builder builder) {
    this.flatBufferBuilder = builder.flatBufferBuilder;
    this.type = builder.type;
    this.payload = builder.payload;
    this.message = builder.message;
  }

  public static class Builder {
    private FlatBufferBuilder flatBufferBuilder;
    private byte type;
    private int payload;
    private ROC.Message message;

    public Builder() {
    }

    public Builder flatBuffBuilder(FlatBufferBuilder flatBufferBuilder) {
      this.flatBufferBuilder = flatBufferBuilder;
      return this;
    }

    public Builder type(byte type) {
      this.type = type;
      return this;
    }

    public Builder payload(int payload) {
      this.payload = payload;
      return this;
    }

    public Builder message(ROC.Message message) {
      this.message = message;
      return this;
    }

    public Message build() {
      return new Message(this);
    }
  }

  public byte[] serialize() {
    ROC.Message.startMessage(flatBufferBuilder);
    ROC.Message.addDataType(flatBufferBuilder, type);
    ROC.Message.addData(flatBufferBuilder, payload);
    int message = ROC.Message.endMessage(flatBufferBuilder);
    flatBufferBuilder.finish(message);
    byte[] buf = flatBufferBuilder.sizedByteArray();
    // Current messgae length is fixed at 1024
    // If less than 1024 bytes than copy and fill to 1024.
    if (buf.length < 1024) buf = Arrays.copyOf(buf, 1024);
    return buf;
  }
  
  public Message deserialize(ByteBuffer buf) {
    message = ROC.Message.getRootAsMessage(buf);
    return this;
  }
}