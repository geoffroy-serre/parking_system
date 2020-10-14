package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TicketDAO {

  private static final Logger logger = LogManager.getLogger("TicketDAO");

  public DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * Save Ticket information in DB.
   * 
   * @param ticket Ticket
   * @return boolean saveticket
   **/
  public boolean saveTicket(Ticket ticket) {
    Connection con = null;

    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
      ps.setInt(1, ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      Timestamp timeStamp = Timestamp.valueOf(ticket.getInTime());
      ps.setTimestamp(4, timeStamp);
      ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (timeStamp));
      return ps.execute();

    } catch (ClassNotFoundException e) {
      logger.error(
          "Error fetching next available slot. Class not Found in TicketDao.saveTicket()",
          e);

    } catch (SQLException e) {
      logger.error(
          "Error fetching next available slot. SQL Exception in TicketDao.saveTicket()",
          e);
    } finally {
      dataBaseConfig.closeConnection(con);
    }

    return false;
  }

  /**
   * Retrieve ticket from DB for the given vehicleRegNumber.
   * 
   * @param vehicleRegNumber String
   * @return Ticket getTicketToGetOut
   */
  public Ticket getTicketToGetOut(String vehicleRegNumber) {
    Connection con = null;
    Ticket ticket = null;

    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con
          .prepareStatement(DBConstants.GET_TICKET_TO_GET_OUT);
      ps.setString(1, vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1),
            ParkingType.valueOf(rs.getString(6)), false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(2));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble(3));
        ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
        
      } else if (!rs.next()) {
        System.out
            .println("No vehicle to exit with this registration number");
        
        
      }
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
      
    } catch (ClassNotFoundException e) {
      logger.error(
          "Error fetching next available slot. Class not found in TicketDAO getTicketToGetOut()",
          e);

    } catch (SQLException e) {
      logger.error(
          "Error fetching next available slotin TicketDAO getTicketToGetOut()",
          e);
    }  finally {
      dataBaseConfig.closeConnection(con);

    }
    return ticket;

  }

  /**
   * Update ticket in DB with calculated price and outTime.
   * 
   * @param ticket Ticket
   * @return boolean updateTicket
   */
  public boolean updateTicket(Ticket ticket) {

    Connection con = null;

    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
      ps.setDouble(1, ticket.getPrice());
      // was ticket.getintime()
      Timestamp timeStamp = Timestamp.valueOf(ticket.getOutTime());
      ps.setTimestamp(2, timeStamp);
      ps.setInt(3, ticket.getId());
      ps.execute();
      return true;
    } catch (ClassNotFoundException e) {
      logger.error(
          "Error saving ticket info. Class not found in TicketDao updateTicket()",
          e);
    } catch (SQLException e) {
      logger.error(
          "Error saving ticket info. SQL Exception in TicketDAO updateTicket()",
          e);
    } finally {
      dataBaseConfig.closeConnection(con);
    }

    return false;
  }

  /**
   * Look in DB if a Car already have a ticket with the choosen parameters Set
   * True if you look for the vehicle is out Set False if you look for the
   * vehicle is still in Return true if the given informations are found at
   * least one time.
   * 
   * @param reg String Vehicle Registration
   * @return boolean isKnownRegistrationInParking
   **/
  public boolean isKnownRegistrationInParking(String reg) {

    Connection con = null;
    boolean result = false;

    try {

      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con
          .prepareStatement(DBConstants.VERIFY_REG_IN_DB_OUTTIME_NULL);
      ps.setString(1, reg);
      ResultSet rs = ps.executeQuery();
      result = rs.next();
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    } catch (ClassNotFoundException e) {
      logger.error(
          "Error to know if the registration is known.Class not found in TicketDAO.isKnownRegistrationInParking()",
          e);
    } catch (SQLException e) {
      logger.error(
          "Error to know if the registration is known. SQL Exception in isKnownRegistrationInParking()",
          e);
    } finally {
      dataBaseConfig.closeConnection(con);

    }
    return result;

  }

  /**
   * Use to know if the registration had already been used in and out of the
   * parking.
   * 
   * @param reg Vehicle Registration
   * @return Boolean isRecurringRegistration
   **/
  public boolean isRecurringRegistration(String reg) {

    Connection con = null;
    boolean result = false;
    try {

      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con
          .prepareStatement(DBConstants.VERIFY_REG_IN_DB_OUTTIME_NOT_NULL);
      ps.setString(1, reg);
      ResultSet rs = ps.executeQuery();

      result = rs.next();
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    } catch (ClassNotFoundException e) {
      logger.error(
          "Error knowing if the registration is known.Class not found in TicketDAO.isKnownRegistrationInParking()",
          e);
    } catch (SQLException e) {
      logger.error(
          "Error to know if the registration is known. SQL Exception in isKnownRegistrationInParking()",
          e);
    } finally {
      dataBaseConfig.closeConnection(con);

    }
    return result;

  }
}
