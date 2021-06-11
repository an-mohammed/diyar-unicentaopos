package com.openbravo.pos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openbravo.pos.forms.DataLogicSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionKeepAlive extends Thread {
	private static Logger log = LoggerFactory.getLogger("SessionKeepAlive");
  private int wait = 60000;
  private DataLogicSystem dataLogicSystem;
  private Boolean keepAlive;

  public SessionKeepAlive(DataLogicSystem dataLogicSystem) {
    this.dataLogicSystem = dataLogicSystem;
    keepAlive = true;
  }

  @Override
  public void run() {
     while (keepAlive) {
       try {
         log.info("Session Keep alive");
         dataLogicSystem.findVersion();
         Thread.sleep(wait);
       }
       catch (Exception ex) {
         keepAlive = false;
         log.error("Exception while keeping session alive: "+ex.getMessage());
       }
     }
  }
}
