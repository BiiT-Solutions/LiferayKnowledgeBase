package com.biit.liferay.access;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.FolderNotDeletedException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.DLFolder;
import com.biit.liferay.model.IFolder;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class FolderService extends ServiceAccess<IFolder<Long>, DLFolder> implements IFolderService {

    @Override
    public Set<IFolder<Long>> decodeListFromJson(String json, Class<DLFolder> objectClass) throws IOException {
        Set<DLFolder> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<DLFolder>>() {
        });
        return new HashSet<>(myObjects);
    }

    @Override
    public IFolder<Long> addFolder(long groupId, long repositoryId, boolean mountPoint, long parentFolderId, String name, String description, IGroup<Long> site)
            throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("groupId", Long.toString(groupId)));
        params.add(new BasicNameValuePair("repositoryId", Long.toString(repositoryId)));
        params.add(new BasicNameValuePair("mountPoint", Boolean.toString(mountPoint)));
        params.add(new BasicNameValuePair("parentFolderId", Long.toString(parentFolderId)));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("description", description));
        params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getUniqueId())));

        String result = getHttpPostResponse("dlfolder/add-folder", params);

        if (result != null) {
            // A Simple JSON Response Read
            IFolder<Long> folder = decodeFromJson(result, DLFolder.class);
            FolderPool.getInstance().addElement(folder);
            return folder;
        }
        return null;
    }

    @Override
    public IFolder<Long> getFolder(long folderId) throws IOException, NotConnectedToWebServiceException,
            WebServiceAccessError, AuthenticationRequired {
        IFolder<Long> folder = FolderPool.getInstance().getElement(folderId);
        if (folder != null) {
            return folder;
        }

        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("folderId", Long.toString(folderId)));

        String result = getHttpPostResponse("dlfolder/get-folder", params);

        LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

        if (result != null) {
            // A Simple JSON Response Read
            folder = decodeFromJson(result, DLFolder.class);
            FolderPool.getInstance().addElement(folder);
            return folder;
        }
        return null;
    }

    @Override
    public boolean deleteFolder(IFolder<Long> folder) throws ClientProtocolException, IOException, NotConnectedToWebServiceException, AuthenticationRequired,
            FolderNotDeletedException {
        if (folder != null) {
            checkConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("folderId", folder.getUniqueId() + ""));

            String result = getHttpPostResponse("dlfolder/delete-folder", params);

            if (result == null || result.length() < 3) {
                LiferayClientLogger.info(this.getClass().getName(), "Folder '" + folder.getUniqueName() + "' deleted.");
                FolderPool.getInstance().removeElement(folder.getUniqueId());
                return true;
            } else {
                throw new FolderNotDeletedException("Folder '" + folder.getUniqueName() + "' (id:" + folder.getUniqueId() + ") not deleted correctly. ");
            }
        }
        return false;
    }

    @Override
    public void reset() {
        FolderPool.getInstance().reset();
    }
}
