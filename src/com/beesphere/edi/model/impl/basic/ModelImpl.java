package com.beesphere.edi.model.impl.basic;

import com.beesphere.edi.model.Group;
import com.beesphere.edi.model.Model;

public class ModelImpl implements Model {
	
	private static final long serialVersionUID = 6119509235878306519L;

	protected Group root;
	protected String agency;
	protected String release;
	protected String standard;
	protected String derivation;
	
	public Group getRoot() {
		return root;
	}
	public void setRoot(Group root) {
		this.root = root;
	}
	public String getAgency() {
		return agency;
	}
	public void setAgency(String agency) {
		this.agency = agency;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public String getStandard () {
		return standard;
	}
	public void setStandard (String standard) {
		this.standard = standard;
	}
	public String getDerivation() {
		return derivation;
	}
	public void setDerivation(String derivation) {
		this.derivation = derivation;
	}
	
	public String toString () {
		return "Agency '" + agency + "', Release '" + release + "', Standard(" + 
					standard + ")" + (derivation == null ? "" : "-Derivation(" + derivation + ")");
	}
	
}
