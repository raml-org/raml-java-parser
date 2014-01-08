package org.raml.model;

import org.raml.parser.annotation.Scalar;

public class TraitModel extends Action{

	@Scalar
	String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
