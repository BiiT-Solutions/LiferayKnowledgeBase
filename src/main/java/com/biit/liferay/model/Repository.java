package com.biit.liferay.model;

import java.util.Calendar;

public class Repository implements IRepository<Long> {
	private long classNameId;

	private long companyId;

	private Calendar createDate;

	private String description;

	private long dlFolderId;

	private long groupId;

	private Calendar modifiedDate;

	private String name;

	private String portletId;

	private long repositoryId;

	private String typeSettings;

	private long userId;

	private String userName;

	private String uuid;

	@Override
	public String getUniqueName() {
		return getName();
	}

	@Override
	public Long getUniqueId() {
		return getRepositoryId();
	}

	public long getClassNameId() {
		return classNameId;
	}

	public void setClassNameId(long classNameId) {
		this.classNameId = classNameId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDlFolderId() {
		return dlFolderId;
	}

	public void setDlFolderId(long dlFolderId) {
		this.dlFolderId = dlFolderId;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPortletId() {
		return portletId;
	}

	public void setPortletId(String portletId) {
		this.portletId = portletId;
	}

	public long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(long repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getTypeSettings() {
		return typeSettings;
	}

	public void setTypeSettings(String typeSettings) {
		this.typeSettings = typeSettings;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
