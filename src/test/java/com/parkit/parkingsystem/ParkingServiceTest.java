package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.customexceptions.ParkingIsFullException;
import com.parkit.parkingsystem.customexceptions.RegIsAlreadyParkedException;
import com.parkit.parkingsystem.customexceptions.RegistrationLengthException;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @Mock
  private static ParkingSpotDao parkingSpotDAO;

  @Mock
  private static ParkingSpot parkingSpot;
  @Mock
  private static TicketDao ticketDAO;

  @BeforeEach
  private void setUpPerTest() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  private void setOut() {
    System.setOut(System.out);
  }

  @Test
  public void processing_Incoming_Vehicle_Reccurring_Test()
      throws RegIsAlreadyParkedException, InterruptedException,
      RegistrationLengthException, ParkingIsFullException {
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ParkingService parkingService = new ParkingService(inputReaderUtil,
        parkingSpotDAO, ticketDAO);
    LocalDateTime inTime = LocalDateTime.now();
    ArgumentCaptor<Ticket> ticketArgCapt = ArgumentCaptor
        .forClass(Ticket.class);
    Ticket ticket = new Ticket();

    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("IN_VEHICLE");
    ticket.setOutTime(null);

    when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);
    when(parkingSpotDAO.isThereAvailableSlot(ParkingType.CAR)).thenReturn(true);
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    when(inputReaderUtil.controlVehicleRegistrationNumber(
        inputReaderUtil.getInputForVehicleRegNumber()))
            .thenReturn("IN_VEHICLE");

    parkingService.processIncomingVehicle();

    verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
    verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
    verify(ticketDAO, times(1)).isKnownRegistrationInParking(
        inputReaderUtil.controlVehicleRegistrationNumber(
            inputReaderUtil.getInputForVehicleRegNumber()));
    verify(ticketDAO, times(1)).saveTicket(ticketArgCapt.capture());
    Assertions.assertThat(ticketArgCapt.getValue().getPrice())
        .isEqualTo(ticket.getPrice());
    Assertions.assertThat(ticketArgCapt.getValue().getVehicleRegNumber())
        .isEqualTo(ticket.getVehicleRegNumber());
    Assertions.assertThat(ticketArgCapt.getValue().getParkingSpot())
        .isEqualTo(ticket.getParkingSpot());
    Assertions.assertThat(inTime).isEqualTo(ticket.getInTime());
    Assertions.assertThat(ticketArgCapt.getValue().getOutTime())
        .isEqualTo(ticket.getOutTime());
  }

  @Test
  public void processing_Incoming_Vehicle_Test()
      throws RegIsAlreadyParkedException, InterruptedException,
      RegistrationLengthException, ParkingIsFullException {
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ParkingService parkingService = new ParkingService(inputReaderUtil,
        parkingSpotDAO, ticketDAO);
    LocalDateTime inTime = LocalDateTime.now();
    ArgumentCaptor<Ticket> ticketArgCapt = ArgumentCaptor
        .forClass(Ticket.class);
    Ticket ticket = new Ticket();

    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("IN_VEHICLE");
    ticket.setOutTime(null);

    when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);
    when(parkingSpotDAO.isThereAvailableSlot(ParkingType.CAR)).thenReturn(true);
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    when(inputReaderUtil.controlVehicleRegistrationNumber(
        inputReaderUtil.getInputForVehicleRegNumber()))
            .thenReturn("IN_VEHICLE");

    parkingService.processIncomingVehicle();

    verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
    verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
    verify(ticketDAO, times(1)).isKnownRegistrationInParking(
        inputReaderUtil.controlVehicleRegistrationNumber(
            inputReaderUtil.getInputForVehicleRegNumber()));
    verify(ticketDAO, times(1)).saveTicket(ticketArgCapt.capture());
    Assertions.assertThat(ticketArgCapt.getValue().getPrice())
        .isEqualTo(ticket.getPrice());
    Assertions.assertThat(ticketArgCapt.getValue().getVehicleRegNumber())
        .isEqualTo(ticket.getVehicleRegNumber());
    Assertions.assertThat(ticketArgCapt.getValue().getParkingSpot())
        .isEqualTo(ticket.getParkingSpot());
    Assertions.assertThat(inTime).isEqualTo(ticket.getInTime());
    Assertions.assertThat(ticketArgCapt.getValue().getOutTime())
        .isEqualTo(ticket.getOutTime());
  }

  @Test
  void process_Exiting_Vehicle_Test()
      throws RegIsAlreadyParkedException, InterruptedException,
      RegistrationLengthException, ParkingIsFullException {
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    Ticket ticket = new Ticket();
    LocalDateTime inTime = LocalDateTime.of(2020, 10, 02, 11, 11);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("OUT_VEHICL");
    ticket.setOutTime(null);
    ticket.setPrice(0.0);

    when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);
    when(parkingSpotDAO.isThereAvailableSlot(ParkingType.CAR)).thenReturn(true);
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    when(ticketDAO.getTicketToGetOut(anyString())).thenReturn(ticket);
    when(ticketDAO.updateTicket(ticket)).thenReturn(true);
    when(inputReaderUtil.controlVehicleRegistrationNumber(
        inputReaderUtil.getInputForVehicleRegNumber()))
            .thenReturn("OUT_VEHICL");

    ParkingService parkingService = new ParkingService(inputReaderUtil,
        parkingSpotDAO, ticketDAO);

    parkingService.processIncomingVehicle();

    Thread.sleep(500);
    parkingService.processExitingVehicle();
    Ticket ticket2 = ticketDAO.getTicketToGetOut("OUT_VEHICL");

    assertNotNull(ticket2.getPrice());
    assertNotNull(ticket2.getOutTime());
    assertEquals(ticket2.getInTime(), ticket.getInTime());
    assertEquals(ticket2.getVehicleRegNumber(), ticket.getVehicleRegNumber());
    verify(parkingSpotDAO, times(2)).updateParking(parkingSpot);

  }

  @Test
  void process_Exiting_Vehicle_with_Price_Test()
      throws RegIsAlreadyParkedException, InterruptedException,
      RegistrationLengthException, ParkingIsFullException {
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    Ticket ticket = new Ticket();
    LocalDateTime inTime = LocalDateTime.of(2020, 10, 02, 11, 11);
    ticket.setInTime(inTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("OUT_VEHICL");
    ticket.setOutTime(null);
    ticket.setPrice(0.0);

    when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);
    when(parkingSpotDAO.isThereAvailableSlot(ParkingType.CAR)).thenReturn(true);
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    when(ticketDAO.getTicketToGetOut(anyString())).thenReturn(ticket);
    when(ticketDAO.updateTicket(ticket)).thenReturn(true);
    when(inputReaderUtil.controlVehicleRegistrationNumber(
        inputReaderUtil.getInputForVehicleRegNumber()))
            .thenReturn("OUT_VEHICL");

    ParkingService parkingService = new ParkingService(inputReaderUtil,
        parkingSpotDAO, ticketDAO);

    parkingService.processIncomingVehicle();

    Thread.sleep(500);
    parkingService.processExitingVehicle();
    Ticket ticket2 = ticketDAO.getTicketToGetOut("OUT_VEHICL");

    assertNotEquals(0.0, (ticket2.getPrice()));
    assertNotNull(ticket2.getOutTime());
    assertEquals(ticket2.getInTime(), ticket.getInTime());
    assertEquals(ticket2.getVehicleRegNumber(), ticket.getVehicleRegNumber());
    verify(parkingSpotDAO, times(2)).updateParking(parkingSpot);

  }

  @Test
  void process_Exiting_Vehicle_Test_A_Second_Time()
      throws RegistrationLengthException {
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ParkingService parkingService = new ParkingService(inputReaderUtil,
        parkingSpotDAO, ticketDAO);
    Ticket ticket = new Ticket();
    ticket.setInTime(LocalDateTime.of(2020, 10, 02, 11, 11));
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("OUT2");
    ticket.setOutTime(null);
    when(ticketDAO.getTicketToGetOut(anyString())).thenReturn(ticket);
    when(ticketDAO.updateTicket(ticket)).thenReturn(true);
    when(inputReaderUtil.controlVehicleRegistrationNumber(
        inputReaderUtil.getInputForVehicleRegNumber())).thenReturn("OUT2");
    parkingService.processExitingVehicle();
    verify(parkingSpotDAO, Mockito.times(1))
        .updateParking(any(ParkingSpot.class));
  }

}
