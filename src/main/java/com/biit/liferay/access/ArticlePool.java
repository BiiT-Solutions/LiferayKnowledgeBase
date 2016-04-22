package com.biit.liferay.access;

import java.util.Enumeration;
import java.util.Hashtable;

import com.biit.liferay.model.IArticle;

public class ArticlePool {

	protected final static long EXPIRATION_TIME = 3600000;// 60 minutes

	private Hashtable<Long, Long> insertionTime; // element id --> time
	private Hashtable<Long, IArticle<Long>> articles; // Roles by user.

	private static ArticlePool instance = new ArticlePool();

	public static ArticlePool getInstance() {
		return instance;
	}

	private ArticlePool() {
		reset();
	}

	public void reset() {
		insertionTime = new Hashtable<Long, Long>();
		articles = new Hashtable<Long, IArticle<Long>>();
	}

	public void addArticle(IArticle<Long> article) {
		if (article != null) {
			insertionTime.put(article.getId(), System.currentTimeMillis());
			articles.put(article.getId(), article);
		}
	}

	public void removeArticle(long kbArticleId) {
		insertionTime.remove(kbArticleId);
		articles.remove(kbArticleId);
	}

	public IArticle<Long> getArticleByResourceKey(long resourceKey) {
		long now = System.currentTimeMillis();
		Long kbArticleId = null;
		if (insertionTime.size() > 0) {
			Enumeration<Long> e = insertionTime.keys();
			while (e.hasMoreElements()) {
				kbArticleId = e.nextElement();
				if ((now - insertionTime.get(kbArticleId)) > EXPIRATION_TIME) {
					// object has expired
					removeArticle(kbArticleId);
					kbArticleId = null;
				} else {
					if (articles.get(kbArticleId).getResourcePrimKey().equals(resourceKey)) {
						return articles.get(kbArticleId);
					}
				}
			}
		}
		return null;
	}

}
