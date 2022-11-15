/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package repast;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/*  
 * Read configuration JSON file for base stations and construct base station
 * array. 
 */
public class BaseStationContainer implements Iterable<BaseStation> {
  private ArrayList<BaseStation> baseStations;

  public BaseStationContainer(String filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    baseStations = new ArrayList<BaseStation>();
    JsonArray arr = JsonParser.parseReader(new FileReader(filePath)).
        getAsJsonObject().
        get("base stations").
        getAsJsonArray();
    Gson gson = new Gson();
    for (JsonElement e : arr) {
      JsonObject obj = e.getAsJsonObject();
      BaseStation bs = gson.fromJson(obj.toString(), BaseStation.class);
      baseStations.add(bs);
    }
  }

  public BaseStation get(int idx) {
    return baseStations.get(idx);
  }

  public int size() {
    return baseStations.size();
  }

  @Override
  public Iterator<BaseStation> iterator() {
    return new ContainerIterator();
  }

  class ContainerIterator implements Iterator<BaseStation> {

    private int index = 0;

    @Override
    public boolean hasNext() {
      return index < size();
    }

    @Override
    public BaseStation next() {
      return get(index++);
    }
  }

  @Override
  public String toString() {
    return "BaseStationContainer [baseStations=" + baseStations + "]";
  }
}
