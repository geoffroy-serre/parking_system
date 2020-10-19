package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.parkit.parkingsystem.customexceptions.RegistrationLengthException;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;



public class InputReaderUtilTest {

  public InputReaderUtil inputReaderUtil = new InputReaderUtil();
  

  @Test
  public void maxSizeInputAllowedTest() throws RegistrationLengthException {
    String regNumber = "TOOLOONGREG";
    // String test = inputReaderUtil.readVehicleRegistrationNumber(regNumber);
    assertThatExceptionOfType(RegistrationLengthException.class)
        .isThrownBy(() -> {
          inputReaderUtil.controlVehicleRegistrationNumber(regNumber);
        });
  }

  @Test
  public void noEmptyInputAllowedTest() throws RegistrationLengthException {
    String regNumber = "";
    // String test = inputReaderUtil.readVehicleRegistrationNumber(regNumber);
    assertThatExceptionOfType(RegistrationLengthException.class)
        .isThrownBy(() -> {
          inputReaderUtil.controlVehicleRegistrationNumber(regNumber);
        });

  }
  
  @Test
  public void noEmptySpaceInputAllowedTest() throws RegistrationLengthException {
    String regNumber = " ";
    // String test = inputReaderUtil.readVehicleRegistrationNumber(regNumber);
    assertThatExceptionOfType(RegistrationLengthException.class)
        .isThrownBy(() -> {
          inputReaderUtil.controlVehicleRegistrationNumber(regNumber);
        });

  }

}
