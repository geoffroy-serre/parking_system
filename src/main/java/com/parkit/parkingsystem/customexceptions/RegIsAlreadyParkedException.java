package com.parkit.parkingsystem.customexceptions;

public class RegIsAlreadyParkedException extends Exception {

  private static final long serialVersionUID = 3076680978320246995L;

  /**
   * Throwed ifRegistration is already in parking.
   * 
   * @param errorMessage
   **/

  public RegIsAlreadyParkedException(String errorMessage) {
    super(errorMessage);
  }

}
