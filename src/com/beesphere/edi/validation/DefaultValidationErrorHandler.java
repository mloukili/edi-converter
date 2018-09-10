package com.beesphere.edi.validation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * A default error handler which just stores all the errors 
 * so they can be reported or transformed.
 * 
 * @author BeeSphere Team
 * 
 */
public class DefaultValidationErrorHandler implements ValidationErrorHandler {
    private List<SAXParseException> warnings = new ArrayList<SAXParseException> (1);
    private List<SAXParseException> errors = new ArrayList<SAXParseException> (1);
    private List<SAXParseException> fatalErrors = new ArrayList<SAXParseException> (1);

    public void warning (SAXParseException e) throws SAXException {
        warnings.add (e);
    }

    public void error (SAXParseException e) throws SAXException {
        errors.add(e);
    }

    public void fatalError (SAXParseException e) throws SAXException {
        fatalErrors.add (e);
    }

    public void reset () {
        warnings.clear ();
        errors.clear ();
        fatalErrors.clear ();
    }

    public boolean isValid () {
        return errors.isEmpty () && fatalErrors.isEmpty ();
    }

	public void handleErrors (InputStream is, Schema schema)
			throws ValidationException {
        if (!isValid()) {
        	throw new SchemaValidationException (is, schema, fatalErrors, errors, warnings);
        }
	}
}
