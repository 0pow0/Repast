/*
 * Created on Sun Oct 30 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.google.flatbuffers.FlatBufferBuilder;

import ROC.EnbReq;
import ROC.MessageType;

public class BaseStationReqTest {
    @Test
    public void testSerialize() {
      FlatBufferBuilder builder = new FlatBufferBuilder(0);
      BaseStationReq req = new BaseStationReq.Builder()
          .eNBId("id")
          .build();
      Message message = new Message.Builder()
          .flatBuffBuilder(builder)
          .type(MessageType.EnbReq)
          .payload(req.serialize(builder)).build();
      ByteBuffer buf = ByteBuffer.wrap(message.serialize());

      Message bar = new Message.Builder().build().deserialize(buf);
      EnbReq foo = (EnbReq) bar.getMessage().data(new EnbReq());
      assertEquals("id", foo.enbId());
    }
}
