package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.Ticket;
import java.time.Duration;

public class FareCalculatorService {

  /**
   * Price is calculated in minutes then divided by 60 to know if there is a
   * hour more to pay For example for 1.5 hours fare are for 2hours. Recurring
   * user got 5% discount Free parking for 30mn and less. If it's so, no fare
   * are calculated the ticket keep its default price (0.0)
   * 
   * @param ticket Ticket
   **/
  public void calculateFare(Ticket ticket) {

    TicketDao ticketDao = new TicketDao();

    if ((ticket.getOutTime() == null)
        || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
      throw new IllegalArgumentException("Out time provided is incorrect:"
                                         + ticket.getOutTime().toString());
    }

    double parkedVehicleDuration = Duration
        .between(ticket.getInTime(), ticket.getOutTime()).toMinutes() / 60.0;

    /*
     * 5% Reduction if the vehicle has already use the parking ( in AND out).
     * The price is multiplied by 0.95 to get the discounted price
     */
    if (parkedVehicleDuration > Fare.FREE_HALF_HOUR
        && ticketDao.isRecurringRegistration(ticket.getVehicleRegNumber())) {
      parkedVehicleDuration = Math.ceil(parkedVehicleDuration);
      System.out.println(
          "Welcome back. As a returning user you have 5% discount off !!");
      switch (ticket.getParkingSpot().getParkingType()) {
        case CAR: {
          ticket.setPrice(
              (parkedVehicleDuration * Fare.CAR_RATE_PER_HOUR) * 0.95);
          break;
        }
        case BIKE: {
          ticket.setPrice(
              (parkedVehicleDuration * Fare.BIKE_RATE_PER_HOUR) * 0.95);
          break;
        }
        default:
          throw new IllegalArgumentException("Unkown Parking Type");
      }
      /*
       * If the vehicle didn't parked before ( in AND out) regular fare are
       * applied
       */
    } else if (parkedVehicleDuration > Fare.FREE_HALF_HOUR) {

      parkedVehicleDuration = Math.ceil(parkedVehicleDuration);

      switch (ticket.getParkingSpot().getParkingType()) {
        case CAR: {
          ticket.setPrice(parkedVehicleDuration * Fare.CAR_RATE_PER_HOUR);
          break;
        }
        case BIKE: {
          ticket.setPrice(parkedVehicleDuration * Fare.BIKE_RATE_PER_HOUR);
          break;
        }
        default:
          throw new IllegalArgumentException("Unkown Parking Type");
      }
    }
  }
}