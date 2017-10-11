package com.biit.liferay.access;

import com.biit.liferay.access.config.KnowledgeBaseConfigurationReader;
import com.biit.liferay.model.IRepository;
import com.biit.utils.pool.SimplePool;

public class RepositoryPool extends SimplePool<Long, IRepository<Long>> {

	private static RepositoryPool instance = new RepositoryPool();

	public static RepositoryPool getInstance() {
		return instance;
	}

	@Override
	public boolean isDirty(IRepository<Long> element) {
		return false;
	}

	@Override
	public long getExpirationTime() {
		return KnowledgeBaseConfigurationReader.getInstance().getFileEntryPoolExpirationTime();
	}

}
