package com.beesphere.edi.reader.tests;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.beesphere.edi.model.impl.xsd.XsdModel;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.impls.ReaderOutputHandler;
import com.beesphere.edi.reader.impls.DefaultReader;
import com.beesphere.xsd.XsdParser;
import com.beesphere.xsd.XsdParserException;

public class ReadWithXsdModel {

	public static void main(String[] args) throws ReaderException, XsdParserException, IOException {
		XsdParser parser = new XsdParser ();
		parser.parse (new FileInputStream ("files/837.xsd"));
		
		new DefaultReader ().setModel (new XsdModel (parser.getSchema ())).read (
			new FileInputStream ("files/837.edi"), 
			new ReaderOutputHandler (new FileWriter ("files/837.xml"))
		);
		
	}
	
}
