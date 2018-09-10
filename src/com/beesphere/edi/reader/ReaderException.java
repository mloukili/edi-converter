package com.beesphere.edi.reader;

import com.beesphere.edi.ProcessingException;

public class ReaderException extends ProcessingException {

	private static final long serialVersionUID = 445615871551158801L;

	public ReaderException () {
		super ();
	}

	public ReaderException (String message) {
		super (message);
	}

	public ReaderException (Throwable th) {
		super (th);
	}

	public ReaderException (String message, Throwable th) {
		super (message, th);
	}

}