package com.biit.liferay.model;

import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KbArticle implements IArticle<Long> {

	private Long companyId;
	private String content;
	private Calendar createDate;
	private String description;
	private Long groupId;
	private long kbArticleId;
	private boolean latest;
	private boolean main;
	private Calendar modifiedDate;
	private Long parentResourcePrimKey;
	private Long parentResourceClassNameId;
	private String sourceURL;
	private String urlTitle;
	private int priority;
	private Long resourcePrimKey;
	private Long rootResourcePrimKey;
	private Long kbFolderId;
	// Sections are not used and causes problem with serialization.
	@JsonIgnore
	private List<String> sections;
	@JsonIgnore
	private List<String> selectedFileNames;
	private int status;
	private Long statusByUserId;
	private String statusByUserName;
	private Calendar statusDate;
	private String title;
	private Long userId;
	private String userName;
	private String uuid;
	private int version;
	private int viewCount;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public boolean isLatest() {
		return latest;
	}

	public void setLatest(boolean latest) {
		this.latest = latest;
	}

	public boolean isMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Long getParentResourcePrimKey() {
		return parentResourcePrimKey;
	}

	public void setParentResourcePrimKey(Long parentResourcePrimKey) {
		this.parentResourcePrimKey = parentResourcePrimKey;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public Long getResourcePrimKey() {
		return resourcePrimKey;
	}

	public void setResourcePrimKey(Long resourcePrimKey) {
		this.resourcePrimKey = resourcePrimKey;
	}

	public Long getRootResourcePrimKey() {
		return rootResourcePrimKey;
	}

	public void setRootResourcePrimKey(Long rootResourcePrimKey) {
		this.rootResourcePrimKey = rootResourcePrimKey;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getStatusByUserId() {
		return statusByUserId;
	}

	public void setStatusByUserId(Long statusByUserId) {
		this.statusByUserId = statusByUserId;
	}

	public String getStatusByUserName() {
		return statusByUserName;
	}

	public void setStatusByUserName(String statusByUserName) {
		this.statusByUserName = statusByUserName;
	}

	public Calendar getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Calendar statusDate) {
		this.statusDate = statusDate;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public long getKbArticleId() {
		return kbArticleId;
	}

	public void setKbArticleId(long kbArticleId) {
		this.kbArticleId = kbArticleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (kbArticleId ^ (kbArticleId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KbArticle other = (KbArticle) obj;
		if (kbArticleId != other.kbArticleId)
			return false;
		return true;
	}

	@Override
	public List<String> getSections() {
		return sections;
	}

	@Override
	public void setSections(List<String> sections) {
		this.sections = sections;
	}

	@Override
	public Long getId() {
		return getKbArticleId();
	}

	@Override
	public String getUniqueName() {
		return getKbArticleId() + "";
	}

	@Override
	public String toString() {
		return "(" + getId() + ") " + getContent();
	}

	public Long getKbFolderId() {
		return kbFolderId;
	}

	public void setKbFolderId(Long kbFolderId) {
		this.kbFolderId = kbFolderId;
	}

	public Long getParentResourceClassNameId() {
		return parentResourceClassNameId;
	}

	public void setParentResourceClassNameId(Long parentResourceClassNameId) {
		this.parentResourceClassNameId = parentResourceClassNameId;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public String getUrlTitle() {
		return urlTitle;
	}

	public void setUrlTitle(String urlTitle) {
		this.urlTitle = urlTitle;
	}

	public List<String> getSelectedFileNames() {
		return selectedFileNames;
	}

	public void setSelectedFileNames(List<String> selectedFileNames) {
		this.selectedFileNames = selectedFileNames;
	}

}
