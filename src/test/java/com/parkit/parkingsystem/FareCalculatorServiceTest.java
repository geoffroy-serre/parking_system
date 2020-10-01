package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;


public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void calculate_Fare_Car(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 12, 11);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	public void calculate_Fare_Bike(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 12, 11);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice() );
	}

	@Test
	public void calculate_Fare_Unkown_Type(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 12, 11);
		ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculate_Fare_Bike_With_Future_In_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2019, 11, 11, 11, 11);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculate_Fare_Bike_With_Half_Hour_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 11, 41);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0, ticket.getPrice() );
	}

	@Test
	public void calculate_Fare_Car_With_Half_Hour_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 11, 41);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0 , ticket.getPrice());
	}

	@Test
	public void calculate_Fare_Car_With_less_Half_Hour_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 11, 26);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0 , ticket.getPrice());
	}

	@Test
	public void calculate_Fare_Car_With_More_Half_Hour_Less_Hour_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 11, 56);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.CAR_RATE_PER_HOUR , ticket.getPrice());
	}

	@Test
	public void calculate_Fare_Car_With_More_Than_A_Day_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 12, 11, 11);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( 24d * Fare.CAR_RATE_PER_HOUR , ticket.getPrice());
	}

	@Test
	public void calculate_Fare_Car_With_More_Than_A_Hour_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 12, 41);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( 2 * Fare.CAR_RATE_PER_HOUR , ticket.getPrice());
	}
	
	@Test
	public void calculate_Reducted_Fare_Car_With_More_Than_A_Hour_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 12, 41);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( Math.ceil((2 * Fare.CAR_RATE_PER_HOUR)*0.95) , ticket.getPrice());
	}
	
	@Test
	public void calculate_Reducted_Fare_Car_With_Half_Hour_Parking_Time(){
		LocalDateTime inTime = LocalDateTime.of(2020, 11, 11, 11, 11);
		LocalDateTime outTime = LocalDateTime.of(2020, 11, 11, 11, 41);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( 0 , ticket.getPrice());
	}




}
