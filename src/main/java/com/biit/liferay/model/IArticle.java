package com.biit.liferay.model;

import com.biit.usermanager.entity.IElement;

public interface IArticle<ArticleId> extends IElement<ArticleId> {
	String getUniqueName();

	Long getResourcePrimKey();

	String getTitle();
}
