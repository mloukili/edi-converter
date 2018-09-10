package com.beesphere.edi.model.impl.basic;

import java.util.ArrayList;
import java.util.List;

import com.beesphere.edi.model.Composite;
import com.beesphere.edi.model.ValueElement;

public class CompositeImpl extends ValueElementImpl implements Composite {

	private static final long serialVersionUID = 2055833064139048664L;
	
	private List<ValueElement> elements;

	public CompositeImpl (String name) {
		super (name);
	}
	
	public CompositeImpl () {
	}
	
	public List<ValueElement> getElements() {
		return elements;
	}

	public void setElements(List<ValueElement> elements) {
		this.elements = elements;
	}

	public void addElement (ValueElement element) {
		if (elements == null) {
			elements = new ArrayList<ValueElement> ();
		}
		elements.add (element);
	}

}
