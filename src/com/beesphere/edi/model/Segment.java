package com.beesphere.edi.model;

import java.util.List;
import java.util.regex.Pattern;

public interface Segment extends Container {
	Pattern getPattern ();
	List<ValueElement> getElements ();
}
