package com.beesphere.edi.model.impl.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.beesphere.edi.model.Segment;
import com.beesphere.edi.model.ValueElement;

public class SegmentImpl extends ContainerImpl implements Segment {

	private static final long serialVersionUID = -6855798599019576384L;

	private Pattern pattern;
	private List<ValueElement> elements;
	
	public SegmentImpl (String name) {
		super (name);
		createPattern (name);
	}
	
	public SegmentImpl () {
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
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
	
	@Override 
	public void setName (String name) {
		super.setName (name);
		createPattern (name);
	}
	
	private void createPattern (String name) {
		if (name != null) {
			pattern = Pattern.compile("^" + name, Pattern.DOTALL);
		}
	}

}
