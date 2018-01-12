package com.biit.liferay.access;

import com.biit.liferay.access.config.KnowledgeBaseConfigurationReader;
import com.biit.liferay.model.IRepository;
import com.biit.utils.pool.SimplePool;

public class FileRepositoryPool extends SimplePool<Long, IRepository<Long>> {

	private static FileRepositoryPool instance = new FileRepositoryPool();

	public static FileRepositoryPool getInstance() {
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
