package com.beesphere.edi.writer.impls;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.beesphere.edi.EdiNames;
import com.beesphere.edi.EdiXsdUtils;
import com.beesphere.edi.writer.WriterException;
import com.beesphere.xsd.XsdInvalidEntityException;
import com.beesphere.xsd.XsdParser;
import com.beesphere.xsd.XsdParserException;
import com.beesphere.xsd.XsdTypeNotFoundException;
import com.beesphere.xsd.model.XsdElement;
import com.beesphere.xsd.model.XsdSchema;
import com.beesphere.xsd.model.XsdSet;
import com.beesphere.xsd.util.XsdUtils;

public class XsdBasedWriter extends AbstractWriter {

	private static final long serialVersionUID = 8906169652814250789L;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger (XsdBasedWriter.class);   

	private static final String ELEMENTS = "elements";
	
	private Map<String, XsdElement> entities = new HashMap<String, XsdElement> ();
	
	public XsdBasedWriter () throws WriterException {
		super ();
	}
	
	@SuppressWarnings("unchecked")
	public XsdBasedWriter (XsdSchema schema) throws WriterException {
		super ();
		if (schema.getRootElement () == null) {
			try {
				XsdUtils.organize (schema, false);
			} catch (XsdInvalidEntityException ieex) {
				throw new WriterException (ieex);
			} catch (XsdTypeNotFoundException tnfex) {
				throw new WriterException (tnfex);
			}
		}
		setSchema (schema);
	}
	
	public XsdBasedWriter (InputStream xsd) throws WriterException {
		super ();
		XsdParser parser;
		try {
			parser = new XsdParser ();
			parser.parse (xsd);
		} catch (XsdParserException e) {
			throw new WriterException (e);
		}
		XsdSchema schema = parser.getSchema ();
		if (schema.getRootElement () == null) {
			try {
				XsdUtils.organize (schema, false);
			} catch (XsdInvalidEntityException ieex) {
				throw new WriterException (ieex);
			} catch (XsdTypeNotFoundException tnfex) {
				throw new WriterException (tnfex);
			}
		}
		setSchema (schema);
	}
	
	@Override
	protected XsdElement pick (Element element) {
		return entities.get (getNodeName(element));
	}
	
	@SuppressWarnings("unchecked")
	public void setSchema (XsdSchema schema) {
		entities = (Map<String, XsdElement>)schema.getProperty (ELEMENTS);
		if (entities == null) {
			entities = new HashMap<String, XsdElement> ();
			mapEntity (schema.getRootElement ());
		}
	}
	
	private void mapEntity (XsdElement element) {
		if (element.getExtraAttribute (EdiNames.QUALIFIER) != null || EdiXsdUtils.isField (element)) {
			entities.put (element.getName (), element);
		} 
		if (element instanceof XsdElement) {
			XsdElement el = (XsdElement)element;
			if (el.getComplexType () != null && el.getComplexType ().getSet () != null) {
				XsdSet set = el.getComplexType ().getSet ();
				for (int i = 0; i < set.count (); i++) {
					mapEntity ((XsdElement)set.get (i));
				}
			}
		}
	}

}
