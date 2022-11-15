/*
 * Created on Sun Oct 30 2022
 * @author Rui Zuo
 */

package ROCBuilder;

import static org.junit.Assert.assertEquals;
import java.nio.ByteBuffer;
import org.junit.Test;
import com.google.flatbuffers.FlatBufferBuilder;

import ROC.MessageType;

public class ActionReqTest {
    @Test
    public void testSerialize() {
      FlatBufferBuilder builder = new FlatBufferBuilder(0);
      ActionReq req = new ActionReq.Builder()
          .uavId("id")
          .lat("lat")
          .lng("lng").build();
      Message message = new Message.Builder()
          .flatBuffBuilder(builder)
          .type(MessageType.ActionReq)
          .payload(req.serialize(builder)).build();
      ByteBuffer buf = ByteBuffer.wrap(message.serialize());

      Message bar = new Message.Builder().build().deserialize(buf);
      ROC.ActionReq foo = (ROC.ActionReq) bar.getMessage().data(new ROC.ActionReq());
      assertEquals("id", foo.uavId());
    }
}
