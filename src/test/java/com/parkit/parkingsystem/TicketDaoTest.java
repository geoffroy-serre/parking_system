package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TicketDaoTest {

  private static DataBaseTestConfig dataBaseConfig = new DataBaseTestConfig();
  private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
  private static TicketDao ticketDAO;
  private static Ticket ticket;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @BeforeEach
  private void setUp() {
    ticketDAO = new TicketDao();
    ticketDAO.dataBaseConfig = dataBaseConfig;
    dataBasePrepareService.clearDataBaseEntries();
    ticket = new Ticket();

    LocalDateTime inTime = LocalDateTime.of(2020, 10, 9, 20, 10);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(new ParkingSpot(3, ParkingType.CAR, false));
    System.setOut(new PrintStream(outContent));

  }

  @AfterEach
  private void tearDown() {
    dataBasePrepareService.clearDataBaseEntries();
    System.setOut(System.out);
  }

  @Test
  public void test_No_Double_Reg_In_At_Same_TimeIsIn() {
    ticket.setVehicleRegNumber("NO_DOUBLE");
    ticketDAO.saveTicket(ticket);
    assertTrue(ticketDAO.isKnownRegistrationInParking("NO_DOUBLE"));
  }

  @Test
  public void test_No_Double_Reg_In_At_Same_TimeNotIn() {
    assertFalse(ticketDAO.isKnownRegistrationInParking("NODOUBLE"));
  }

  @Test
  public void saveTicket_and_getTicket_are_Identical_Test() {
    ticket.setVehicleRegNumber("SAVE_GET");
    ticketDAO.saveTicket(ticket);

    Ticket ticket2 = new Ticket();
    ticket2 = ticketDAO.getTicketToGetOut("SAVE_GET");

    assertEquals(ticket.getPrice(), ticket2.getPrice());
    assertEquals(ticket.getParkingSpot(), ticket2.getParkingSpot());
    assertEquals(ticket.getOutTime(), ticket2.getOutTime());
    assertEquals(ticket.getInTime(), ticket2.getInTime());
    assertEquals(ticket.getVehicleRegNumber(), ticket2.getVehicleRegNumber());

  }

  @Test
  public void noRegKnowToGetOutTest() {

    String message = "No vehicle to exit with this registration number";
    ticketDAO.getTicketToGetOut("SAVEGET");

    assertEquals(message, outContent.toString().trim());
  }

}
