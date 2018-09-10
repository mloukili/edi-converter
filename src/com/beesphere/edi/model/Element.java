package com.beesphere.edi.model;

import java.io.Serializable;

public interface Element extends Serializable {
	int getMinOccurs ();
	void setMinOccurs (int minOccurs);
	int getMaxOccurs ();
	void setMaxOccurs (int maxOccurs);
	boolean isTruncatable ();
	String getName ();
}
