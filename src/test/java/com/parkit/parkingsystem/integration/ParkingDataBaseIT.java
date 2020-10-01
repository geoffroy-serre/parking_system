package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.customexceptions.RegIsAlreadyParkedException;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception{
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
		//ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		//parkingService.processIncomingVehicle();

	}

	@AfterAll
	private static void tearDown(){

	}

	@Test
	public void test_Registration_can_be_only_present_one_time_if_active() throws Exception{
		//TODO: check that we can't have two time the same registration in the same time in the parking lot.

		ParkingService parkingService;

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		//parkingService.processIncomingVehicle();
		assertThrows(RegIsAlreadyParkedException.class, () -> parkingService.processIncomingVehicle());

	}

	@Test
	public void test_Parking_A_Car() throws Exception{
		//TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		Connection con = dataBaseTestConfig.getConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM ticket");
		assertTrue(rs.next());
		assertEquals("1", rs.getString("ID"));
		assertEquals(1, rs.getInt("PARKING_NUMBER"));
		assertEquals("ABCDEF", rs.getString("VEHICLE_REG_NUMBER"));
		assertEquals(0d, rs.getDouble("PRICE"));
		assertTrue(rs.getString("IN_TIME") != null);
		assertTrue(rs.getString("OUT_TIME") == null);

		ResultSet rsp = st.executeQuery("SELECT * FROM parking");
		assertTrue(rsp.next());
		assertEquals(0, rsp.getInt("AVAILABLE"));
	}

	@Test
	public void test_Car_Parking_Lot_Exit() throws Exception{
		//TODO: check that the fare generated and out time are populated correctly in the database and the availibity is set
		//test_Parking_A_Car();
		/*
		 * A pause is needed betwen incoming et exiting processes to avoid bug due to
		 * the quick in out.
		 */
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		Thread.sleep(500);

		parkingService.processExitingVehicle();

		Connection con = dataBaseTestConfig.getConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM ticket");
		assertTrue(rs.next());
		assertEquals("1", rs.getString("ID"));
		assertEquals(1, rs.getInt("PARKING_NUMBER"));
		assertEquals("ABCDEF", rs.getString("VEHICLE_REG_NUMBER"));
		assertEquals(0d, rs.getDouble("PRICE"));
		assertTrue(rs.getString("IN_TIME") != null);
		assertTrue(rs.getString("OUT_TIME") != null);

		ResultSet rsp = st.executeQuery("SELECT * FROM parking");
		assertTrue(rsp.next());
		assertEquals(1, rsp.getInt("AVAILABLE"));

	}

	@Test
	public void test_reducted_price_for_recurrents_customers() {
		
		
	}
	@Test
	public void test_Registration_must_be_in_to_exit() {
		
	}
	
	@Test
	public void test_exit_update_current_ticket_not_previous() {
		
	}
}
