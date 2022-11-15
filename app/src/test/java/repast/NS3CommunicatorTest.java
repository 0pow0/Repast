/*
 * Created on Sun Oct 30 2022
 * @author Rui Zuo
 */

package repast;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.google.flatbuffers.FlatBufferBuilder;

import ROC.MessageType;
import ROCBuilder.Message;
import ROCBuilder.SINRReq;

public class NS3CommunicatorTest {
    @Test
    public void testSend() throws UnknownHostException, IOException {
      /*
       * Manually start netcat first.
       */
      // NS3Communicator obj = new NS3Communicator();
      // obj.connect();
      // SINRReq req = new SINRReq.Builder().uavId("id").build();
      // FlatBufferBuilder builder = new FlatBufferBuilder(0);
      // Message message = new Message.Builder()
          // .flatBuffBuilder(builder)
          // .type(MessageType.SINRReq)
          // .payload(req.serialize(builder))
          // .build();
      // obj.send(message);
    }
}
