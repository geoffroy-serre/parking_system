package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * Save Ticket information in DB
	 * @param ticket
	 * @return
	 */
	public boolean saveTicket(Ticket ticket){
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			//ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			//ps.setInt(1,ticket.getId());
			ps.setInt(1,ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			Timestamp timeStamp = Timestamp.valueOf(ticket.getInTime());
			ps.setTimestamp(4, timeStamp);
			ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (timeStamp) );
			return ps.execute();
		}catch (Exception ex){
			logger.error("Error fetching next available slot",ex);
		}finally {
			dataBaseConfig.closeConnection(con);

		}
		return false;
	}

	/**
	 * Retrieve ticket from DB for the given vehicleRegNumber
	 * @param vehicleRegNumber
	 * @return
	 */
	public Ticket getTicketToGetOut(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;

		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET_TO_GET_OUT);
			// PARKING_NUMBER,ID, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1,vehicleRegNumber);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
				//ticket.setOutTime(rs.getTimestamp(5).toLocalDateTime());
			}
			else if (!rs.next()){
				System.out.println("No vehicle to exit with this registration number\n");
			}

			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("Error fetching next available slot",e);

		}
		finally {
			dataBaseConfig.closeConnection(con);

		}
		return ticket;

	}

	/**
	 * Update ticket in DB with calculated price and outTime
	 * @param ticket
	 * @return
	 */
	public boolean updateTicket(Ticket ticket) {

		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			//was ticket.getintime()
			Timestamp timeStamp = Timestamp.valueOf(ticket.getOutTime());
			ps.setTimestamp(2, timeStamp);
			ps.setInt(3,ticket.getId());
			ps.execute();
			return true;
		}catch (Exception ex){
			logger.error("Error saving ticket info",ex);
		}finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
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
	public Boolean isKnownRegistrationInParking(String reg, Boolean vehicleIsOut)  {

		Connection con = null;
		Boolean result = null;
		try {
			if (!vehicleIsOut) {
				con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.VERIFY_REG_IN_DB_OUTTIME_NULL);
				ps.setString(1, reg);
				ResultSet rs = ps.executeQuery();
				result = rs.next();	
			}

			else {
				con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.VERIFY_REG_IN_DB_OUTTIME_NOT_NULL);
				ps.setString(1,  reg);
				ResultSet rs = ps.executeQuery();

				result = rs.next();	
			}
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("Error to know if the registration is known",e);
		}
		return result;


	}
}
