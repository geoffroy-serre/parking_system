package com.parkit.parkingsystem.service;

import java.time.Duration;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	/**
	 * Price is calculated in minutes then divided by 60 to know if there is a hour more to pay
	 * For example for 1.5 hours fare are for 2hours.
	 * Free parking for 30mn and less.
	 * @param ticket
	 */
	public void calculateFare(Ticket ticket){
		if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
		}


		double parkedVehicleDuration  = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes()/60.0;


		if (parkedVehicleDuration < 0.5) {
			ticket.setPrice(0);
		}
		else {
			parkedVehicleDuration = Math.ceil(parkedVehicleDuration);
		}


		switch (ticket.getParkingSpot().getParkingType()){
		case CAR: {
			ticket.setPrice(parkedVehicleDuration * Fare.CAR_RATE_PER_HOUR);
			break;
		}
		case BIKE: {
			ticket.setPrice(parkedVehicleDuration * Fare.BIKE_RATE_PER_HOUR);
			break;
		}
		default: throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}