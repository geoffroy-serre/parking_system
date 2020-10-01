package com.parkit.parkingsystem.service;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.constants.TimeFormat;
import com.parkit.parkingsystem.customexceptions.RegIsAlreadyParkedException;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * Contains methods used for vehicle entry / exit processing.
 * Get a method to verify the next parking spot available
 * @author Heimdall
 *
 */
public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private  TicketDAO ticketDAO;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeFormat.FRANCE);



	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	/**
	 * Save ticket for incomming vehicle in DB, and give in output to user the parking spot available.
	 * Information for the ticket are get by getVehicleRegistration() 
	 * getNextParkingNumberIfAvailable() is use to get vehcile type, and parking spot adapted for type and avability
	 * @throws Exception 
	 */
	public void processIncomingVehicle() throws RegIsAlreadyParkedException{

		ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
		String vehicleRegNumber = getVehicleRegNumber();


		if(parkingSpot !=null && parkingSpot.getId() > 0 && !ticketDAO.isKnownRegistrationInParking(vehicleRegNumber, false)){

			parkingSpot.setAvailable(false);
			/*
			 * allot this parking space and mark it's availability as false
			 */
			parkingSpotDAO.updateParking(parkingSpot);

			LocalDateTime inTime = LocalDateTime.now();

			Ticket ticket = new Ticket();
			//ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			//ticket.setId(ticketID);
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber(vehicleRegNumber);
			ticket.setPrice(0);
			ticket.setInTime(inTime);
			ticket.setOutTime(null);
			ticketDAO.saveTicket(ticket);
			System.out.println("Generated Ticket and saved in DB");
			System.out.println("Please park your vehicle in spot number: "+parkingSpot.getId());
			System.out.println("Recorded in-time for vehicle number: "+vehicleRegNumber+" is: "+simpleDateFormat.format(Timestamp.valueOf(inTime))+"\n");
		}

		else if (ticketDAO.isKnownRegistrationInParking(vehicleRegNumber, false)) {
			System.out.println("Vehicle seems to be already parked, try again\n");

			throw new RegIsAlreadyParkedException("Vehicle is Already parked");

		}

		else{
			System.out.println("Unable to process incoming vehicle");
			InteractiveShell.loadInterface();
		}
	} 



	private String getVehicleRegNumber() {
		System.out.println("Please type the vehicle registration number and press enter key:\n");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	/**
	 * Get the vehicle type by prompting user with getVehicleType()
	 * Choose a parking spot depending of vehicle type , and avability
	 * @return parkingSpot
	 */
	public ParkingSpot getNextParkingNumberIfAvailable(){
		int parkingNumber=0;
		ParkingSpot parkingSpot = null;
		try{
			ParkingType parkingType = getVehicleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if(parkingNumber > 0){
				parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
			}else{
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		}catch(IllegalArgumentException ie){
			logger.error("Error parsing user input for type of vehicle", ie);
		}catch(Exception e){
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	private ParkingType getVehicleType(){
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch(input){
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			//System.out.println("Incorrect input provided\n");
			getVehicleType();
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}

	/**
	 * Generate fare depending of parking duration, and type of vehicle
	 * Ticket information are retrieved from DB.
	 * Ticket information in DB is updated with price and outTime.
	 */
	public void processExitingVehicle() {


		/*TODO check if outime is null before exiting. If the reg is already in with outtime, exiting the same
			reg update the last one
		 */
		String vehicleRegNumber = getVehicleRegNumber();
		Ticket ticket=ticketDAO.getTicketToGetOut(vehicleRegNumber);

		LocalDateTime outTime = LocalDateTime.now();
		ticket.setOutTime(outTime);
		fareCalculatorService.calculateFare(ticket);
		if(ticketDAO.updateTicket(ticket)) {
			ParkingSpot parkingSpot = ticket.getParkingSpot();
			parkingSpot.setAvailable(true);
			parkingSpotDAO.updateParking(parkingSpot);
			if(ticket.getPrice() ==0) {
				System.out.println("You stayed less than 30mn. No fare to pay");
			}
			else {
				System.out.println("Please pay the parking fare: " + ticket.getPrice());
			}

			System.out.println("Recorded out-time for vehicle number: " + ticket.getVehicleRegNumber() + " is: " + simpleDateFormat.format(Timestamp.valueOf(outTime))+"\n");

		}
		else{
			System.out.println("Unable to update ticket information. Error occurred\n");
		}
	}
}

