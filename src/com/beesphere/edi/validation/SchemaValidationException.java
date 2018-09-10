package com.beesphere.edi.validation;

import java.io.InputStream;
import java.util.List;

import javax.xml.validation.Schema;

import org.xml.sax.SAXParseException;

public class SchemaValidationException extends ValidationException {

	private static final long serialVersionUID = -1056598753564532116L;

    public SchemaValidationException(InputStream is,
			Schema schema, List<SAXParseException> fatalErrors,
			List<SAXParseException> errors, List<SAXParseException> warnings) {
        super (message(schema, fatalErrors, errors, warnings));
	}

    protected static String message(Schema schema, List<SAXParseException> fatalErrors,
                                    List<SAXParseException> errors, List<SAXParseException> warnings) {
    	StringBuilder sb = new StringBuilder ("Validation failed for: " + schema);
        if (!fatalErrors.isEmpty()) {
            sb.append(" Fatal Errors: ");
            writeErrorsList(sb, errors);
        }
        if (!errors.isEmpty()) {
            sb.append(" Errors: ");
            writeErrorsList(sb, errors);
        }
        if (!warnings.isEmpty()) {
            sb.append(" Warnings: ");
            writeErrorsList(sb, errors);
        }
        String res = sb.toString();
        sb.setLength (0);
        sb = null;
        return res;
    }
    
    private static void writeErrorsList (StringBuilder sb, List<SAXParseException> exs) {
    	for (Exception ex : exs) {
        	sb.append (ThrowableUtils.toString (ex));
    	}
    }
    
}

/*
 * XsdSchemaValidator.java
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.validation.Validator;
import java.io.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
class XsdSchemaValidator {
  private static int errorCount = 0;
  public static void main(String[] a) {
    if (a.length<2) {
      System.out.println("Usage:");
      System.out.println("java XsdSchemaValidator schema_file_name "
        + "xml_file_name");
    } else {
      String schemaName = a[0];
      String xmlName = a[1];
      Schema schema = loadSchema(schemaName);
      validateXml(schema, xmlName);
    }
  }
  public static void validateXml(Schema schema, String xmlName) {
    try {
      // creating a Validator instance
      Validator validator = schema.newValidator();

      // setting my own error handler
      validator.setErrorHandler(new MyErrorHandler());

      // preparing the XML file as a SAX source
      SAXSource source = new SAXSource(
        new InputSource(new java.io.FileInputStream(xmlName)));

      // validating the SAX source against the schema
      validator.validate(source);
      System.out.println();
      if (errorCount>0) {
        System.out.println("Failed with errors: "+errorCount);
      } else {
        System.out.println("Passed.");
      } 

    } catch (Exception e) {
      // catching all validation exceptions
      System.out.println();
      System.out.println(e.toString());
    }
  }
  public static Schema loadSchema(String name) {
    Schema schema = null;
    try {
      String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
      SchemaFactory factory = SchemaFactory.newInstance(language);
      schema = factory.newSchema(new File(name));
    } catch (Exception e) {
      System.out.println(e.toString());
    }
    return schema;
  }
  private static class MyErrorHandler implements ErrorHandler {
    public void warning(SAXParseException e) throws SAXException {
       System.out.println("Warning: "); 
       printException(e);
    }
    public void error(SAXParseException e) throws SAXException {
       System.out.println("Error: "); 
       printException(e);
    }
    public void fatalError(SAXParseException e) throws SAXException {
       System.out.println("Fattal error: "); 
       printException(e);
    }
    private void printException(SAXParseException e) {
      errorCount++;
      System.out.println("   Line number: "+e.getLineNumber());
      System.out.println("   Column number: "+e.getColumnNumber());
      System.out.println("   Message: "+e.getMessage());
      System.out.println();
    }
  }
}
*/
