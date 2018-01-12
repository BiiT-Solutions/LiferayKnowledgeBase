package com.biit.liferay.access;

public enum FileRepositoryName {
	SITE("20");

	private String name;

	private FileRepositoryName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
