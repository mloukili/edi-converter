package com.beesphere.edi.model.impl.xsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beesphere.edi.EdiNames;
import com.beesphere.edi.EdiXsdUtils;
import com.beesphere.edi.model.Container;
import com.beesphere.edi.model.Element;
import com.beesphere.edi.model.Group;
import com.beesphere.edi.model.ValueElement;
import com.beesphere.edi.model.impl.basic.CompositeImpl;
import com.beesphere.edi.model.impl.basic.FieldImpl;
import com.beesphere.edi.model.impl.basic.GroupImpl;
import com.beesphere.edi.model.impl.basic.ModelImpl;
import com.beesphere.edi.model.impl.basic.SegmentImpl;
import com.beesphere.edi.reader.ReaderException;
import com.beesphere.xsd.XsdInvalidEntityException;
import com.beesphere.xsd.XsdTypeNotFoundException;
import com.beesphere.xsd.model.XsdElement;
import com.beesphere.xsd.model.XsdManagedEntity;
import com.beesphere.xsd.model.XsdSchema;
import com.beesphere.xsd.model.XsdSet;
import com.beesphere.xsd.util.XsdUtils;

public class XsdModel extends ModelImpl {
	
	private static final long serialVersionUID = 7518439019951983939L;
	
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger (XsdModel.class);  

	public XsdModel (XsdSchema schema) throws ReaderException {
		super ();
		if (schema.getRootElement () == null) {
			try {
				XsdUtils.organize (schema, false);
			} catch (XsdInvalidEntityException ieex) {
				throw new ReaderException (ieex);
			} catch (XsdTypeNotFoundException tnfex) {
				throw new ReaderException (tnfex);
			}
		}
		readSchema (schema);
	}
	
	private void readSchema (XsdSchema schema) throws ReaderException {
		XsdElement xsdRoot = schema.getRootElement ();
		if (xsdRoot.getExtraAttribute(EdiNames.AGENCY) == null
				|| xsdRoot.getExtraAttribute(EdiNames.RELEASE) == null
				|| xsdRoot.getExtraAttribute(EdiNames.STANDARD) == null) {
			throw new ReaderException ("agency, release or standard not found in your model.");
		}
		agency = xsdRoot.getExtraAttribute(EdiNames.AGENCY).getValue();
		release = xsdRoot.getExtraAttribute(EdiNames.RELEASE).getValue();
		standard = xsdRoot.getExtraAttribute(EdiNames.STANDARD).getValue();

		if (xsdRoot.getExtraAttribute(EdiNames.DERIVATION) != null) {
			derivation = xsdRoot.getExtraAttribute(EdiNames.DERIVATION).getValue();
		}
		Element element = processElement (xsdRoot);
		if (Group.class.isAssignableFrom (element.getClass ())) {
			root = (Group)element;
		}
	}

	private Element processElement (XsdElement entity) {
		Element element = null;
		if (EdiXsdUtils.isGroup (entity)) {
			element = new GroupImpl (entity.getName ());
		} else if (EdiXsdUtils.isSegment (entity)) {
			SegmentImpl segment = new SegmentImpl (entity.getName ());
			segment.setTruncatable (true);
			element = segment;
		} else if (EdiXsdUtils.isComposite (entity) || EdiXsdUtils.isSubComposite (entity)) {
			CompositeImpl composite = new CompositeImpl (entity.getName ());
			composite.setTruncatable (true);
			element = composite;
		} else {
			element = new FieldImpl (entity.getName ());
		}
		int defMaxOccurs = -1;
		if (element instanceof CompositeImpl || element instanceof FieldImpl) {
			defMaxOccurs = 1;
		}
		element.setMinOccurs (EdiXsdUtils.getMinOccurs(entity.getMinOccurs (), 0));
		element.setMaxOccurs (EdiXsdUtils.getMaxOccurs(entity.getMaxOccurs (), defMaxOccurs));
		
		// get complex type set
		if (entity.getComplexType() != null) {
			XsdSet set = entity.getComplexType().getSet ();
			if (set != null) {
				XsdManagedEntity subEntity = null;
				for (int i = 0; i < set.count (); i++) {
					subEntity = set.get (i);
					if (entity instanceof XsdElement) {
						Element subElement = processElement ((XsdElement)subEntity);
						if (element instanceof GroupImpl) {
							if (Container.class.isAssignableFrom (subElement.getClass ())) {
								((GroupImpl)element).addContainer ((Container)subElement);
							}
						} else if (element instanceof SegmentImpl) {
							if (ValueElement.class.isAssignableFrom (subElement.getClass ())) {
								((SegmentImpl)element).addElement ((ValueElement)subElement);
							}
						} else if (element instanceof CompositeImpl) {
							if (ValueElement.class.isAssignableFrom (subElement.getClass ())) {
								((CompositeImpl)element).addElement ((ValueElement)subElement);
							}
						} 
					}
				}
			}
		}
		return element;
	}
	
}
