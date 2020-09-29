package com.parkit.parkingsystem.customexceptions;

public class RegIsAlreadyParkedException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3076680978320246995L;

	public RegIsAlreadyParkedException(String errorMessage) {
		super(errorMessage);
	}

}
