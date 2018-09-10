package com.beesphere.edi.dialect.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beesphere.edi.dialect.AbstractDialect;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.impls.ReaderErrors;

public class X12Dialect extends AbstractDialect {

	private static final long serialVersionUID = 6550040679416883737L;
	
	private static Logger logger = LoggerFactory.getLogger(X12Dialect.class);
	
	public void create (char [] specLine) throws ReaderException {
		if (!(specLine[0] == 'I' && specLine[1] == 'S' && specLine[2] == 'A'))
			throw new ReaderException (ReaderErrors.X12_MISSING_ISA);

		// ISA*
		// ...^ (offset 3)
		composite = String.valueOf (specLine[3]);

		if (specLine[84] == 'U') {
			if (isAcceptable(specLine[107]))
				field = String.valueOf (specLine[107]);
			if (isAcceptable (specLine[108]))
				segment = String.valueOf (specLine[108]);
		} else {
			if (isAcceptable(specLine[104]))
				field = String.valueOf (specLine[104]);
			if (isAcceptable(specLine[105]))
				segment = String.valueOf (specLine[105]);
		}

	}

	protected boolean isAcceptable(char c) {
		if (Character.isDigit(c) || Character.isLetter(c))
			return false;
		return composite.charAt (0) != c;
	}

	protected static String findTerminatorSuffix(char[] buf, int i, int j) {
		StringBuffer result = new StringBuffer();
		for (int n = i; n < j && !Character.isLetter(buf[n]); n++)
			result.append(buf[n]);
		return result.toString();
	}
	
	public static void main (String [] args) throws ReaderException {
		String X12_SPEC = 
			"ISA*00*          *00*          *ZZ*SENDER         *ZZ*RECEIVER       *990914*0244*U*00401*100007098*0*P*:~";
		X12Dialect desc = new X12Dialect ();
		desc.create (X12_SPEC.toCharArray ());
		logger.debug (desc.toString ());
	}

}
