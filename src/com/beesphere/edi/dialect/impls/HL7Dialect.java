package com.beesphere.edi.dialect.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beesphere.edi.dialect.AbstractDialect;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.impls.ReaderErrors;

public class HL7Dialect extends AbstractDialect {

	private static final long serialVersionUID = 6550040679416883737L;
	
	private static Logger logger = LoggerFactory.getLogger(HL7Dialect.class);
	
	public void create (char [] specLine) throws ReaderException {
		if (!(specLine[0] == 'M' && specLine[1] == 'S' && specLine[2] == 'H'))
			throw new ReaderException (ReaderErrors.X12_MISSING_ISA);

		// MSH|
		// ...| (offset 3)
		composite = String.valueOf (specLine[3]);
		field = String.valueOf (specLine[4]);
		subField = String.valueOf (specLine[5]);
		segment = String.valueOf ('\n');

	}

	public static void main (String [] args) throws ReaderException {
		// Chars:    ^~\&
		String HL7_SPEC = 
			"MSH|^~\\&|DDTEK LAB|ELAB-1|DDTEK OE|BLDG14|200502150930||ORU^R01^ORU_R01|CTRL-9876|P|2.4";
		HL7Dialect desc = new HL7Dialect ();
		desc.create (HL7_SPEC.toCharArray ());
		logger.debug (desc.toString ());
	}

}
