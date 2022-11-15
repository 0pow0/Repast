/*
 * Created on Sun Nov 06 2022
 * @author Rui Zuo
 */

package repast;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * UE container
 */
public class UserEquipmentContainer implements Iterable<UserEquipment> {
  private ArrayList<UserEquipment> userEquipments;
  
  public UserEquipmentContainer() {
    userEquipments = new ArrayList<>();
  }

  public int size() {
    return userEquipments.size();
  }

  public boolean add(UserEquipment ue) {
    return userEquipments.add(ue);
  }

  public UserEquipment get(int index) {
    return userEquipments.get(index);
  }

  public void remove(UserEquipment ue) {
    userEquipments.remove(ue);
  }

  @Override
  public Iterator<UserEquipment> iterator() {
    return new ContainerIterator();
  }

  class ContainerIterator implements Iterator<UserEquipment> {

    private int index = 0;

    @Override
    public boolean hasNext() {
      return index < size();
    }

    @Override
    public UserEquipment next() {
      return get(index++);
    }
  }
}
