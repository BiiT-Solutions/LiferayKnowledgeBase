package com.biit.liferay.access;

import com.biit.liferay.access.config.KnowledgeBaseConfigurationReader;
import com.biit.liferay.model.IArticle;
import com.biit.utils.pool.SimplePool;

public class ArticlePool extends SimplePool<Long, IArticle<Long>> {

	private static ArticlePool instance = new ArticlePool();

	public static ArticlePool getInstance() {
		return instance;
	}

	@Override
	public boolean isDirty(IArticle<Long> element) {
		return false;
	}

	@Override
	public long getExpirationTime() {
		return KnowledgeBaseConfigurationReader.getInstance().getArticlePoolExpirationTime();
	}

}
