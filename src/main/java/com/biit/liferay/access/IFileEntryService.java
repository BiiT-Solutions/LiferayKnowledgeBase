package com.biit.liferay.access;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;

import com.biit.liferay.access.exceptions.DocumentNotDeletedException;
import com.biit.liferay.access.exceptions.DuplicatedFileException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.IFileEntry;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;

public interface IFileEntryService {

	IFileEntry<Long> geFileEntry(long fileEntryId) throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
			WebServiceAccessError;

	String getFileRelativeUrl(long fileEntryId) throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
			WebServiceAccessError;

	/**
	 * Can add a document or media to a site. Errors only returns the title of
	 * the document but no information.
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
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws NotConnectedToWebServiceException
	 * @throws AuthenticationRequired
	 * @throws WebServiceAccessError
	 * @throws DuplicatedFileException 
	 */
	IFileEntry<Long> addFile(long repositoryId, long folderId, String sourceFileName, String mimeType, String title, String description, String changeLog,
			File file) throws ClientProtocolException, IOException, NotConnectedToWebServiceException, AuthenticationRequired, WebServiceAccessError, DuplicatedFileException;

	void deleteFile(IFileEntry<Long> fileEntry) throws DocumentNotDeletedException, NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired;

	Set<IFileEntry<Long>> getFileEntries(long repositoryId, long folderId) throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
			AuthenticationRequired;

}
