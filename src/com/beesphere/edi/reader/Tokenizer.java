package com.beesphere.edi.reader;

import java.io.InputStream;
import java.io.Serializable;

import com.beesphere.edi.dialect.Dialect;
import com.beesphere.edi.model.Model;

public interface Tokenizer extends Serializable {
	void       init (InputStream in, Model model) throws ReaderException;
	String     curr ();
	String     next () throws ReaderException;
	int        index ();
	String  [] fields () throws ReaderException;
	boolean    isInitialized () throws ReaderException;
	Dialect    getDialect ();
}
