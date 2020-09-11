package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.Period;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

       // int inHour = ticket.getInTime().getHours();
       // int outHour = ticket.getOutTime().getHours();

        //TODO: Some tests are failing here. Need to check if this logic is correct
       // int duration = outHour - inHour;
       long diff  = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes();
          System.out.println("----------------"+diff+"---------------");

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice((diff/60d) * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice((diff/60d) * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}