package com.beesphere.edi.reader.tests;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.impls.ReaderOutputHandler;
import com.beesphere.edi.reader.impls.XsdBasedReader;
import com.beesphere.edi.validation.ValidationException;
import com.beesphere.xsd.XsdParserException;

public class ReadWithXsdBasedReader {

	public static void main(String[] args) throws ReaderException, XsdParserException, IOException, ValidationException {
		
		InputStream xsd = new FileInputStream ("files/837.xsd");
		
		String [] xmlns = {
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
			"xmlns:edi=\"http://www.beesphere.net/xsds/2008/edi\""
		};
		
		ReaderOutputHandler roh = new ReaderOutputHandler (new FileWriter ("files/837.xml"), "edi");
		roh.setXmlns (xmlns);

		new XsdBasedReader (xsd).read (new FileInputStream ("files/837.edi"), roh);		
	}
}
