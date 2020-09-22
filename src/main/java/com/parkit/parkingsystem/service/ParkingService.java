package com.parkit.parkingsystem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
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
    private DataBaseConfig dataBaseConfig;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    /**
     * Look in DB if a Car already have a ticket with the choosen parameters
     * Return true if the given informations are found at least one time
     * @param reg
     * @param outTime
     * @return boolean
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Boolean isKnownRegistrationInParking(String reg, LocalDateTime outTime) throws ClassNotFoundException, SQLException  {
    	
    	Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
		
			
			PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE VEHICLE_REG_NUMBER =? AND OUT_TIME=?");
			ps.setString(1,  reg);
			ps.setTimestamp(2, Timestamp.valueOf(outTime));
			ResultSet rs = ps.executeQuery();
					
			return rs.next();
		}
		finally {
			
		}
		//CATCH /FINALLY
    	
    }
   
    /**
     * Save ticket for incomming vehicle in DB, and give in output to user the parking spot available.
     * Information for the ticket are get by getVehicleRegistration() 
     * getNextParkingNumberIfAvailable() is use to get vehcile type, and parking spot adapted for type and avability
     */
    public void processIncomingVehicle() {
        try{
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            String vehicleRegNumber = getVehicleRegNumber();
           if(parkingSpot !=null && parkingSpot.getId() > 0  ){
            	
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
                System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber+" is:"+inTime+"\n");
            }
           else {
        	
			System.out.println("Unable to process incoming vehicle");
			InteractiveShell.loadInterface();
		}
           
        }catch(Exception e){
            logger.error("Unable to process incoming vehicle",e);
        }
    }

    private String getVehicleRegNumber() throws Exception {
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
                System.out.println("Incorrect input provided\n");
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
        try{
            String vehicleRegNumber = getVehicleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
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
                
                System.out.println("Recorded out-time for vehicle number: " + ticket.getVehicleRegNumber() + " is:" + outTime+"\n");
                
            }else{
                System.out.println("Unable to update ticket information. Error occurred\n");
            }
        }catch(Exception e){
            logger.error("Unable to process exiting vehicle",e);
        }
    }
}
