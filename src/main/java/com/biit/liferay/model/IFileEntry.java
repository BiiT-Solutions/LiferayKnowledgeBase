package com.biit.liferay.model;

import com.biit.usermanager.entity.IElement;
import com.biit.utils.pool.PoolElement;

public interface IFileEntry<FileEntryId> extends IElement<FileEntryId>, PoolElement<FileEntryId> {

	Long getGroupId();

	Long getFolderId();

	String getTitle();

	String getUuid();

}
