package com.beesphere.edi.dialect.impls;

import com.beesphere.edi.dialect.AbstractDialect;
import com.beesphere.edi.dialect.Dialect;

public class DefaultDialect extends AbstractDialect {

	private static final long serialVersionUID = -1447002244160336460L;
	
	public static final Dialect DEFAULT = new DefaultDialect ();
	
	public DefaultDialect () {
	}

	public DefaultDialect (char [] delimeters) {
		if (delimeters == null || delimeters.length <= 0) {
			return;
		}
		segment = String.valueOf (delimeters [0]);
		if (delimeters.length > 1) {
			composite = String.valueOf (delimeters [1]);
		}
		if (delimeters.length > 2) {
			field = String.valueOf (delimeters [2]);
		}
		if (delimeters.length > 3) {
			subField = String.valueOf (delimeters [3]);
		}
	}

	@Override
	public void create (char [] specLine) {
	}

}
