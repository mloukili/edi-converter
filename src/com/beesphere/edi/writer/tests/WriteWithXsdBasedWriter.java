package com.beesphere.edi.writer.tests;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.beesphere.edi.dialect.impls.DefaultDialect;
import com.beesphere.edi.writer.WriterException;
import com.beesphere.edi.writer.impls.WriterOutputHandler;
import com.beesphere.edi.writer.impls.XsdBasedWriter;

public class WriteWithXsdBasedWriter {

	public static void main(String[] args) throws WriterException, IOException {
		new XsdBasedWriter (new FileInputStream ("files/837.xsd")).write (
			new FileInputStream ("files/837.xml"), 
			new WriterOutputHandler (new FileWriter ("files/837.gen"), DefaultDialect.DEFAULT)
		);
	}

}
