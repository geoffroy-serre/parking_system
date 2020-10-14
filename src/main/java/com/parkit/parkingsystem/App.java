package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Starting point of program.
 * 
 * @author heimdall
 * 
 *
 */
public class App {
  private static final Logger logger = LogManager.getLogger("App");

  /**
   * Starting method of the app.
   * 
   * @param args args
   */
  public static void main(String[] args) {
    logger.info("Initializing Parking System");
    InteractiveShell.loadInterface();

  }
}
