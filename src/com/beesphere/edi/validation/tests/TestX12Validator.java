package com.beesphere.edi.validation.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.validation.SchemaValidator;
import com.beesphere.edi.validation.ValidationException;
import com.beesphere.xsd.XsdParserException;

public class TestX12Validator {
	public static void main(String[] args) throws ReaderException, XsdParserException, IOException, ValidationException {
		
		InputStream is = new FileInputStream ("files/837.xml");
		InputStream xsd = new FileInputStream ("files/837.xsd");
		
		SchemaValidator.validate (is, xsd);

	}

}
