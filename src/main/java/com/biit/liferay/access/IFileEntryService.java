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
