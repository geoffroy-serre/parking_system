package com.parkit.parkingsystem.customexceptions;

public class RegistrationLengthException extends Exception {

  
  private static final long serialVersionUID = 1L;

  /**
   * Use to throw excelption if registration number is more thant 10characters, or empty or null.
   * @param errorMessage String
   */
  public RegistrationLengthException(String errorMessage) {
    super(errorMessage);
  }
}
