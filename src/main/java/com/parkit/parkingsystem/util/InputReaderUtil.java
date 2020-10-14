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

  /**
   * Prompt for user to input vehicle registration number.
   * 
   * @return String input Registration
   * @throws RegistrationLengthException RegistrationLengthException
   */
  public String readVehicleRegistrationNumber() {
    String vehicleRegNumber = scan.nextLine();

    try {

      if (vehicleRegNumber == null
          || vehicleRegNumber.replaceAll("\\s+", "").length() == 0
          || vehicleRegNumber.length() > 10) {

        throw new RegistrationLengthException("Invalid input provided");

      }

    } catch (RegistrationLengthException e) {
      System.out.println("Registration must me less than 10 characters\n");
      logger.error("Registration must me less than 10 characters\n", e);

    }
    return vehicleRegNumber;

  }

}
