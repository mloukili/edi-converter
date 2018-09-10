package com.beesphere.edi.model.impl.basic;

import java.util.ArrayList;
import java.util.List;

import com.beesphere.edi.model.Container;
import com.beesphere.edi.model.Group;

public class GroupImpl extends ContainerImpl implements Group {

	private static final long serialVersionUID = 7794421679577803571L;

	private List<Container> containers;

	public GroupImpl (String name) {
		super (name);
	}
	
	public GroupImpl () {
	}
	
	public List<Container> getContainers() {
		return containers;
	}

	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}
	
	public void addContainer (Container container) {
		if (containers == null) {
			containers = new ArrayList<Container> ();
		}
		containers.add (container);
	}

}
