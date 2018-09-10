package com.beesphere.edi;

import java.io.IOException;
import java.io.Serializable;

import com.beesphere.edi.model.Element;

public interface OutputHandler extends Serializable {

	enum Kind {
		DOCUMENT, GROUP, SEGMENT, COMPOSITE, SUB_COMPOSITE, FIELD, SUB_FIELD
	};
	
	void onStart (Element element, Kind kind) throws IOException;
	void onEnd (Element element, Kind kind) throws IOException;
	void onData (Element element, Kind kind, String value) throws IOException;

}
