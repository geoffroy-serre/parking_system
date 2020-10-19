package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.customexceptions.RegistrationLengthException;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputReaderUtil {

  private static Scanner scan = new Scanner(System.in, "UTF-8");
  private static final Logger logger = LogManager.getLogger("InputReaderUtil");
  

  /**
   * Prompt user numerical input.
   * 
   * @return int input sleection
   */
  public int readSelection() {
    try {
      return Integer.parseInt(scan.nextLine());

    } catch (NumberFormatException e) {
      logger.error(
          "Error while reading user input from Shell. NumberFormatException in InputReaderUtil readSelection()",
          e);
      System.out.println(
          "Error reading input. Please enter valid number for proceeding further\n");
      return -1;
    }
  }
  
  public String getInputForVehicleRegNumber() {
     
    return scan.nextLine();
  }

  /**
   * Control user input for registration number.
   * Reg must be less tor egal than 10characters and not empty or null
   * 
   * @param vehicleRegNumber String
   * @return vehicleRegNumber String
   * @throws RegistrationLengthException if the registration dont respect the rules
   */
  public String controlVehicleRegistrationNumber(String vehicleRegNumber)
      throws RegistrationLengthException {

    if (vehicleRegNumber == null
        || vehicleRegNumber.replaceAll("\\s+", "").length() == 0
        || vehicleRegNumber.length() > 10) {
      System.out.println(
          "Vehicle registration can not be empty or with more than 10 characters\n");
      throw new RegistrationLengthException("Invalid input provided");

    } else {
      return vehicleRegNumber;
    }

  }

}
