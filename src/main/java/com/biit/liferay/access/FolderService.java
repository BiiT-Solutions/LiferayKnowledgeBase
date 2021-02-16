package com.biit.liferay.access;

import java.io.IOException;
import java.util.ArrayList;
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
		Set<IFolder<Long>> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<DLFolder>>() {
		});
		return myObjects;
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
