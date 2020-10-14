package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.customexceptions.ParkingIsFullException;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingDAOTest {

  private static ParkingSpotDAO parkingSpotDAO;
  private static ParkingSpot parkingSpot;
  private static DataBaseTestConfig dataBaseConfig = new DataBaseTestConfig();
  private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();

  @Mock
  private static DataBaseTestConfig dataBaseConfigMock = new DataBaseTestConfig();

  @BeforeEach
  private void setUp() {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseConfig;
    dataBasePrepareService.clearDataBaseEntries();
  }

  @AfterEach
  private void tearDown() {
    dataBasePrepareService.clearDataBaseEntries();
  }

  @Test
  public void shouldReturnParkingSpotNumberOne()
      throws ParkingIsFullException, InterruptedException {
    int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
    assertEquals(1, result);
  }

  @Test
  public void updateParkingShouldReturnParkingSpotNumber3After2VehiculeIncoming()
      throws ParkingIsFullException, InterruptedException {
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);
    // ACT
    parkingSpotDAO.updateParking(parkingSpot);
    parkingSpotDAO.updateParking(parkingSpot2);
    int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

    // ASSERT
    assertEquals(3, result);
  }

  @Test
  public void shouldReturnParkingFull() throws ParkingIsFullException {
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);
    ParkingSpot parkingSpot3 = new ParkingSpot(3, ParkingType.CAR, false);
    parkingSpotDAO.updateParking(parkingSpot);
    parkingSpotDAO.updateParking(parkingSpot2);
    parkingSpotDAO.updateParking(parkingSpot3);

    assertEquals(0, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    // assertEquals(0, result);
  }

  @Test
  public void updateParkingShouldReturnParkingSpotNumber1After2VehiculeIncomingAndFirstExiting()
      throws ParkingIsFullException, InterruptedException {
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);

    parkingSpotDAO.updateParking(parkingSpot);
    parkingSpotDAO.updateParking(parkingSpot2);
    parkingSpot.setAvailable(true);
    parkingSpotDAO.updateParking(parkingSpot);
    int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

    assertEquals(1, result);
  }

  @Test
  public void updateParkingShouldReturnException_When_DB_ConnectionFail()
      throws ClassNotFoundException, SQLException, IOException,
      ParkingIsFullException, InterruptedException {

    ParkingSpotDAO parkingSpotDAO3 = new ParkingSpotDAO();
    parkingSpotDAO3.dataBaseConfig = dataBaseConfigMock;
    ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.CAR, false);
    when(dataBaseConfigMock.getConnection())
        .thenThrow(new SQLException("Error occurred"));

    parkingSpotDAO3.updateParking(parkingSpot2);
    parkingSpotDAO3.getNextAvailableSlot(ParkingType.CAR);
    verify(dataBaseConfigMock, times(2)).getConnection();
  }

}
