package com.biit.liferay.access;

public enum RepositoryName {
	SITE("20");

	private String name;

	private RepositoryName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
