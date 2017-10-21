package com.biit.liferay.access;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.exceptions.FolderNotDeletedException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IFolder;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface IFolderService {

	boolean deleteFolder(IFolder<Long> folder) throws ClientProtocolException, IOException, NotConnectedToWebServiceException, AuthenticationRequired,
			FolderNotDeletedException;

	IFolder<Long> getFolder(long folderId) throws JsonParseException, JsonMappingException, IOException, NotConnectedToWebServiceException,
			WebServiceAccessError, AuthenticationRequired;

	IFolder<Long> addFolder(long groupId, long repositoryId, boolean monuntPoint, long parentFolderId, String name, String description, IGroup<Long> site)
			throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError;

}
