package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.constants.TimeFormat;
import com.parkit.parkingsystem.customexceptions.ParkingIsFullException;
import com.parkit.parkingsystem.customexceptions.RegIsAlreadyParkedException;
import com.parkit.parkingsystem.customexceptions.RegistrationLengthException;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains methods used for vehicle entry / exit processing. Have a method to
 * verify the next parking spot available Have a method to get vehicle's
 * registration
 * 
 * @author Heimdall
 *
 */
public class ParkingService {

  private static final Logger logger = LogManager.getLogger("ParkingService");

  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDao parkingSpotDao;
  private TicketDao ticketDao;
  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
      TimeFormat.DATE_FORMAT_FRANCE);

  /**
   * Use the parking service.
   * 
   * @param inputReaderUtil User input
   * @param parkingSpotDao  ParkingSpotDao
   * @param ticketDao       TicketDao
   **/
  public ParkingService(InputReaderUtil inputReaderUtil,
      ParkingSpotDao parkingSpotDao, TicketDao ticketDao) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDao = parkingSpotDao;
    this.ticketDao = ticketDao;
  }

  /**
   * Save ticket for incomming vehicle in DB, and give in output to user the
   * parking spot available. Information for the ticket are get by
   * getVehicleRegistration() getNextParkingNumberIfAvailable() is use to get
   * vehcile type, and parking spot adapted for type and avability
   * 
   * @throws RegIsAlreadyParkedException Car already in parking
   * @throws RegistrationLengthException Registration null ,0 or >10
   * @throws ParkingIsFullException
   **/
  public void processIncomingVehicle() throws RegIsAlreadyParkedException,
      RegistrationLengthException, ParkingIsFullException {
    ParkingType parkingType = getVehicleType();

    if (!parkingSpotDao.isThereAvailableSlot(parkingType)) {
      System.out.println("Parking Full");
      throw new ParkingIsFullException("Parkingfull for: "
                                       + parkingType.toString());
    } else {
      ParkingSpot parkingSpot = getNextParkingNumber(parkingType);
      String vehicleRegNumber = getVehicleRegNumber();

      if (parkingSpot != null && parkingSpot.getId() > 0
          && !ticketDao.isKnownRegistrationInParking(vehicleRegNumber)) {
        parkingSpot.setAvailable(false);
        /*
         * allot this parking space and mark it's availability as false
         */
        parkingSpotDao.updateParking(parkingSpot);
        LocalDateTime inTime = LocalDateTime.now();
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticketDao.saveTicket(ticket);
        logger.info("Generated Ticket and saved in DB");

        if (ticketDao.isRecurringRegistration(vehicleRegNumber)) {
          System.out.println(
              "Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
        }

        System.out.println("Please park your vehicle in spot number: "
                           + parkingSpot.getId());
        logger.info("Recorded in-time for vehicle number: "
                    + vehicleRegNumber
                    + " is: "
                    + simpleDateFormat.format(Timestamp.valueOf(inTime))
                    + "\n");
      } else if (ticketDao.isKnownRegistrationInParking(vehicleRegNumber)) {
        System.out.println("Vehicle seems to be already parked, try again\n");
        throw new RegIsAlreadyParkedException(
            "Vehicle is Already parked in ParkingService processIncomingVehicle()");
      } else {
        System.out.println("Unable to process incoming vehicle");
        // InteractiveShell.loadInterface();
      }
    }
  }

  /**
   * Prompt user for vehicle's registration input.
   * 
   * @return String getVehicleRegNumber
   * @throws RegistrationLengthException RegistrationLengthException
   */
  public String getVehicleRegNumber() throws RegistrationLengthException {
    System.out.println(
        "Please type the vehicle registration number and press enter key:");
    return inputReaderUtil.controlVehicleRegistrationNumber(
        inputReaderUtil.getInputForVehicleRegNumber());
  }

  /**
   * Get the vehicle type by prompting user with getVehicleType() Choose a
   * parking spot depending of vehicle type , and avability.
   * 
   * @return parkingSpot getNextParkingNumberIfAvailable
   */
  public ParkingSpot getNextParkingNumber(ParkingType parkingType) {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;

    parkingNumber = parkingSpotDao.getNextAvailableSlot(parkingType);
    if (parkingNumber > 0) {
      parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
    }
    return parkingSpot;
  }

  /**
   * Prompt userfor vehicle type.
   * 
   * @return Parking.Type
   */
  private ParkingType getVehicleType() {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");
    int input = inputReaderUtil.readSelection();
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }

      case 2: {
        return ParkingType.BIKE;
      }

      default: {
        getVehicleType();
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }

  /**
   * Generate price, set parking spot available back.
   * 
   * @throws RegistrationLengthException Registration issue
   */
  public void processExitingVehicle() throws RegistrationLengthException {

    String vehicleRegNumber = getVehicleRegNumber();
    Ticket ticket = ticketDao.getTicketToGetOut(vehicleRegNumber);
    if (ticket == null) {
      System.out.println("Please try again\n");
      InteractiveShell.loadInterface();
    } else {
      LocalDateTime outTime = LocalDateTime.now();
      ticket.setOutTime(outTime);
      fareCalculatorService.calculateFare(ticket);
      if (ticketDao.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDao.updateParking(parkingSpot);
        if (ticket.getPrice() == 0) {
          System.out.println("You stayed less than 30mn. No fare to pay");
        } else {
          System.out.println("Please pay the parking fare: "
                             + ticket.getPrice()
                             + "\n");
        }
        logger.info("Recorded out-time for vehicle number: "
                    + ticket.getVehicleRegNumber()
                    + " is: "
                    + simpleDateFormat.format(Timestamp.valueOf(outTime))
                    + "\n");
      } else {
        System.out
            .println("Unable to update ticket information. Error occurred\n");
      }

    }
  }
}
