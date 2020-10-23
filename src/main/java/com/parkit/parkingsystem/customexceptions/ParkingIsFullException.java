package com.parkit.parkingsystem.customexceptions;

public class ParkingIsFullException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Use to Throw exception if parking is full.
   */
  public ParkingIsFullException(String errorMessage) {
    super(errorMessage);
  }
}
