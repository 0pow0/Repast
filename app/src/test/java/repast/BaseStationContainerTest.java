/*
 * Created on Fri Oct 28 2022
 * @author Rui Zuo
 */

package repast;

import org.junit.Test;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

public class BaseStationContainerTest {
  @Test
  public void appHasAGreeting() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    BaseStationContainer classUnderTest = new BaseStationContainer(
        "/home/rzuo02/work/repast/app/src/test" +
            "/resources/base-stations.json");
    BaseStation bs = classUnderTest.get(0);
    assertEquals("test id", 1, bs.getId());
    assertEquals("test lat", -102.939365, bs.getLng(), 0.0001);
  }

  @Test
  public void testIterator() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    BaseStationContainer container = new BaseStationContainer(
      "/home/rzuo02/work/repast/app/src/test/resources/" +
      "base-stations.json");
    for (BaseStation bs : container) {
      assertEquals("test id", 1, bs.getId());
    }
  }
}
