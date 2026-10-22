package com.betacom.veicolijdbc.exceptions;

public class AcademyException extends RuntimeException {

	public AcademyException(String message) {
		super(message);
	}
	
	public AcademyException(String message, Throwable cause) {
		super(message, cause);
	}
}
