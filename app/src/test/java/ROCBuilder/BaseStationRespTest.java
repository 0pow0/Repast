/*
 * Created on Sun Oct 30 2022
 * @author Rui Zuo
 */
package ROCBuilder;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.google.flatbuffers.FlatBufferBuilder;

import ROC.EnbResp;
import ROC.MessageType;

public class BaseStationRespTest {
  @Test
  public void testDeserialize() {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    BaseStationResp resp = new BaseStationResp();
    resp.seteNBId("id");
    resp.setUsedRB("Rb");
    resp.setNumberOfUe("1");
    Message message = new Message.Builder()
        .flatBuffBuilder(builder)
        .type(MessageType.EnbResp)
        .payload(resp.serialize(builder))
        .build();
    ByteBuffer buf = ByteBuffer.wrap(message.serialize());

    message.deserialize(buf);
    BaseStationResp foo = new BaseStationResp().deserialize(message);
    assertEquals("id", foo.geteNBId());
  }

  @Test
  public void testSerialize() {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    BaseStationResp resp = new BaseStationResp();
    resp.seteNBId("id");
    resp.setUsedRB("Rb");
    resp.setNumberOfUe("1");
    Message message = new Message.Builder()
        .flatBuffBuilder(builder)
        .type(MessageType.EnbResp)
        .payload(resp.serialize(builder))
        .build();
    ByteBuffer buf = ByteBuffer.wrap(message.serialize());

    Message bar = new Message.Builder().build().deserialize(buf);
    EnbResp foo = (EnbResp) bar.getMessage().data(new EnbResp());
    assertEquals("id", foo.enbId());
  }
}
