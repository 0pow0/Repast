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

import util.AppConf;

/*  
 * Read configuration JSON file for base stations and construct base station
 * array. 
 */
public class BaseStationContainer implements Iterable<BaseStation> {
  private ArrayList<BaseStation> baseStations;
  private static BaseStationContainer container = null;
  public static String configFilePath;

  public static BaseStationContainer getInstance() {
    if (container == null) {
      try {
        container = new BaseStationContainer(configFilePath);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return container;
  }

  private BaseStationContainer(String filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    configFilePath = AppConf.getInstance().getString("repast.BaseStationContainer.baseStationConfigPath");
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
