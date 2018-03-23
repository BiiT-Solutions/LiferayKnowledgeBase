package com.biit.liferay.model;

import java.util.Calendar;

public class FileEntry implements IFileEntry<Long> {

	private Long fileEntryId;

	private Long classNameId;

	private Long classPK;

	private Long companyId;

	private Calendar createDate;

	private Long custom1ImageId;

	private Long custom2ImageId;

	private String description;

	private String extension;

	private String extraSettings;

	private Long fileEntryTypeId;

	private Long folderId;

	private Long groupId;

	private Long largeImageId;

	private boolean manualCheckInRequired;

	private String mimeType;

	private Calendar modifiedDate;

	private String name;

	private Integer readCount;

	private Long repositoryId;

	private Long size;

	private Long smallImageId;

	private String title;

	private String treePath;

	private Long userId;

	private String userName;

	private String uuid;

	private String version;

	@Override
	public Long getUniqueId() {
		return fileEntryId;
	}

	@Override
	public String toString() {
		return title;
	}

	public Long getFileEntryId() {
		return fileEntryId;
	}

	public void setFileEntryId(Long fileEntryId) {
		this.fileEntryId = fileEntryId;
	}

	public Long getClassNameId() {
		return classNameId;
	}

	public void setClassNameId(Long classNameId) {
		this.classNameId = classNameId;
	}

	public Long getClassPK() {
		return classPK;
	}

	public void setClassPK(Long classPK) {
		this.classPK = classPK;
	}

	@Override
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}

	public Long getCustom1ImageId() {
		return custom1ImageId;
	}

	public void setCustom1ImageId(Long custom1ImageId) {
		this.custom1ImageId = custom1ImageId;
	}

	public Long getCustom2ImageId() {
		return custom2ImageId;
	}

	public void setCustom2ImageId(Long custom2ImageId) {
		this.custom2ImageId = custom2ImageId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getExtraSettings() {
		return extraSettings;
	}

	public void setExtraSettings(String extraSettings) {
		this.extraSettings = extraSettings;
	}

	public Long getFileEntryTypeId() {
		return fileEntryTypeId;
	}

	public void setFileEntryTypeId(Long fileEntryTypeId) {
		this.fileEntryTypeId = fileEntryTypeId;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getLargeImageId() {
		return largeImageId;
	}

	public void setLargeImageId(Long largeImageId) {
		this.largeImageId = largeImageId;
	}

	public boolean isManualCheckInRequired() {
		return manualCheckInRequired;
	}

	public void setManualCheckInRequired(boolean manualCheckInRequired) {
		this.manualCheckInRequired = manualCheckInRequired;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}

	public Long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Long repositoryId) {
		this.repositoryId = repositoryId;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getSmallImageId() {
		return smallImageId;
	}

	public void setSmallImageId(Long smallImageId) {
		this.smallImageId = smallImageId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getUniqueName() {
		return getTitle() + " (" + getUniqueId() + ")";
	}

}
