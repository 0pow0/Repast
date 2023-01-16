/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package repast;

import java.io.IOException;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import repast.simphony.batch.InstanceRunner;
import repast.simphony.scenario.ScenarioLoadException;

public class App {
  public static void main(String[] args)
    throws IOException, ScenarioLoadException, ModelNotFoundException,
    MalformedModelException {
    System.out.println("Working Directory = " + System.getProperty("user.dir"));
    InstanceRunner runner = new InstanceRunner();
    runner.configure(args);
    runner.run();
  }
}
