package com.beesphere.edi.writer.impls;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.beesphere.edi.EdiXsdUtils;
import com.beesphere.edi.OutputHandler;
import com.beesphere.edi.writer.Writer;
import com.beesphere.edi.writer.WriterException;
import com.beesphere.xsd.model.XsdElement;
import com.beesphere.xsd.model.XsdManagedEntity;
import com.beesphere.xsd.model.XsdSet;

public abstract class AbstractWriter implements Writer {
	
	private static final long serialVersionUID = -1445455113755291425L;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger (AbstractWriter.class);   

	private StringBuilder sb = new StringBuilder();
	private static final DocumentBuilderFactory DEFAULT_FACTORY = DocumentBuilderFactory.newInstance();
	protected static final DocumentBuilderFactory factory = DEFAULT_FACTORY;

	protected transient DocumentBuilder builder;
	private OutputHandler outputHandler;

	public AbstractWriter () throws WriterException {
		super ();
		try {
			factory.setNamespaceAware(true);
	        builder = factory.newDocumentBuilder();
		} catch (Throwable e) {
			throw new WriterException (e);
		} 
	}
	
	protected static String getNodeName (Node element) {
		String name = element.getNodeName ();
		String prefix = element.getPrefix();
		if (prefix != null) {
			name = name.substring(name.indexOf(':') + 1);
		}
		return name;
	}

	@Override
	public void write (InputStream is, OutputHandler outputHandler) throws WriterException {
		this.outputHandler = outputHandler;
		try {
			Document document = builder.parse (new InputSource (is));
			XsdElement xsdElement = pick (document.getDocumentElement());
			process (xsdElement, document.getDocumentElement());
		} catch (Throwable th) {
			throw new WriterException (th);
		}
	}
	
	private void process (XsdElement xsdElement, Element element) throws WriterException {
		if (EdiXsdUtils.isSegment (xsdElement)) {
			try {
				outputHandler.onData (null, OutputHandler.Kind.SEGMENT, getNodeName (element));
			} catch (IOException e) {
				throw new WriterException (e);
			}
		}
		XsdSet set = EdiXsdUtils.getSet (xsdElement);
		if (set != null) {
			for (int i = 0; i < set.count (); i++) {
				XsdManagedEntity subXsdEntity = set.get (i);
				if (!(subXsdEntity instanceof XsdElement)) {
					continue;
				}
				List<Element> list = getChildren(element, subXsdEntity.getName ());
				for (Element subElement : list) {
					process ((XsdElement)subXsdEntity, subElement);
				}
				if (i < (set.count () - 1)) {
					try {
						if (EdiXsdUtils.isSegment (xsdElement)) {
							outputHandler.onEnd (null, OutputHandler.Kind.COMPOSITE);
						} else if (EdiXsdUtils.isComposite (xsdElement)) {
							outputHandler.onEnd (null, OutputHandler.Kind.FIELD);
						} else if (EdiXsdUtils.isSubComposite (xsdElement)) {
							outputHandler.onEnd (null, OutputHandler.Kind.SUB_FIELD);
						}
					} catch (IOException e) {
						throw new WriterException (e);
					}
				}
			}
		}
		try {
			if (EdiXsdUtils.isSegment (xsdElement)) {
				outputHandler.onEnd (null, OutputHandler.Kind.SEGMENT);
			} else if (EdiXsdUtils.isField (xsdElement)) {
				outputHandler.onData (null, OutputHandler.Kind.FIELD, getText (element));
			}
		} catch (IOException e) {
			throw new WriterException (e);
		}
	}
	
	private String getText (Element element) {
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				sb.append (node.getNodeValue ());
			}
		}
		String res = sb.toString ();
		sb.setLength (0);
		return res;
	}
    public static List<Element> getChildren (Element element, String tag) {
		NodeList childNodes = element.getChildNodes();
		List<Element> result = new ArrayList<Element> ();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (getNodeName(node).equals (tag) && node.getNodeType () == Node.ELEMENT_NODE) {
				result.add((Element) node);
			}
		}

		return result;
	}
	
	
	protected abstract XsdElement pick (Element element);
	
}
