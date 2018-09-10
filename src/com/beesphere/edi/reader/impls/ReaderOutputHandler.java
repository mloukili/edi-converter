package com.beesphere.edi.reader.impls;

import java.io.IOException;
import java.io.Writer;

import com.beesphere.edi.LangUtils;
import com.beesphere.edi.OutputHandler;
import com.beesphere.edi.model.Element;
import com.qlogic.commons.utils.strings.StringUtils;

public class ReaderOutputHandler implements OutputHandler {
	
	private static final long serialVersionUID = -1824828401879918801L;
	
	private boolean xmlnsSet;
		
	protected boolean omitXmlDeclaration = true;
	protected boolean ignoreEmpty;
	protected String namespace;
	
	protected String [] xmlns;
	
	protected Writer out;
	protected StringBuilder buffer;
	
	public ReaderOutputHandler (Writer out, String namespace, boolean ignoreEmpty) {
		this.out = out;
		this.namespace = namespace;
		this.ignoreEmpty = ignoreEmpty;
	}

	public ReaderOutputHandler (Writer out, String namespace) {
		this (out, namespace, true);
	}

	public ReaderOutputHandler (Writer out, boolean ignoreEmpty) {
		this (out, null, ignoreEmpty);
	}

	public ReaderOutputHandler (Writer out) {
		this (out, true);
	}

	@Override
	public void onStart (Element element, Kind kind) throws IOException {
		if (kind.equals (Kind.DOCUMENT)) {
			if (buffer == null) {
				buffer = new StringBuilder ();
			}
			if (!omitXmlDeclaration) {
				out.append (LangUtils.XML_HEADER);
			}
		} else {
			out.append (LangUtils.LESS);
			if (namespace != null) {
				out.append (namespace).append (LangUtils.COLON);
			}
			out.append (element.getName ());
			if (!xmlnsSet) {
				if (xmlns != null && xmlns.length > 0) {
					for (String xn : xmlns) {
						out.append (LangUtils.SPACE);
						out.append (xn);
					}
				}
				xmlnsSet = true;
			}
			out.append (LangUtils.GREATER);
		}
	}
	
	@Override
	public void onEnd (Element element, Kind kind) throws IOException {
		if (element != null) {
			out.append (LangUtils.LESS_SLASH);
			if (namespace != null) {
				out.append (namespace).append (LangUtils.COLON);
			}
			out.append (element.getName ()).append (LangUtils.GREATER);
			out.flush ();
		}
	}

	@Override
	public void onData (Element element, Kind kind, String value) throws IOException {
		if (kind.equals (OutputHandler.Kind.FIELD) && value != null) {
			if (value.equals (LangUtils.EMPTY)) {
				if (!ignoreEmpty) {
					onStart (element, kind);   out.append (escapeXML (value));   onEnd (element, kind);
				} 
			} else {
				onStart (element, kind);   out.append (escapeXML (value));   onEnd (element, kind);
			}
		}
	}

	private String escapeXML (String str) {
        return StringUtils.escapeXML (buffer, str);
    }  

	public boolean isIgnoreEmpty() {
		return ignoreEmpty;
	}

	public void setIgnoreEmpty(boolean ignoreEmpty) {
		this.ignoreEmpty = ignoreEmpty;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String[] getXmlns() {
		return xmlns;
	}

	public void setXmlns(String[] xmlns) {
		this.xmlns = xmlns;
	}

	public boolean isOmitXmlDeclaration() {
		return omitXmlDeclaration;
	}

	public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
		this.omitXmlDeclaration = omitXmlDeclaration;
	}

	public boolean isXmlnsSet() {
		return xmlnsSet;
	}

	public void setXmlnsSet(boolean xmlnsSet) {
		this.xmlnsSet = xmlnsSet;
	}

	public Writer getOut() {
		return out;
	}

	public void setOut(Writer out) {
		this.out = out;
	}

	public StringBuilder getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuilder buffer) {
		this.buffer = buffer;
	}

}
