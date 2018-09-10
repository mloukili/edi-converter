package com.beesphere.edi.model;

import java.util.List;

public interface Composite extends ValueElement {
	List<ValueElement> getElements ();
}
