package com.beesphere.edi.validation;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class SchemaValidator {
	
	private static final SchemaFactory DEFAULT_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	
	private static final ValidationErrorHandler DEFAULT_ERROR_HANDLER = new DefaultValidationErrorHandler();
	
	public static void validate (InputStream is, InputStream xsd) throws ValidationException {
		validate (is, xsd, null);
	}

	public static void validate (InputStream is, InputStream xsd, ValidationErrorHandler handler) throws ValidationException {
		
		if (handler == null) {
			handler = DEFAULT_ERROR_HANDLER;
		}
		
		SchemaFactory factory = DEFAULT_FACTORY;

		Schema schema;
		try {
			schema = factory.newSchema (new StreamSource (xsd));
		} catch (SAXException e) {
			throw new ValidationException (e);
		}
		
        Validator validator = schema.newValidator ();
		
        validator.setErrorHandler(handler);
        try {
			validator.validate (new StreamSource (is));
		} catch (Throwable th) {
			throw new ValidationException (th);
		}
        handler.handleErrors (is, schema);
	}

	
}
