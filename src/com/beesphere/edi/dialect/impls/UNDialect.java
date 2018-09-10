package com.beesphere.edi.dialect.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beesphere.edi.dialect.AbstractDialect;
import com.beesphere.edi.reader.ReaderException;

public class UNDialect extends AbstractDialect {

	private static final long serialVersionUID = 6550040679416883737L;
	
	private static Logger logger = LoggerFactory.getLogger(UNDialect.class);
	
	public void create (char [] specLine) throws ReaderException {
		if (!(specLine [0] == 'U' && specLine [1] == 'N')) {
			throw new ReaderException (
					"EDIFACT interchange must begin with UN");
		}
		if (!(specLine [0] == 'U' && specLine [1] == 'N')) {
			throw new ReaderException (
					"EDIFACT interchange must begin with UN");
		}

		boolean fieldDetermined = false;
		boolean compositeDetermined = false;
		boolean segmentDetermined = false;

		if (specLine [2] == 'A') {
			// UNA......
			// 012345678
			field = String.valueOf (specLine [3]);
			fieldDetermined = true;
			composite = String.valueOf (specLine [4]);
			compositeDetermined = true;
			segment = String.valueOf (specLine [8]);
			segmentDetermined = true;
		}

		if (!fieldDetermined || !compositeDetermined || !segmentDetermined) {
			processUNB (specLine, compositeDetermined, fieldDetermined,
					segmentDetermined);
		}
	}

	private void processUNB (char[] specLine, boolean compositeDetermined,
			boolean fieldDetermined, boolean segmentDetermined) 
			throws ReaderException {

		// UNB+UNOA...
		// 01234567

		if (specLine [2] != 'B')
			throw new ReaderException (
					"Required UNB segment not found in EDIFACT interchange");

		switch (specLine [7]) {

		case 'B':
			if (!compositeDetermined) {
				composite = String.valueOf ('\u001D');
				compositeDetermined = true;
			}

			if (!fieldDetermined) {
				field = String.valueOf ('\u001F');
				fieldDetermined = true;
			}

			if (!segmentDetermined) {
				segment = String.valueOf ('\u001C');
				segmentDetermined = true;
			}
			// Deliberately fall into the sequence below
		case 'A':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
			if (!compositeDetermined) {
				composite = String.valueOf ('+');
			}

			if (specLine [3] != composite.charAt (0))
				throw new ReaderException(
						"Expected data element separator after UNB segment tag");

			if (!segmentDetermined)
				segment = String.valueOf ('\'');

			if (!fieldDetermined)
				field = String.valueOf (':');

			break;

		default:
			throw new ReaderException(
					"Unknown Syntax Identifier in UNB segment: "
							+ new String (specLine, 4, 4));
		}
	}

	public static void main (String [] args) throws ReaderException {
		String UN_SPEC = 
			"UNB+UNOA:3+5425007009989:14+5400102000086:14+080722:1404+08072214045381++SUPBELGINV04'";
			//"UNA:+.? '";
		UNDialect desc = new UNDialect ();
		desc.create (UN_SPEC.toCharArray ());
		logger.debug (desc.toString ());
	}

}
