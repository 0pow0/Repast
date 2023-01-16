package util;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

public class AppConf {
  private Config config;

  private static AppConf instance = null;

  private AppConf() {
    config = ConfigFactory.load("app");
  }

  public static Config getInstance() {
    if (instance == null) {
      instance = new AppConf();
    }
    return instance.config;
  } 
}
