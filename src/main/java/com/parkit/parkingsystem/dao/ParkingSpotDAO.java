package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParkingSpotDAO {
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
    int result = -1;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con
          .prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
      ps.setString(1, parkingType.toString());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getInt(1);
      }
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
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
    }
    return result;
  }

  /**
   * Update parking slot availabiility.
   * 
   * @param parkingSpot Describing the parking spot
   * @return boolean updated Parking
   **/
  public boolean updateParking(ParkingSpot parkingSpot) {
    // update the availability fo that parking slot
    Connection con = null;
    boolean result = false;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con
          .prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
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
    }
    return result;

  }

}
