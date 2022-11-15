/*
 * Created on Thu Oct 27 2022
 * @author Rui Zuo
 */

package repast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ROCBuilder.Message;

/*
 * Responsible for communication with NS3 through socket. Read and write
 * serialized data from/to NS3. Serialize data into different specific message
 * type. Decode incoming message into specific message type. Message type are
 * defined in ROC package with the help of google flatbuffers.
 */
public class NS3Communicator {
  private String addr = "127.0.0.1";
  private int port = 62700;
  private Socket socket;
  private OutputStream os; // Output stream to write to socket
  private InputStream is; // Input stream to read from socket
  private Sender sender;
  private Receiver receiver;

  public String getAddr() {
    return addr;
  }

  public NS3Communicator setAddr(String addr) {
    this.addr = addr;
    return this;
  }

  public int getPort() {
    return port;
  }

  public NS3Communicator setPort(int port) {
    this.port = port;
    return this;
  }

  public NS3Communicator() {
  }  

  public NS3Communicator(int port) {
    this.port = port;
  }

  public NS3Communicator connect() throws UnknownHostException, IOException {
    if (socket != null) return this;
    socket = new Socket(addr, port);
    os = new DataOutputStream(socket.getOutputStream()); 
    is = new DataInputStream(socket.getInputStream());
    sender = new Sender(os);
    receiver = new Receiver(is);
    return this;
  }

  public void send(Message message) throws IOException {
    sender.send(message);
  } 

  public Message receive() throws IOException {
    return receiver.receive();
  }
}

/*
 * Serialize incoming data, write and send to output stream of socket.
 */
class Sender {
  private OutputStream os;

  public Sender(OutputStream os) {
    this.os = os;
  } 

  public void send(Message message) throws IOException {
    byte[] buf = message.serialize();
    os.write(buf, 0, buf.length);
    os.flush();
  }
}

/*
 * Read and deserialize incoming data. Return Message type.
 */
class Receiver {
  private InputStream is;

  public Receiver(InputStream is) {
    this.is = is;
  }

  /*
   * When read from socket, first read 4 bytes to get length of message of type
   * int32; Then read that specific length of bytes from socket again.
   */
  private ByteBuffer read() throws IOException {
    byte[] len = new byte[4];
		byte[] bytes;
		ByteBuffer buf;
		synchronized (is) {
			int n1 = is.read(len, 0, 4);
			if (n1 == -1)
				throw new SocketException("[n1=-1] No byte is available");
			buf = ByteBuffer.wrap(len);
			int N = buf.getInt();
			bytes = new byte[N];
			int n2 = is.read(bytes, 0, N);
			if (n2 == -1)
				throw new SocketException("[n2=-1] No byte is available");
    }
		buf = ByteBuffer.wrap(bytes);
		return buf;
  }

  public Message receive() throws IOException {
    ByteBuffer buf = read();
    return new Message.Builder().build().deserialize(buf);
  }
}
