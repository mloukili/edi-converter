package com.beesphere.edi.writer;

import com.beesphere.edi.ProcessingException;

public class WriterException extends ProcessingException {

	private static final long serialVersionUID = 445615871551158801L;

	public WriterException () {
		super ();
	}

	public WriterException (String message) {
		super (message);
	}

	public WriterException (Throwable th) {
		super (th);
	}

	public WriterException (String message, Throwable th) {
		super (message, th);
	}

}