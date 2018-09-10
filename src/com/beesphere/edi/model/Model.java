package com.beesphere.edi.model;

import java.io.Serializable;

public interface Model extends Serializable {
	Group getRoot ();
	String getAgency ();
	String getRelease ();
	String getStandard ();
	String getDerivation ();
}
