package com.beesphere.edi.writer;

import java.io.InputStream;
import java.io.Serializable;

import com.beesphere.edi.OutputHandler;

public interface Writer extends Serializable {
	void write (InputStream is, OutputHandler outputHandler) throws WriterException;
}
