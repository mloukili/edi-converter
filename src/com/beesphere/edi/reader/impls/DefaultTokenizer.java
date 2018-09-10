package com.beesphere.edi.reader.impls;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beesphere.edi.LangUtils;
import com.beesphere.edi.dialect.Dialect;
import com.beesphere.edi.dialect.impls.DefaultDialect;
import com.beesphere.edi.model.Model;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.edi.reader.Tokenizer;

public class DefaultTokenizer implements Tokenizer {

	private static final long serialVersionUID = -3808921344693611924L;
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultTokenizer.class);
	
	protected static final Dialect DEFAULT_DIALECT = new DefaultDialect ();
	
	protected Reader reader;
	protected StringBuilder sb = new StringBuilder (512);
	protected char[] segmentDelimiter;
	protected String escape;
	
	protected Dialect dialect;
	protected int index = 0;
	protected String curr = null;
	protected String [] fields = null;
	protected int counter = 0;
	
	protected boolean initialized;

	/**
	 * Construct the stream reader.
	 * 
	 * @param ediInputSource
	 *            EDI Stream input source.
	 * @param delimiters
	 *            Segment delimiter String.
	 */
	public DefaultTokenizer () {
		dialect = DEFAULT_DIALECT;
	}
	
	public void read (InputStream is, int n) {
		new InputStreamReader (is);
	}

	@Override
	public void init (InputStream is, Model model) throws ReaderException {
		reader = new InputStreamReader (is);
		if (dialect != null) {
			this.segmentDelimiter = dialect.getSegment().toCharArray();
			this.escape = dialect.getEscape ();
		}
		this.initialized = true;
	}

	@Override
	public int index () {
		return index;
	}

	@Override
	public String curr () {
		return curr;
	}

	@Override
	public String next () throws ReaderException {
		int c = readChar ();
		int delimiterLen = segmentDelimiter.length;
		int escapeLen = escape != null ? escape.length() : 0;

		sb.setLength (0);
		fields = null;
		curr = null;

		// We reached the end of the stream the last time this method was
		// called - see the while loop below...
		if (c == -1) {
			return null;
		}

		// Read the next segment...
		while (c != -1) {
			sb.append((char) c);

			int segLen = sb.length();
			if (segLen >= delimiterLen) {
				boolean reachedSegEnd = true;

				for (int i = 0; i < delimiterLen; i++) {
					char segChar = sb.charAt(segLen - 1 - i);
					char delimChar = segmentDelimiter[delimiterLen - 1 - i];

					if (segChar != delimChar) {
						// Not the end of a segment
						reachedSegEnd = false;
						break;
					}

					// Do not separate segment if escape character occurs.
					if (segLen - 1 - i - escapeLen > -1 && escape != null) {
						String escapeString = sb.substring(segLen
								- 1 - i - escapeLen, segLen - 1 - i);
						if (escape.equals(escapeString)) {
							sb = sb.delete(segLen - 1 - i
									- escapeLen, segLen - 1 - i);
							reachedSegEnd = false;
							break;
						}
					}

				}

				// We've reached the end of a segment...
				if (reachedSegEnd) {
					// Trim off the delimiter and break out...
					sb.setLength(segLen - delimiterLen);
					break;
				}
			}

			c = readChar ();
		}

		curr = sb.toString().trim();

		if (curr.equals(LangUtils.EMPTY)) {
			return next ();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("[" + curr + "]");
		}

		index++;

		return curr;
	}

	@Override
	public String [] fields () {
		if (fields == null) {
			fields = EDIUtils.split (curr,
					dialect.getComposite(), dialect.getEscape());

			if (fields == null) {
				return null;
			}
			
			// If the segment delimiter is a LF, strip off any preceeding CR
			// characters...
			if (dialect.getSegment().equals ("\n")) {
				int endIndex = fields.length - 1;
				if (fields [endIndex].endsWith("\r")) {
					int stringLen = fields [endIndex].length();
					fields [endIndex] = fields [endIndex]
							.substring(0, stringLen - 1);
				}
			}
		}
		
		return fields;
	}

	@Override
	public boolean isInitialized () throws ReaderException {
		return initialized;
	}
	
	protected int readChar () throws ReaderException {
		try {
			int c = reader.read ();
			counter++;
			return c;
		} catch (IOException e) {
			throw new ReaderException (e);
		}
	}
	
	protected char [] read (int n) throws ReaderException {
		char [] chars = new char [n];
		try {
			reader.read (chars);
			return chars;
		} catch (IOException e) {
			throw new ReaderException (e);
		}
	}

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

}
