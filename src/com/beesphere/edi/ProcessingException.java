package com.beesphere.edi;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = 445615871551158801L;

	public ProcessingException () {
		super ();
	}

	public ProcessingException (String message) {
		super (message);
	}

	public ProcessingException (Throwable th) {
		super (th);
	}

	public ProcessingException (String message, Throwable th) {
		super (message, th);
	}

}