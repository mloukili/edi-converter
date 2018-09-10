package com.beesphere.edi.model;

import java.util.List;

public interface Group extends Container {
	List<Container> getContainers ();
}
