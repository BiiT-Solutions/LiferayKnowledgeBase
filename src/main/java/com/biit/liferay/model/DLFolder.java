package com.biit.liferay.model;

/*-
 * #%L
 * Access to Liferay Knowledge Base
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Calendar;

public class DLFolder implements IFolder<Long> {
    private long companyId;
    private Calendar createDate;
    private int defaultFileEntryTypeId;
    private String description;
    private long folderId;
    private long groupId;
    private boolean hidden;
    private Calendar lastPostDate;
    private Calendar modifiedDate;
    private boolean mountPoint;
    private String name;
    private boolean overrideFileEntryTypes;
    private long parentFolderId;
    private long repositoryId;
    private int status;
    private long statusByUserId;
    private String statusByUserName;
    private Calendar statusDate;
    private String treePath;
    private long userId;
    private String userName;
    private String uuid;

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

    public int getDefaultFileEntryTypeId() {
        return defaultFileEntryTypeId;
    }

    public void setDefaultFileEntryTypeId(int defaultFileEntryTypeId) {
        this.defaultFileEntryTypeId = defaultFileEntryTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Calendar getLastPostDate() {
        return lastPostDate;
    }

    public void setLastPostDate(Calendar lastPostDate) {
        this.lastPostDate = lastPostDate;
    }

    public Calendar getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Calendar modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public boolean isMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(boolean mountPoint) {
        this.mountPoint = mountPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOverrideFileEntryTypes() {
        return overrideFileEntryTypes;
    }

    public void setOverrideFileEntryTypes(boolean overrideFileEntryTypes) {
        this.overrideFileEntryTypes = overrideFileEntryTypes;
    }

    public long getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStatusByUserId() {
        return statusByUserId;
    }

    public void setStatusByUserId(long statusByUserId) {
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

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
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

    @Override
    public String getUniqueName() {
        return treePath;
    }

    @Override
    public Long getUniqueId() {
        return folderId;
    }

    @Override
    public String toString() {
        return getUniqueName();
    }
}
