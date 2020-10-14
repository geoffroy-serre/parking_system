package com.parkit.parkingsystem.customexceptions;

public class ParkingIsFullException extends Exception {

  /**
   * use to Throw exception if parking is full.
   */
  private static final long serialVersionUID = 1L;

  public ParkingIsFullException(String errorMessage) {
    super(errorMessage);
  }
}
