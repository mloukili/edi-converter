package com.beesphere.edi.writer.impls;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beesphere.edi.OutputHandler;
import com.beesphere.edi.dialect.Dialect;
import com.beesphere.edi.dialect.impls.DefaultDialect;
import com.beesphere.edi.model.Element;

public class WriterOutputHandler implements OutputHandler {
	
	private static final long serialVersionUID = -1824828401879918801L;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger (WriterOutputHandler.class);  
	
	protected Writer out;
	
	protected Dialect dialect; 
	
	public WriterOutputHandler (Writer out, Dialect dialect) {
		this.out = out;
		if (dialect == null) {
			dialect = DefaultDialect.DEFAULT;
		}
		this.dialect = dialect;
	}

	public WriterOutputHandler (Writer out) {
		this (out, null);
	}

	@Override
	public void onStart (Element element, Kind kind) throws IOException {
		
	}
	
	@Override
	public void onEnd (Element element, Kind kind) throws IOException {
		if (kind.equals (Kind.SEGMENT)) {
			out.append (dialect.getSegment ()).append ("\n");
		} else if (kind.equals (Kind.COMPOSITE)) {
			out.append (dialect.getComposite ()); 
		} else if (kind.equals (Kind.FIELD)) {
			out.append (dialect.getField ());  
		} else if (kind.equals (Kind.SUB_FIELD)) {
			out.append (dialect.getSubField ());  
		}
		out.flush ();
	}

	@Override
	public void onData (Element element, Kind kind, String value) throws IOException {
		if (kind.equals (Kind.SEGMENT)) {
			out.append (value).append (dialect.getComposite ());
		} else if (kind.equals (Kind.FIELD)) {
			out.append (value);
		}
	}

}
