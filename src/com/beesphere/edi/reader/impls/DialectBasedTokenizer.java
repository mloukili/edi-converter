package com.beesphere.edi.reader.impls;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beesphere.edi.dialect.Dialect;
import com.beesphere.edi.dialect.impls.HL7Dialect;
import com.beesphere.edi.dialect.impls.UNDialect;
import com.beesphere.edi.dialect.impls.X12Dialect;
import com.beesphere.edi.model.Model;
import com.beesphere.edi.reader.ReaderException;

public class DialectBasedTokenizer extends DefaultTokenizer {

	private static final long serialVersionUID = -4247521630021744115L;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger (DialectBasedTokenizer.class);   
	
	private static final String X12 = "X12";
	private static final String UN  = "UN";
	private static final String HL7 = "HL7";
	
	private static Map<String, Dialect> DIALECTS = new HashMap<String, Dialect> ();
	
	static {
		DIALECTS.put (X12, new X12Dialect ());
		DIALECTS.put (UN, new UNDialect ());
		DIALECTS.put (HL7, new HL7Dialect ());
	}
	
	private char [] specLine;
	
	@Override
	public void init (InputStream is, Model model) throws ReaderException {
		reader = new InputStreamReader (is);
		dialect = DIALECTS.get (model.getAgency ());
		if (dialect == null) {
			dialect = DIALECTS.get (X12);
		}
		specLine = read (128);
		dialect.create (specLine);
		this.segmentDelimiter = dialect.getSegment().toCharArray();
		this.escape = dialect.getEscape ();
		this.initialized = true;
	}

	protected int readChar () throws ReaderException {
		if (specLine != null && counter < specLine.length) {
			return specLine [counter++];
		}
		try {
			int c = reader.read ();
			counter++;
			return c;
		} catch (IOException e) {
			throw new ReaderException (e);
		}
	}
	
}
