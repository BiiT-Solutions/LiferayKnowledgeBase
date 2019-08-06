package com.biit.liferay.access;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.exceptions.FolderNotDeletedException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IFolder;
import com.biit.usermanager.entity.IElement;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface IArticleFolderService {

	boolean deleteFolder(IFolder<Long> folder) throws ClientProtocolException, IOException,
			NotConnectedToWebServiceException, AuthenticationRequired, FolderNotDeletedException, WebServiceAccessError;

	IFolder<Long> getFolder(long folderId) throws JsonParseException, JsonMappingException, IOException,
			NotConnectedToWebServiceException, WebServiceAccessError, AuthenticationRequired;

	IFolder<Long> addFolder(Long groupId, Long parentResourcePrimKey, Long parentResourceClassNameId, String name,
			String description, IGroup<Long> site) throws ClientProtocolException, NotConnectedToWebServiceException,
			IOException, AuthenticationRequired, WebServiceAccessError;

	Integer getFoldersCount(Long groupId, Long parentKBFolderId, IGroup<Long> site)
			throws ClientProtocolException, IOException, NotConnectedToWebServiceException, AuthenticationRequired;

	IFolder<Long> getFolder(String urlFolder, Long groupId, Long parentKBFolderId)
			throws JsonParseException, JsonMappingException, IOException, NotConnectedToWebServiceException,
			WebServiceAccessError, AuthenticationRequired;

	IElement<Long> getFolderClassName() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
			AuthenticationRequired, WebServiceAccessError;
}
