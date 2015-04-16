package com.biit.liferay.access.exceptions;

public class ArticleNotDeletedException extends Exception {
	private static final long serialVersionUID = 5514879587695238968L;

	public ArticleNotDeletedException(String message) {
		super(message);
	}
}
