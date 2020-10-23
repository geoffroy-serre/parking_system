package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DbConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParkingSpotDao {
  private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

  public DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * Get the next available parking slot for the current vehicle type.
   * 
   * @param parkingType Type of parking depending vehicle type
   * @return int Parking Spot
   * 
   **/
  public int getNextAvailableSlot(ParkingType parkingType) {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    int result = 0;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.GET_NEXT_PARKING_SPOT);
      ps.setString(1, parkingType.toString());
      rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getInt(1);
      }

    } catch (ClassNotFoundException ex) {
      logger.error(
          "Error updating parking info Class not found in ParkingSpotDAO getNetAvailableSlot()",
          ex);

    } catch (SQLException ex) {
      logger.error(
          "Error updating parking info. Problem with SQL in ParkingSpotDAO getNetAvailableSlot()",
          ex);

    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return result;
  }

  /**
   * Watch for availability slots.
   * 
   * @param parkingType Type of vehicle
   * @return boolean is Available
   * 
   */
  public boolean isThereAvailableSlot(ParkingType parkingType) {
    Connection con = null;
    boolean availability = false;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.GET_NEXT_PARKING_SPOT);
      ps.setString(1, parkingType.toString());
      rs = ps.executeQuery();
      rs.next();

      if (rs.getInt(1) > 0) {
        availability = true;
      }

    } catch (ClassNotFoundException ex) {
      logger.error(
          "Error updating parking info Class not found in ParkingSpotDAO isThereAvailableSlot",
          ex);

    } catch (SQLException ex) {
      logger.error(
          "Error updating parking info. Problem with SQL in ParkingSpotDAO isThereAvailableSlot",
          ex);

    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return availability;

  }

  /**
   * Update parking slot availabiility.
   * 
   * @param parkingSpot Describing the parking spot
   * @return boolean updated Parking
   **/
  public boolean updateParking(ParkingSpot parkingSpot) {
    // update the availability for that parking slot
    Connection con = null;
    boolean result = false;
    PreparedStatement ps = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DbConstants.UPDATE_PARKING_SPOT);
      ps.setBoolean(1, parkingSpot.isAvailable());
      ps.setInt(2, parkingSpot.getId());
      int updateRowCount = ps.executeUpdate();

      result = updateRowCount == 1;
      dataBaseConfig.closePreparedStatement(ps);
    } catch (ClassNotFoundException ex) {
      logger.error(
          "Error updating parking info Class not found in ParkingSpotDAO updateParking()",
          ex);
      return false;
    } catch (SQLException ex) {
      logger.error(
          "Error updating parking info. Problem with SQL in ParkingSpotDAO updateParking()",
          ex);
      return false;

    } finally {
      dataBaseConfig.closeConnection(con);
      dataBaseConfig.closePreparedStatement(ps);
    }
    return result;

  }

}
