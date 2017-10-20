package com.biit.liferay.access;

import com.biit.liferay.access.config.KnowledgeBaseConfigurationReader;
import com.biit.liferay.model.IFolder;
import com.biit.utils.pool.SimplePool;

public class FolderPool extends SimplePool<Long, IFolder<Long>> {

	private static FolderPool instance = new FolderPool();

	public static FolderPool getInstance() {
		return instance;
	}

	@Override
	public boolean isDirty(IFolder<Long> element) {
		return false;
	}

	@Override
	public long getExpirationTime() {
		return KnowledgeBaseConfigurationReader.getInstance().getFolderPoolExpirationTime();
	}

}
