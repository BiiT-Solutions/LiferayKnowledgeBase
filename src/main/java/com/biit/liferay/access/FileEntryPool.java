package com.biit.liferay.access;

import com.biit.liferay.access.config.KnowledgeBaseConfigurationReader;
import com.biit.liferay.model.IFileEntry;
import com.biit.utils.pool.SimplePool;

public class FileEntryPool extends SimplePool<Long, IFileEntry<Long>> {

	private static FileEntryPool instance = new FileEntryPool();

	public static FileEntryPool getInstance() {
		return instance;
	}

	@Override
	public boolean isDirty(IFileEntry<Long> element) {
		return false;
	}

	@Override
	public long getExpirationTime() {
		return KnowledgeBaseConfigurationReader.getInstance().getFileEntryPoolExpirationTime();
	}

}
