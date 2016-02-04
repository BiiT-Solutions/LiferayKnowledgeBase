package com.biit.liferay.model;

import java.util.List;

import com.biit.usermanager.entity.IElement;

public interface IArticle<ArticleId> extends IElement<ArticleId> {
	String getUniqueName();

	Long getResourcePrimKey();

	String getTitle();

	String getContent();

	String getDescription();
	
	List<String> getSections();
}
