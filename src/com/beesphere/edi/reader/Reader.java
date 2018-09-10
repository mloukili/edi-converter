package com.beesphere.edi.reader;

import java.io.InputStream;
import java.io.Serializable;

import com.beesphere.edi.OutputHandler;

public interface Reader extends Serializable {
	void read (InputStream is, OutputHandler outputHandler) throws ReaderException;
}
