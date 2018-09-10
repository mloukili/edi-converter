package com.beesphere.edi.dialect;

import java.io.Serializable;

import com.beesphere.edi.reader.ReaderException;

public interface Dialect extends Serializable {
	String getSegment ();
	String getComposite ();
	String getField ();
	String getSubField ();
	String getEscape ();
	void create (char [] specLine) throws ReaderException;
}
