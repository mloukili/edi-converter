package com.beesphere.edi.reader.impls;

import java.io.InputStream;

import com.beesphere.edi.model.impl.xsd.XsdModel;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.Tokenizer;
import com.beesphere.xsd.XsdParser;
import com.beesphere.xsd.XsdParserException;

public class XsdBasedReader extends DefaultReader {
	
	private static final long serialVersionUID = -2583563630067165064L;

	public XsdBasedReader (InputStream xsd) throws ReaderException {
		super ();
		parseModel (xsd);
	}
	
	public XsdBasedReader (Tokenizer tokenizer, InputStream xsd) throws ReaderException {
		super (tokenizer);
		parseModel (xsd);
	}
	
	private void parseModel (InputStream xsd) throws ReaderException {
		XsdParser parser;
		try {
			parser = new XsdParser ();
			parser.parse (xsd);
		} catch (XsdParserException e) {
			throw new ReaderException (e);
		}
		
		setModel(new XsdModel (parser.getSchema ()));
	}
	
}
