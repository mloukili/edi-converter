package com.beesphere.edi.validation;

public class ValidationException extends Exception {
	
	private static final long serialVersionUID = -2141648246885725829L;

    public ValidationException (String message) {
        super (message);
    }
    
    public ValidationException(Throwable cause) {
        super (cause);
    }

    public ValidationException(String message, Throwable cause) {
        super (message, cause);
    }

}

