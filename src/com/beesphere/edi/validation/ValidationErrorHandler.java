package com.beesphere.edi.validation;

import java.io.InputStream;

import javax.xml.validation.Schema;

import org.xml.sax.ErrorHandler;

/**
 * Validation error handler.
 *
 * @version $Revision: 659760 $
 */
public interface ValidationErrorHandler extends ErrorHandler {

    /**
     * Resets any state within this error handler
     */
    void reset();

    /**
     * Process any errors which may have occurred during validation
     *
     * @param invoker the invoker
     * @param message the invoker message
     * @param schema   the schema
     * @param result   the result
     */
    void handleErrors(InputStream is, Schema schema) throws ValidationException;
}
