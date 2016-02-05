package com.biit.liferay.model;

import java.util.Calendar;
import java.util.List;

import com.biit.usermanager.entity.IElement;

public interface IArticle<ArticleId> extends IElement<ArticleId> {
	String getUniqueName();

	Long getResourcePrimKey();

	String getTitle();

	String getContent();

	String getDescription();

	List<String> getSections();

	void setSections(List<String> sections);

	int getViewCount();

	int getVersion();

	void setTitle(String title);

	void setDescription(String description);

	void setContent(String content);

	Calendar getCreateDate();
}
