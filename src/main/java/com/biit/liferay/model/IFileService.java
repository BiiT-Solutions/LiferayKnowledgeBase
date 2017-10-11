package com.biit.liferay.model;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;

public interface IFileService {

	IFileEntry<Long> geFileDefinition(long fileEntryId) throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			WebServiceAccessError;

	String getFileRelativeUrl(long fileEntryId) throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
			WebServiceAccessError;

	/**
	 * Can add a document or media to a site. Errors only returns the title of
	 * the document but no informaiton.
	 * 
	 * @param repositoryId
	 * @param folderId
	 * @param sourceFileName
	 * @param mimeType
	 * @param title
	 *            Must be unique.
	 * @param description
	 * @param changeLog
	 * @param file
	 * @param siteName
	 * @param virtualHost
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NotConnectedToWebServiceException
	 * @throws AuthenticationRequired
	 * @throws WebServiceAccessError
	 */
	IFileEntry<Long> addFile(long repositoryId, long folderId, String sourceFileName, String mimeType, String title, String description, String changeLog,
			File file, String siteName, String virtualHost) throws ClientProtocolException, IOException, NotConnectedToWebServiceException,
			AuthenticationRequired, WebServiceAccessError;

}
