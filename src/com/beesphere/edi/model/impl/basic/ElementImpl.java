package com.beesphere.edi.model.impl.basic;

import com.beesphere.edi.model.Element;

public class ElementImpl implements Element {
	
	private static final long serialVersionUID = 6875492019316215466L;

	private String name;
	private int minOccurs;
	private int maxOccurs;
	private boolean truncatable;
	
	public ElementImpl (String name) {
		this.name = name;
	}
	
	public ElementImpl () {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMinOccurs() {
		return minOccurs;
	}
	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}
	public int getMaxOccurs() {
		return maxOccurs;
	}
	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
	public boolean isTruncatable() {
		return truncatable;
	}
	public void setTruncatable(boolean truncatable) {
		this.truncatable = truncatable;
	}
}
