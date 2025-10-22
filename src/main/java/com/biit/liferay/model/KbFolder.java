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

public class KbFolder implements IFolder<Long> {
    public static final String FOLDER_LIFERAY_CLASSNAME = "com.liferay.knowledgebase.model.KBFolder";

    private Long companyId;
    private Calendar createDate;
    private String description;
    private Long groupId;
    private Long kbFolderId;
    private Calendar modifiedDate;
    private String name;
    private Long parentKBFolderId;
    private String urlTitle;
    private Long userId;
    private String userName;
    private String uuid;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getKbFolderId() {
        return kbFolderId;
    }

    public void setKbFolderId(Long kbFolderId) {
        this.kbFolderId = kbFolderId;
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

    public Long getParentKBFolderId() {
        return parentKBFolderId;
    }

    public void setParentKBFolderId(Long parentKBFolderId) {
        this.parentKBFolderId = parentKBFolderId;
    }

    public String getUrlTitle() {
        return urlTitle;
    }

    public void setUrlTitle(String urlTitle) {
        this.urlTitle = urlTitle;
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
    public String getUniqueName() {
        return getUrlTitle() + "_" + getKbFolderId();
    }

    @Override
    public Long getUniqueId() {
        return getKbFolderId();
    }

    @Override
    public String toString() {
        return getUniqueName();
    }

}
