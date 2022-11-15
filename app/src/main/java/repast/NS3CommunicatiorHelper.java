/*
 * Created on Sun Nov 06 2022
 * @author Rui Zuo
 */

package repast;

import com.google.flatbuffers.FlatBufferBuilder;

import ROC.MessageType;
import ROCBuilder.ActionReq;
import ROCBuilder.BaseStationReq;
import ROCBuilder.BaseStationResp;
import ROCBuilder.CreationReq;
import ROCBuilder.DeletionReq;
import ROCBuilder.Message;
import ROCBuilder.UserEquipmentReq;
import ROCBuilder.UserEquipmentResp;

/*
 * Helper class to generate different message type and send.
 */
public class NS3CommunicatiorHelper {
  private NS3Communicator ns3Communicator;

  public NS3CommunicatiorHelper(NS3Communicator communicator) {
    this.ns3Communicator = communicator;
  }

  private void send(Message message) {
    try {
      this.ns3Communicator.send(message);
    } catch (Exception e) {
      System.out.println("private void send(Message message)");
      System.out.println(e);
    }
  }

  private Message generateMessage(FlatBufferBuilder builder, byte type,
    int payload) {
    return new Message.Builder()
      .flatBuffBuilder(builder)
      .type(type)
      .payload(payload)
      .build();
  }

  private Message receiveMessgae() {
    Message message;
    try {
      message = ns3Communicator.receive();
    } catch (Exception e) {
      System.out.println("private Message receiveMessgae()");
      message = null; 
      System.out.println(e);
    }
    return message; 
  }

  public Message sendUserEquipmentReq(String ueID) {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    UserEquipmentReq req = new UserEquipmentReq.Builder().ueID(ueID).build();
    Message message = generateMessage(builder, MessageType.UeReq,
      req.serialize(builder));
    send(message);
    return message;
  }

  public UserEquipmentResp receiveUserEquipmentResp() {
    Message message = receiveMessgae();
    return new UserEquipmentResp().deserialize(message);
  }

  public Message sendBaseStationReq(String enbID) {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    BaseStationReq req = new BaseStationReq.Builder().eNBId(enbID).build();
    Message message = generateMessage(builder, MessageType.EnbReq,
      req.serialize(builder));
    send(message);
    return message;
  }

  //FIXME What if the message type mismatch?
  public BaseStationResp receiveBaseStationResp() {
    Message message = receiveMessgae();
    return new BaseStationResp().deserialize(message);
  }

  public Message sendCreationReq(String uavID, String lat, String lng,
    int masterId) {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    CreationReq req = new CreationReq.Builder()
      .uavId(uavID)
      .lat(lat)
      .lng(lng)
      .masterId(masterId)
      .build();
    Message message = generateMessage(builder,
      MessageType.CreationReq,
      req.serialize(builder));
    send(message);
    return message;
  }

  public Message sendDeletionReq(String uavID) {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    DeletionReq req = new DeletionReq.Builder()
      .uavId(uavID)
      .build();
    Message message = generateMessage(builder,
      MessageType.DeletionReq,
      req.serialize(builder));
    send(message);
    return message;
  }

  public Message sendActionReq(String uavID, String lat, String lng) {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    ActionReq req = new ActionReq.Builder()
      .uavId(uavID)
      .lat(lat)
      .lng(lng)
      .build();
    Message message = generateMessage(builder,
      MessageType.ActionReq,
      req.serialize(builder));
    send(message);
    return message;
  }
}
