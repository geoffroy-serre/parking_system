package com.parkit.parkingsystem.customexceptions;

public class RegistrationLengthException extends Exception {

  /**
   * use to Throw exception if submited registration is more than 10 characters.
   */
  private static final long serialVersionUID = 1L;

  public RegistrationLengthException(String errorMessage) {
    super(errorMessage);
  }
}
